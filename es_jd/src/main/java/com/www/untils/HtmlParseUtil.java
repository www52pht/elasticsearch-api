package com.www.untils;

import com.www.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author Administrator
 */
@Component
public class HtmlParseUtil {
    public static void main(String[] args) throws IOException {
        new HtmlParseUtil().parseID("Linux").forEach(System.out::println);
//        for (Content content : java) {
//            System.out.println(content);
//        }
    }


    public ArrayList<Content> parseID(String keywords) throws IOException {
        //获取请求 https://search.jd.com/Search?keyword=java&wq=java&page=7&s=177&click=1
        //前提，需要联网（还有就是当前的URL是不支持中文的要是搜索中文的话需要在链接上加上&enc=utf-8）
        String url = "https://search.jd.com/Search?keyword=" + keywords + "&wq=" + keywords + "&page=7&s=177&click=1&enc=utf-8";

        //解析网页(jsoup返回的Document就是浏览器的Document对象)
        Document document = Jsoup.parse(new URL(url), 30000);
        //所有你在js中的可以使用的方法，这里的document都可以使用
        Element element = document.getElementById("J_goodsList");
        // System.out.println(element.html());
        //获取所有的li元素
        Elements elements = element.getElementsByTag("li");

        ArrayList<Content> goodlists = new ArrayList<>();

        for (Element el : elements) {
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();

            Content content = new Content();
            content.setImg(img);
            content.setPrice(price);
            content.setTitle(title);

            goodlists.add(content);

        }
        return goodlists;

    }


}
