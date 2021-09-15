package com.darcklh.louise.Model;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Service.FileControlApi;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 和Cqhttp通信的实体
 */
@Data
@Component
public class R {

    //自动注入信息载体
    @Autowired
    LouiseConfig louiseConfig;

    //发送报文的基础信息
    private String nickname;
    private String senderType;
    private String number;

    //报文体
    private JSONObject message = new JSONObject();

    //请求go-cqhhtp的请求头
    private HttpHeaders headers= new HttpHeaders();

    //构造Rest请求模板
    private RestTemplate restTemplate = new RestTemplate();

    Logger logger = LoggerFactory.getLogger(R.class);

    /**
     * 根据参数向cqhttp发送消息
     * @param sendJson JSONObject
     * @return response String
     */
    public String sendMessage(JSONObject sendJson) {

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> cqhttp = new HttpEntity<>(sendJson.toString(), headers);
        //让Bot发送信息
        logger.info("发送报文: " + sendJson);
        return restTemplate.postForObject("http://localhost:5700/send_msg", cqhttp, String.class);
    }

    /**
     * 像管理员发送消息
     * @param msg
     */
    public void sendAdminMessage(String msg, R r) {
        r.put("user_id", louiseConfig.getLOUISE_ADMIN_NUMBER());
        r.put("message", msg);
        r.sendMessage(getMessage());
    }

    /**
     * 快速操作
     * @param fastJson
     * @return
     */
    public JSONObject fastResponse(JSONObject fastJson) {
        logger.info("快速操作: " + fastJson);
        return fastJson;
    }

    /**
     * 向R中的message对象添加信息
     * @param key
     * @param value
     */
    public void put(String key, String value) {
        this.message.put(key, value);
    }

}
