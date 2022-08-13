package com.darcklh.louise;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Model.Messages.InMessage;
import com.darcklh.louise.Model.Messages.Node;
import com.darcklh.louise.Model.Messages.OutMessage;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Model.Messages.Messages;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MyLouiseApplicationTests {
//
//    @Autowired
//    R r;
//
//    enum MsgType {
//        NORMAL,
//        FORWARD
//    }
//
//    @Test
//    public void test() {
//
//        InMessage inMessage = new InMessage();
//        inMessage.setGroup_id((long) 792873704);
//        inMessage.setSelf_id((long) 412543224);
//
//        OutMessage outMessage = new OutMessage(inMessage);
//        // Messages message = new Messages(, inMessage.getSelf_id());
//        // messages.setMessages(LouiseConfig.LOUISE_ERROR_THIRD_API_REQUEST_FAILED);
//
//        outMessage.getMessages().add(new Node("wo tm lai la", inMessage.getSelf_id()));
//        // System.out.println(JSONObject.toJSONString(outMessage));
//        r.sendTestGroupForwardMessage(outMessage);
//        return;
//    }
}
