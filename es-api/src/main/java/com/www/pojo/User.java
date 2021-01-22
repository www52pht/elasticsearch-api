package com.www.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 */
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String namel;
    private Integer age;
}
