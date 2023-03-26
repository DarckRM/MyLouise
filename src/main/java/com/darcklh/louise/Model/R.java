package com.darcklh.louise.Model;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Model.Messages.OutMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
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
            log.warn("无法与BOT建立连接: " + e.getMessage());
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
        log.info("请求 cqhttp 接口: " + api);
        this.refresh();
        return restTemplate.postForObject(LouiseConfig.BOT_BASE_URL + api, cqhttp, JSONObject.class);
    }

    public JSONObject requestAPI(String api, JSONObject param) {
        if (!testConnWithBot())
            return null;
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> cqhttp = new HttpEntity<>(param.toJSONString(), headers);
        //开始请求
        log.info("请求 cqhttp 接口: " + api);
        this.refresh();
        return restTemplate.postForObject(LouiseConfig.BOT_BASE_URL + api, cqhttp, JSONObject.class);
    }

    /**
     * 带参数请求cqhttp接口
     * @param api
     * @param outMessage
     * @return
     */
    public JSONObject requestAPI(String api, OutMessage outMessage) {

        if (!testConnWithBot())
            return null;
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> cqhttp = new HttpEntity<>(JSONObject.toJSONString(outMessage), headers);
        // 开始请求
        log.info("请求 cqhttp 接口: " + api);
        JSONObject response = restTemplate.postForObject(LouiseConfig.BOT_BASE_URL + api, cqhttp, JSONObject.class);

        // 校验请求结果
        if(!verifyRequest(response)) {
            String message = "Louise 无法发送消息，可能是被干扰了，请尝试私聊😢😢\n";
            message += "错误解释: " + response.getString("wording") + "\n";
            message += "错误消息: " + response.getString("msg") + "\n";
            outMessage.setMessage(message);
            cqhttp = new HttpEntity<>(JSONObject.toJSONString(outMessage), headers);

            log.info("发送错误原因:" + response.getString("wording") + " : " + response.getString("msg"));
            restTemplate.postForObject(LouiseConfig.BOT_BASE_URL + "send_msg", cqhttp, JSONObject.class);
        }
        this.refresh();
        log.info("接口 " + api + " 返回消息:" + response.toString());
        return response;
    }

    /**
     * 根据参数向cqhttp发送消息
     * @param outMessage OutMessage
     * @return response String
     */
    public void sendMessage(OutMessage outMessage) {

        if (!testConnWithBot())
            throw new InnerException("B101", "无法连接 BOT， 请确认 Go-Cqhttp 正在运行", "");
        if (outMessage.getMessages().size() != 0)
            this.requestAPI("send_group_forward_msg", outMessage);
        else this.requestAPI("send_msg", outMessage);
    }

    /**
     * 发送群公告
     * @param group_id 群号
     * @param content 消息
     * @return
     */
//    public JSONObject sendGroupNotice(String group_id, String content) {
//
//        this.put("group_id", group_id);
//        this.put("content", content);
//        return this.requestAPI("/_send_group_notice", this.getMessage());
//    }


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
