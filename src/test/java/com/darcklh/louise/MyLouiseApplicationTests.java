package com.darcklh.louise;

import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Controller.CqhttpWSController;
import com.darcklh.louise.Mapper.FeatureStaticDao;
import com.darcklh.louise.Model.Louise.BooruTags;
import com.darcklh.louise.Model.Louise.User;
import com.darcklh.louise.Model.Louise.UserInvoke;
import com.darcklh.louise.Model.Messages.InMessage;
import com.darcklh.louise.Model.Messages.Message;
import com.darcklh.louise.Model.Messages.Node;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Model.Saito.FeatureStatic;
import com.darcklh.louise.Service.BooruTagsService;
import com.darcklh.louise.Service.PluginInvokeService;
import com.darcklh.louise.Utils.DragonflyUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MyLouiseApplicationTests {

//    @Autowired
//    DragonflyUtils dragonflyUtils;
//
//    @Autowired
//    BooruTagsService booruTagsService;
//
//    @Test
//    public void test() {
//        dragonflyUtils.setEx("rua", "wuhuhu", 300);
//    }
}
