package com.www;

import com.alibaba.fastjson.JSON;
import com.sun.org.apache.regexp.internal.RE;
import com.www.config.ElasticsearchClientConfig;
import com.www.pojo.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.UUIDs;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import javax.lang.model.element.VariableElement;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.*;

@SpringBootTest
class EsApiApplicationTests {

    //面向对象来操作
    @Autowired
    @Qualifier("restHighLevelClient")
    RestHighLevelClient client;

    //测试索引的创建
    @Test
    void contextLoads() throws IOException {
        //1、创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("www");
        //2、客户端执行请求indicesClient，请求后获得响应
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }

    //测试获得索引,判断是否存在
    @Test
    void testGetIndexRequest() throws IOException {
        //1、创建索引请求
        GetIndexRequest request = new GetIndexRequest("www");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    //测试删除索引
    @Test
    void testDeleteIndexRequest() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("www");
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }


    //测试添加文档
    @Test
    void testAddDocument() throws IOException {
        //创建对象
        User user = new User("王炜维", 23);
        //创建请求
        IndexRequest request = new IndexRequest("www");
        //规则 put /www/_doc/1
        request.id("1");

        request.timeout(TimeValue.timeValueSeconds(1L));

        //将我们的数据放进请求
        request.source(JSON.toJSONString(user), XContentType.JSON);
        //客户端发送请求,获取响应的结果
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);

        System.out.println(JSON.toJSONString(user));

        System.out.println(response.toString());

        System.out.println(response.getResult());  //对应我们命令的返回状态 CREATED UPDATED
    }

    //测试获得文档,判断是否存在 get /index/_doc/1
    @Test
    void testGetRequest() throws IOException {
        //创建请求
        GetRequest getRequest = new GetRequest("www", "1");

        getRequest.fetchSourceContext(FetchSourceContext.DO_NOT_FETCH_SOURCE);
        getRequest.storedFields("_none_");

        boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println(exists);


    }

    //测试获得文档的信息
    @Test
    void testGetDocument() throws IOException {
        GetRequest getRequest = new GetRequest("www", "1");
        GetResponse documentFields = client.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(documentFields.getSourceAsString());  //打印文档内容
        System.out.println(documentFields); //返回的全部内容是跟命令是一样的
    }

    //更新文档记录
    @Test
    void testUpdate() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("www", "1");

        updateRequest.timeout(TimeValue.timeValueSeconds(1L));

        User user = new User("胖还挺", 20);

        String jsonString = JSON.toJSONString(user);

        updateRequest.doc(jsonString, XContentType.JSON);

        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);

        System.out.println(updateResponse.getResult());

        System.out.println(updateResponse.toString());
    }

    //删除文档记录
    @Test
    void testDelete() throws IOException {
        //创建删除请求
        DeleteRequest deleteRequest = new DeleteRequest("www", "1");

        deleteRequest.timeout(TimeValue.timeValueSeconds(1L));

        DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);

        System.out.println(deleteResponse.getResult());

    }

    //特殊情况，批量插入

    @Test
    void testBulkRequest() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();

        bulkRequest.timeout(TimeValue.timeValueSeconds(10L));

        ArrayList<User> userArrayList = new ArrayList<>();

        userArrayList.add(new User("张三", 15));
        userArrayList.add(new User("李四", 16));
        userArrayList.add(new User("王五", 17));
        userArrayList.add(new User("赵柳", 134));
        userArrayList.add(new User("张三2", 123));
        userArrayList.add(new User("张三4", 154));
        userArrayList.add(new User("张三5", 1545));
        userArrayList.add(new User("张三6", 145));
        userArrayList.add(new User("张三7", 457));
        userArrayList.add(new User("张三8", 123));
        userArrayList.add(new User("张三34", 1523));
        userArrayList.add(new User("kaixin", 1523));


        //批处理请求
        for (int i = 0; i < userArrayList.size(); i++) {

            String s = new Random(100).toString();
            //批量更新和批量删除，就在这里修改对应的请求就可以了
            bulkRequest.add
                    (new IndexRequest("www")
//                            .id(s)
                                    .source(JSON.toJSONString(userArrayList.get(i)), XContentType.JSON)
                    );

        }

        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);

        System.out.println(bulkResponse.hasFailures()); //是否失败,返回false 代表成功

    }


    //查询
    @Test
    void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("www");

        //构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //查询条件，我们可以使用QueryBuilders工具类来实现
        //QueryBuilders.termQuery精确
        //QueryBuilders.matchAllQuery()匹配所有

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("namel", "kaixin");

        // MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();

        sourceBuilder.query(termQueryBuilder);

        //分页
        sourceBuilder.from();
        sourceBuilder.size();

        sourceBuilder.timeout(TimeValue.timeValueSeconds(60L));

        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println(JSON.toJSONString(searchResponse.getHits()));

        System.out.println("===========================");

        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
            System.out.println(documentFields.getSourceAsMap());
        }
    }
}
