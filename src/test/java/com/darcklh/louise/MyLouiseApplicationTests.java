package com.darcklh.louise;

import com.darcklh.louise.Controller.CqhttpWSController;
import com.darcklh.louise.Mapper.FeatureStaticDao;
import com.darcklh.louise.Model.Louise.BooruTags;
import com.darcklh.louise.Model.Louise.UserInvoke;
import com.darcklh.louise.Model.Messages.InMessage;
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

//    @Test
//    public void test() {
//        InMessage inMessage = new InMessage();
//        inMessage.setMessage("嗯");
//        CqhttpWSController.messageMap.put(41254L, inMessage);
//        CqhttpWSController.getMessage((value) -> {
//            if (value.getMessage().equals("嗯")) {
//                log.info("请在 15秒 内发送图片");
//                CqhttpWSController.getMessage((value2) -> {
//
//                }, 41254L, 15000L);
//                log.info("获取消息超时");
//            }
//            log.info("获取消息超时");
//        }, 41254L, 5000L);
//    }

}
