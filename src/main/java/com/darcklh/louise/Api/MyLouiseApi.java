package com.darcklh.louise.Api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("/louise")
public class MyLouiseApi {
    Logger logger = LoggerFactory.getLogger(MyLouiseApi.class);

    @Autowired
    private SendPictureApi sendPictureApi;

    //BOT运行接口
    //String BASE_BOT_URL = "http://localhost:5700";

    @RequestMapping("/bot")
    public void requestProcessCenter(@RequestBody String request) {
        JSONObject jsonObject = JSONObject.parseObject(request);
        String post_type = jsonObject.getString("post_type");
        //logger.info("命令: " + jsonObject.getString("raw_message").substring(1) + " 用户qq号: " +jsonObject.getJSONObject("sender").getString("user_id"));
        //排除心跳检测
        if (post_type.equals("meta_event")) {
            logger.debug("心跳检测");
            return;
        }

        switch (post_type) {
            case "message": handleMessagePost(jsonObject);
        }

    }

    /**
     * 处理Message类型上报
     *
     */
    private void handleMessagePost(JSONObject message) {

        //获取请求元数据信息
        String message_type = message.getString("message_type");
        String command = message.getString("raw_message").substring(1).split(" ")[0];
        String number = "";

        //判断私聊或是群聊
        int operate = 0;
        if (message_type.equals("group")) {
            number = message.getString("group_id");
            operate = 2;
        } else if (message_type.equals("private")) {
            number = message.getString("user_id");
            operate = 1;
        }

        switch (command) {
            //调用LoliconAPI随机或根据参数请求色图
            case "setu": sendPictureApi.sendPicture(number, operate, message); break;
            case "find": break;
        }

        return;
    }
}
