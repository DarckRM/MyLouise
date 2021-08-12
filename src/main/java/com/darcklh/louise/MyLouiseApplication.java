package com.darcklh.louise;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@MapperScan("com.darcklh.louise.Mapper")
@PropertySource(value="classpath:myLouise.properties")
public class MyLouiseApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyLouiseApplication.class, args);
    }

}
