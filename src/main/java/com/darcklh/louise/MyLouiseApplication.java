package com.darcklh.louise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource(value="classpath:LouiseConfig.properties")
public class MyLouiseApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyLouiseApplication.class, args);
    }

}
