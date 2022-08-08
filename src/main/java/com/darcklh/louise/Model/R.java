package com.darcklh.louise.Model;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Config.LouiseConfig;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.*;

/**
 * 和Cqhttp通信的实体
 */
@Data
@Component
@Slf4j
public class R {

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

    private boolean testConnWithBot() {
        Socket socket = new Socket();
        SocketAddress address = new InetSocketAddress("127.0.0.1", 5700);
        try {
            socket.setSoTimeout(1000);
            socket.connect(address, 1000);
            socket.close();
        } catch (IOException e){
            log.info("无法与BOT建立连接: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 请求cqhttp接口
     * @param api
     * @return
     */
    public JSONObject requestAPI(String api) {
        if (!testConnWithBot())
            return null;
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> cqhttp = new HttpEntity<>(headers);
        //开始请求
        log.info("请求接口: " + api);
        this.refresh();
        return restTemplate.postForObject(LouiseConfig.BOT_BASE_URL + api, cqhttp, JSONObject.class);
    }

    /**
     * 带参数请求cqhttp接口
     * @param api
     * @param sendJson
     * @return
     */
    public JSONObject requestAPI(String api, JSONObject sendJson) {
        if (!testConnWithBot())
            return null;
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> cqhttp = new HttpEntity<>(sendJson.toString(), headers);
        //开始请求
        log.info("请求接口: " + api);
        JSONObject jsonObject = restTemplate.postForObject(LouiseConfig.BOT_BASE_URL + api, cqhttp, JSONObject.class);
        this.refresh();
        return jsonObject;
    }

    /**
     * 根据参数向cqhttp发送消息
     * @param sendJson JSONObject
     * @return response String
     */
    public JSONObject sendMessage(JSONObject sendJson) {

        //让Bot发送信息
        log.info("发送报文: " + sendJson);
        return this.requestAPI("send_msg", sendJson);
    }

    /**
     * 发送群公告
     * @param group_id 群号
     * @param content 消息
     * @return
     */
    public JSONObject sendGroupNotice(String group_id, String content) {

        this.put("group_id", group_id);
        this.put("content", content);
        return this.requestAPI("/_send_group_notice", this.getMessage());
    }

    /**
     * 快速操作
     * @param fastJson
     * @return
     */
    public JSONObject fastResponse(JSONObject fastJson) {
        log.info("快速操作: " + fastJson);
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

    /**
     * 每次发送消息后清空消息体
     */
    public void refresh() {
        this.message = new JSONObject();
    }

}
