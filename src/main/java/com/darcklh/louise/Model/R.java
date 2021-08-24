package com.darcklh.louise.Model;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * 请求cqhttp的实体
 */
@Data
@Component
public class R {

    //各种信息
    @Value("${LOUISE.unknown_command}")
    public String UNKNOWN_COMMAND;
    @Value("${LOUISE.thirdApi_request_failed}")
    public String THIRDAPI_REQUEST_FAILED;
    @Value("${LOUISE.unknown_user}")
    public String UNKNOWN_USER;
    @Value("${LOUISE.banned_user}")
    public String BANNED_USER;

    private JSONObject message = new JSONObject();

    //请求go-cqhhtp的请求头
    private HttpHeaders headers= new HttpHeaders();

    //构造Rest请求模板
    private RestTemplate restTemplate = new RestTemplate();

    /**
     * 根据参数向cqhttp发送消息
     * @param sendJson JSONObject
     * @return response String
     */
    public String sendMessage(JSONObject sendJson) {

        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> cqhttp = new HttpEntity<>(sendJson.toString(), headers);
        //让Bot发送信息
        String response = restTemplate.postForObject("http://localhost:5700/send_msg", cqhttp, String.class);

        return response;
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
