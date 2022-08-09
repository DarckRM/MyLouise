package com.darcklh.louise.Model;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Utils.isEmpty;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        // 开始请求
        log.info("请求接口: " + api);
        JSONObject response = restTemplate.postForObject(LouiseConfig.BOT_BASE_URL + api, cqhttp, JSONObject.class);

        // 校验请求结果
        if(!verifyRequest(response)) {
            String message = "Louise 无法发送消息，可能是被干扰了，请尝试私聊😢😢\n";
            message += "错误解释: " + response.getString("wording") + "\n";
            message += "错误消息: " + response.getString("msg") + "\n";
            sendJson.put("message", message);
            cqhttp = new HttpEntity<>(sendJson.toString(), headers);

            log.info("发送错误原因:" + response.getString("wording") + " : " + response.getString("msg"));
            restTemplate.postForObject(LouiseConfig.BOT_BASE_URL + "send_msg", cqhttp, JSONObject.class);
        }
        this.refresh();
        return response;
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
     * 根据参数发送转发群组信息 主要是用于处理风控问题
     * @param content
     * @param senderName
     * @param selfId
     * @param sendJson
     * @return
     */
    public JSONObject sendGroupForwardMessage(String content, String senderName, Long selfId, JSONObject sendJson) {

        List<JSONObject> jsonObjectList = new ArrayList<>();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", "node");

        JSONObject data = new JSONObject();
        data.put("name", senderName);
        data.put("uin", selfId);
        data.put("content", content);

        jsonObject.put("data", data);
        jsonObjectList.add(jsonObject);

        sendJson.put("messages", jsonObjectList);

        return this.requestAPI("send_group_forward_msg", sendJson);
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

    /**
     * 校验请求的结果
     * @param response
     */
    private boolean verifyRequest(JSONObject response) {
        if (response == null)
            return false;
        return response.getString("status").equals("ok");
    }

}
