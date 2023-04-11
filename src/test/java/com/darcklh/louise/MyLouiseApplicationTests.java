package com.darcklh.louise;

import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Controller.CqhttpWSController;
import com.darcklh.louise.Mapper.FeatureStaticDao;
import com.darcklh.louise.Model.Louise.BooruTags;
import com.darcklh.louise.Model.Louise.UserInvoke;
import com.darcklh.louise.Model.Messages.InMessage;
import com.darcklh.louise.Model.Messages.Message;
import com.darcklh.louise.Model.Messages.Node;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Model.Saito.FeatureStatic;
import com.darcklh.louise.Service.BooruTagsService;
import com.darcklh.louise.Service.PluginInvokeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MyLouiseApplicationTests {
//
//    @Test
//    public void test() {
//        Message message = Message.build();
//        message.setUser_id(412543224L);
//        message.setGroup_id(477960516L);
//        message.setMessage_type("group");
//        message.node(Node.build().text("测试链式调用1\n换行"))
//                .node(Node.build().text("测试链式调用1").image("https://blog.rmdarck.icu/p/%E6%AC%A2%E8%BF%8E%E8%AE%BF%E9%97%AE%E6%9C%AC%E5%8D%9A%E5%AE%A2/44C021F1CE3E72F088B2C9DDB3EB9569_hu0e23e75987d65a58a8de15746cdad4e8_9183_1600x0_resize_q75_box.jpg"))
//                .node(Node.build().text("测试插入节点"), 0)
//                .reply(-318257162)
//                .send((result) -> {
//                    log.info(result.toString());
//                });
//    }
}
