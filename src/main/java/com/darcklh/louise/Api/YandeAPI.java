package com.darcklh.louise.Api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Config.LouiseConfig;
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

/**
 * @author DarckLH
 * @date 2022/4/13 20:21
 * @Description
 */
@Slf4j
@RestController
public class YandeAPI {

    Logger logger = LoggerFactory.getLogger(YandeAPI.class);

    @Autowired
    FileControlApi fileControlApi;

    /**
     *  获取 Yandere 每日图片
     */
    @RequestMapping("louise/yande/{type}")
    public JSONObject test(@RequestBody JSONObject message, @PathVariable String type) throws IOException {

        //返回值
        JSONObject returnJson = new JSONObject();
        //获取请求元数据信息
        String nickname = message.getJSONObject("sender").getString("nickname");

        String uri = "https://yande.re/post/popular_by_" + type + ".json";

//      String api_key = "8e67c397f5ffc2e6320694ed12ef696ef0833e0ca885b0ae886891e786ca8508";
//      String user_id = "829309";

        // 使用代理请求 Yande
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpProxy().getFactory());

        String result = restTemplate.getForObject(uri, String.class);
        JSONArray resultJsonArray = JSON.parseArray(result);

        assert resultJsonArray != null;
        // TODO: 暂时限定请求 10 张
        int limit = 10;
        String replyImgList = "";
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

        returnJson.put("reply", nickname+" 这是Gelbooru的Post页面\n" + replyImgList);
        return returnJson;

    }

}