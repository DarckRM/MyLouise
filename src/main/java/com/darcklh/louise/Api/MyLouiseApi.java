package com.darcklh.louise.Api;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class MyLouiseApi {
    Logger logger = LoggerFactory.getLogger(MyLouiseApi.class);

    //BOT运行接口
    String BASE_BOT_URL = "http://localhost:5700";

    @RequestMapping("/bot")
    public String requestProcessCenter(@RequestBody String request) {
        JSONObject jsonObject = JSONObject.parseObject(request);

        String post_type = jsonObject.getString("post_type");

        //排除心跳检测
        if (post_type.equals("meta_event")) {
            logger.debug("心跳检测");
            return "";
        }

        switch (post_type) {
            case "message": handleMessagePost(jsonObject);
        }

        System.out.println(jsonObject);
        return "what";
    }

    /**
     * 处理Message类型上报
     *
     */
    private void handleMessagePost(JSONObject message) {

        //获取具体的Message命令
        String message_type = message.getString("message_type");
        String command = message.getString("raw_message").substring(1);
        String number = message.getString("user_id");
        int operate = 0;
        if (message_type.equals("group")) {
            number = message.getString("group_id");
            operate = 2;
        } else if (message_type.equals("private"))
            operate = 1;

        switch (command) {
            case "setu": sendPicture(number, operate);
        }

        //随机发送一张图片

        return;
    }

    private void sendPicture(String id, int operate) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers= new HttpHeaders();
        JSONObject jsonObject = new JSONObject();
        if (operate == 1) {
            jsonObject.put("user_id",id);
            jsonObject.put("message","[CQ:image,file=https://iw233.cn/API/Random.php]");
        } else if (operate == 2) {
            jsonObject.put("group_id",id);
            jsonObject.put("message","[CQ:image,file=https://iw233.cn/API/Random.php]");
        }



        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentLength(jsonObject.toString().length());

        HttpEntity<String> entity = new HttpEntity<String>(jsonObject.toString(),headers);
        restTemplate.postForObject(BASE_BOT_URL+"/send_msg", entity, String.class);
        return;

    }
}
