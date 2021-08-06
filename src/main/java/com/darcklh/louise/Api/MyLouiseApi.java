package com.darcklh.louise.Api;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Service.SearchPictureApi;
import com.darcklh.louise.Service.SendPictureApi;
import com.darcklh.louise.Utils.EncryptUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;

@Controller
@RequestMapping("/louise")
public class MyLouiseApi {
    Logger logger = LoggerFactory.getLogger(MyLouiseApi.class);

    @Autowired
    private SendPictureApi sendPictureApi;

    @Autowired
    private SearchPictureApi searchPictureApi;

    @Autowired
    private  EncryptUtils encryptUtils;

    //机器人上报密钥
    @Value("${HTTP_POST_KEY}")
    String HTTP_POST_KEY;

    @RequestMapping("/test")
    @ResponseBody
    public JSONObject testRequestProcessCenter(HttpServletRequest request, @RequestBody String message) throws NoSuchAlgorithmException {

        String cryptCode = request.getHeader("X-signature");
        String encryptCode = encryptUtils.genHMAC(request.toString(), HTTP_POST_KEY);

        logger.debug(cryptCode);
        logger.debug(encryptCode);

        JSONObject result = new JSONObject();
        result.put("reply","现在测试中");
        return result;

    }

    @RequestMapping("/bot")
    @ResponseBody
    public JSONObject requestProcessCenter(@RequestBody String request) {

        JSONObject jsonObject = JSONObject.parseObject(request);
        String post_type = jsonObject.getString("post_type");

        //logger.info("命令: " + jsonObject.getString("raw_message").substring(1) + " 用户qq号: " +jsonObject.getJSONObject("sender").getString("user_id"));
        //排除心跳检测
        if (post_type.equals("meta_event")) {
            logger.debug("心跳检测");
            return null;
        }

        switch (post_type) {
            case "message": return handleMessagePost(jsonObject);
        }
        return null;
    }

    /**
     * 处理Message类型上报
     *
     */
    private JSONObject handleMessagePost(JSONObject message) {

        //获取请求元数据信息
        String message_type = message.getString("message_type");
        String command = message.getString("raw_message").substring(1).split(" ")[0];
        String number = "";
        String nickname = message.getJSONObject("sender").getString("nickname");

        //判断私聊或是群聊
        String senderType = "";
        if (message_type.equals("group")) {
            number = message.getString("group_id");
            senderType = "group_id";
        } else if (message_type.equals("private")) {
            number = message.getString("user_id");
            senderType = "user_id";
        }

        //默认返回值
        JSONObject defaultResult = new JSONObject();
        defaultResult.put("reply", "不是很懂你在说什么，如有需要请输入!help获取帮助");

        switch (command) {
            //处理默认信息
            default: return defaultResult;
            case "help": defaultResult.put("reply", "笑死，根本没有帮助"); return defaultResult;
            //调用LoliconAPI随机或根据参数请求色图
            case "setu": return sendPictureApi.sendPicture(number, nickname, senderType, message);
            //TODO 完善其它图库的返回结果
            //调用识图API根据上传图片进行识图
            case "find": return searchPictureApi.findWithSourceNAO(number, nickname, senderType, message);
        }
    }
}
