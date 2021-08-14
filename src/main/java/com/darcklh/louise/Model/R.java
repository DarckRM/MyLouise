package com.darcklh.louise.Model;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
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

    public static JSONObject sendJsonMessage(HttpServletResponse response, String message) throws Exception {

        JSONObject returnJson = new JSONObject();
        returnJson.put("reply",message);
        return returnJson;
    }

}
