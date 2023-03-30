package com.darcklh.louise;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.darcklh.louise.Api.FileControlApi;
import com.darcklh.louise.Api.MyLouiseApi;
import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Controller.GroupController;
import com.darcklh.louise.Mapper.BooruTagsDao;
import com.darcklh.louise.Model.Louise.BooruTags;
import com.darcklh.louise.Model.Louise.Group;
import com.darcklh.louise.Model.Messages.InMessage;
import com.darcklh.louise.Model.Messages.Node;
import com.darcklh.louise.Model.Messages.OutMessage;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Model.Messages.Messages;
import com.darcklh.louise.Service.BooruTagsService;
import com.darcklh.louise.Service.GroupService;
import com.darcklh.louise.Service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Field;
import java.net.http.HttpClient;
import java.util.List;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MyLouiseApplicationTests {

//    @Autowired
//    GroupService groupService;
//
//    @Test
//    public void run() {
//        List<Group> group_list = groupService.findBy();
//
//        for ( Group group : group_list) {
//            try {
//                groupService.update(group);
//            } catch (Exception ignored) {
//            }
//        }
//
////    }

}
