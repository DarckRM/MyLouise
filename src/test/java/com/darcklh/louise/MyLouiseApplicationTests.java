package com.darcklh.louise;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Mapper.UploadInfoDao;
import com.darcklh.louise.Mapper.UserDao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

@SpringBootTest
class MyLouiseApplicationTests {

    @Autowired
    UserDao userDao;

    @Test
    void contextLoads() {
        String testString = "!setu 萝莉,少女 白丝,黑丝".substring(1);
        String command = testString.split(" ")[0];
            switch (command) {
                case "setu":
                    testFunc(testString.substring(5),"tag");
            }

    }

    void testFunc(String testString, String commandType) {

        JSONObject loliBody = new JSONObject();
        loliBody.put("tag","");
        JSONArray loliParams = new JSONArray();
        for (String params : testString.split(" ")) {
            JSONArray loliParam = new JSONArray();
            for (String param : params.split(",")) {
                loliParam.add(param);
            }
            loliParams.add(loliParam);
        }
        loliBody.put("tag", loliParams);

        System.out.println(loliBody);
    }

    @Test
    void testMapper() {
        System.out.println("412543224");
    }

}
