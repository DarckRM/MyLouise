package com.darcklh.louise;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Model.Messages.InMessage;
import com.darcklh.louise.Model.Messages.Node;
import com.darcklh.louise.Model.Messages.OutMessage;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Model.Messages.Messages;
import com.darcklh.louise.Service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MyLouiseApplicationTests {

    @Autowired
    TaskService taskService;

    @Test
    void test() {
        System.out.println(taskService.findBy());
    }
}
