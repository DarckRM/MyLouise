package com.darcklh.louise.Api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Model.MessageInfo;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Utils.HttpProxy;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author DarckLH
 * @date 2022/4/13 20:21
 * @Description
 */
@Slf4j
@RestController
public class YandeAPI {

    Logger logger = LoggerFactory.getLogger(YandeAPI.class);

    // 每页最大数
    private static int LIMIT = 15;

    @Autowired
    FileControlApi fileControlApi;

    @Autowired
    R r;

    /**
     *  获取 Yandere 每日图片
     */
    @RequestMapping("louise/yande/{type}")
    public JSONObject YandePic(@RequestBody MessageInfo messageInfo, @PathVariable String type) throws IOException {

        // 返回值
        JSONObject sendJson = new JSONObject();

        // 校验参数合法性
        if (!type.equals("day") && !type.equals("week") && !type.equals("month")) {
            sendJson.put("reply", "Yande 功能仅支持参数 day | week | month，请这样 !yande/[参数] 请求哦");
            return sendJson;
        }

        // 获取请求元数据信息
        String number = "";
        // 判断私聊或是群聊
        String senderType = "";
        if (messageInfo.getMessage_type().equals("group")) {
            number = messageInfo.getGroup_id().toString();
            senderType = "group_id";

        } else if (messageInfo.getMessage_type().equals("private")) {
            number = messageInfo.getUser_id().toString();
            senderType = "user_id";
        }

        // 构造消息请求体
        sendJson.put(senderType, number);
        sendJson.put("message", messageInfo.getSender().getNickname() + ", 开始请求 Yande 的 Every " + type + " 精选图片");
        r.sendMessage(sendJson);

        String uri = "https://yande.re/post/popular_by_" + type + ".json";

        // 处理线程安全问题
        String finalSenderType = senderType;

        new Thread(() -> {
            // 使用代理请求 Yande
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setRequestFactory(new HttpProxy().getFactory());

            String result = restTemplate.getForObject(uri + "?limit=" + LIMIT, String.class);
            log.info("请求 Yande: " + uri + "?limit=" + LIMIT);
            JSONArray resultJsonArray = JSON.parseArray(result);

            assert resultJsonArray != null;
            sendYandeResult(finalSenderType, messageInfo, resultJsonArray, sendJson, LIMIT);

        }).start();
        return null;
    }

    @RequestMapping("louise/yande")
    public JSONObject YandeSearch(@RequestBody MessageInfo messageInfo) {

        JSONObject sendJson = new JSONObject();

        // 获取请求元数据信息
        String number = "";
        // 判断私聊或是群聊
        String senderType = "";
        if (messageInfo.getMessage_type().equals("group")) {
            number = messageInfo.getGroup_id().toString();
            senderType = "group_id";

        } else if (messageInfo.getMessage_type().equals("private")) {
            number = messageInfo.getUser_id().toString();
            senderType = "user_id";
        }

        // 判断是否携带 Tags 参数
        if (messageInfo.getMessage().length() < 7) {
            sendJson.put("reply", "请至少携带一个 Tag 参数，像这样 !yande 参数1 参数2 | 页数 条数\n页数和条数可以不用指定");
            return sendJson;
        }

        // 处理命令前缀
        String message = messageInfo.getMessage().substring(7);
        String[] tags;
        String[] pageNation = new String[2];

        pageNation[0] = "1";
        pageNation[1] = "10";

        // 判断是否携带分页参数
        if (message.contains("|")) {
            // 获取参数列表
            String paramOne = message.substring(0, message.indexOf("|") - 1);
            String paramTwo = message.substring(message.indexOf("|") + 2);

            tags = paramOne.split(" ");
            String[] temp = paramTwo.split(" ");

            // 如果只有一个分页参数代表 页数
            if (temp.length == 1) {
                pageNation[0] = temp[0];
            } else {
                pageNation = temp;
            }

        } else {
            tags = message.split(" ");
        }

        // pageNation 只准接收两个参数
        if (tags.length > 4) {
            sendJson.put("reply", "标签参数最大只允许 4 个");
            return sendJson;
        }

        // pageNation 只准接收两个参数
        if (pageNation.length > 2) {
            sendJson.put("reply", "分页参数只允许传入 页数 和 结果数 位置 两个参数");
            return sendJson;
        }

        // 构造消息请求体
        sendJson.put(senderType, number);
        sendJson.put("message", messageInfo.getSender().getNickname() + ", 开始检索 Yande 图片咯");
        r.sendMessage(sendJson);

        // 处理线程安全问题
        String finalSenderType = senderType;
        String[] finalPageNation = pageNation;

        new Thread(() -> {
            String tagsParam = "";
            // 构造 Tags 参数
            for(String tag : tags)
                tagsParam += tag + "+";

            String uri = "https://yande.re/post.json" + "?tags=" + tagsParam + "&limit=" + finalPageNation[1] + "&page=" + finalPageNation[0];
            log.info("请求地址: " + uri);
            // 使用代理请求 Yande
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setRequestFactory(new HttpProxy().getFactory());

            String result = restTemplate.getForObject(uri, String.class);
            JSONArray resultJsonArray = JSON.parseArray(result);

            assert resultJsonArray != null;
            sendYandeResult(finalSenderType, messageInfo, resultJsonArray, sendJson, Integer.parseInt(finalPageNation[1]));

        }).start();

        return null;
    }

    /**
     *
     * @param finalSenderType
     * @param messageInfo
     * @param resultJsonArray
     * @param sendJson
     * @param limit 如果是精选图集则只展示 15 张
     */
    private void sendYandeResult(String finalSenderType, MessageInfo messageInfo, JSONArray resultJsonArray, JSONObject sendJson, Integer limit) {

        String replyImgList = messageInfo.getSender().getNickname() +  ", 你的请求结果出来了，你输入的参数是: " + messageInfo.getMessage().substring(7) + "\n";
        for ( Object object: resultJsonArray) {
            if (limit == 0)
                break;
            JSONObject imgJsonObj = (JSONObject) object;
            String urlList = imgJsonObj.getString("sample_url");
            String fileName = imgJsonObj.getString("md5") + "." + imgJsonObj.getString("file_ext");
            fileControlApi.downloadPicture_RestTemplate(urlList, fileName, "Yande");
            replyImgList += "[CQ:image,file=" + LouiseConfig.BOT_LOUISE_CACHE_IMAGE + "Yande/" + fileName + "]\n";
            limit--;
        }

        if (finalSenderType.equals("user_id")) {
            sendJson.put("message", messageInfo.getSender().getNickname() + " 这是Gelbooru的Post页面\n" + replyImgList);
            r.sendMessage(sendJson);
        } else {

            List<JSONObject> jsonObjectList = new ArrayList<>();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "node");
            JSONObject data = new JSONObject();
            data.put("name", "Yande");
            data.put("uin", messageInfo.getSelf_id());
            data.put("content", replyImgList);
            jsonObject.put("data", data);
            jsonObjectList.add(jsonObject);

            sendJson.put("messages", jsonObjectList);
            r.requestAPI("send_group_forward_msg", sendJson);
        }

    }
}
