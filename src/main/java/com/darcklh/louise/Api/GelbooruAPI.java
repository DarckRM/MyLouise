package com.darcklh.louise.Api;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Config.LouiseConfig;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author DarckLH
 * @date 2022/4/13 20:21
 * @Description
 */
@RestController
public class GelbooruAPI {

    @Autowired
    LouiseConfig louiseConfig;

    Logger logger = LoggerFactory.getLogger(GelbooruAPI.class);

    /**
     *  测试函数
     */
    @RequestMapping("louise/gelbooru")
    public JSONObject test(@RequestBody JSONObject message) {

        //返回值
        JSONObject returnJson = new JSONObject();
        //解析上传的信息 拿到图片URL还有一些相关参数
        String url = message.getString("message");
        //获取请求元数据信息
        String message_type = message.getString("message_type");
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

        String uri = "https://gelbooru.com/index.php?page=post&s=list&tags=all";
        String page = "post";
        String s = "list";
        String tags = "all";
        String api_key = "8e67c397f5ffc2e6320694ed12ef696ef0833e0ca885b0ae886891e786ca8508";
        String user_id = "829309";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("page", page);
        params.add("s", s);
        params.add("tags", tags);
//        params.add("api_key", s);
//        params.add("user_id", s);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.USER_AGENT, "PostmanRuntime/7.26.8");
        headers.add("Connection","keep-alive");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, request, String.class);

        //解析Document查询出来的第一个数据
        params.clear();
        Document document = Jsoup.parse(response.getBody());

        Element element = document.getElementsByClass("thumbnail-container").first();
        Elements thumbnails = element.getElementsByClass("thumbnail-preview");
        String images = "";
        int x = 0;

        for (Element thumbnail : thumbnails) {
            Element image = thumbnail.getElementsByTag("img").first();
            images += "\n[CQ:image,file="+ image.attr("src")+"]\n";
            x++;
            if (x == 11)
                break;
        }

        returnJson.put("reply", nickname+" 这是Gelbooru的Post页面\n" + images);
        return returnJson;

    }

}