package com.darcklh.louise.Api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 识别发送的图片的Api
 */
@Controller
public class SearchPictureApi {

    Logger logger = LoggerFactory.getLogger(SearchPictureApi.class);

    //BOT运行接口
    @Value("${BASE_BOT_URL}")
    String BASE_BOT_URL;

    @Value("${SOURCENAO_API}")
    private String SOURCENAO_API;

    @Value("${SOURCENAO_API_KEY}")
    private String SOURCENAO_API_KEY;

    public void findWithSourceNAO(String id, String nickname, String senderType, JSONObject message) {

        logger.info("进入搜图流程, 发起用户为:"+nickname+" QQ:"+id);
        logger.debug(message.toString());
        //解析上传的信息 拿到图片URL还有一些相关参数
        String url = message.getString("message");
        url = url.substring(url.indexOf("url=")+4, url.length()-1);
        logger.info("上传图片的地址:"+url);
        //构造Rest请求模板
        RestTemplate restTemplate = new RestTemplate();
        //请求go-cqhhtp的参数和请求头
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(senderType, id);

        //构造请求SourceNAO的请求体
        Map<String, String> map = new HashMap<>();
        map.put("url", url);
        map.put("api_key", SOURCENAO_API_KEY);
        map.put("db", "999");
        map.put("output_type", "2");
        map.put("numres", "1");

        JSONObject result = JSON.parseObject(restTemplate.getForObject(SOURCENAO_API+"?url={url}&db={db}&api_key={api_key}&output_type={output_type}&numres={numres}", String.class, map));
        logger.debug("查询到的结果: "+result);

        //判断结果
        int status = result.getJSONObject("header").getInteger("status");
        if (status != 0) {
            if (status > 0) {
                jsonObject.put("message", "sourceNAO出问题了，不关咱的事");
            } else if (status < 0) {
                jsonObject.put("message", "上传的图片有问题，或者我出了啥问题，我也不知道");
            }
            HttpEntity<String> entity = new HttpEntity<String>(jsonObject.toString(),headers);
            restTemplate.postForObject(BASE_BOT_URL+"/send_msg", entity, String.class);
            return;
        }


        JSONObject sourceNAO = result.getJSONArray("results").getJSONObject(0);
        logger.info("最匹配的结果: "+sourceNAO.toString());

        //格式化结果
        String characters = sourceNAO.getJSONObject("data").getString("characters");;
        String creator = sourceNAO.getJSONObject("data").getString("creator");
        String similarity = sourceNAO.getJSONObject("header").getString("similarity");

        String imageUrl;
        imageUrl = sourceNAO.toString().substring(sourceNAO.toString().indexOf(" - ")+3, sourceNAO.toString().length()-3);
        String imageUrlEndfix = imageUrl.substring(imageUrl.indexOf("."), imageUrl.indexOf(".")+4);
        imageUrl = imageUrl.substring(0, imageUrl.indexOf("_"));

        String imageUrlPrefix = imageUrl.substring(0,2) + "/" + imageUrl.substring(2,4) + "/";
        imageUrl = imageUrlPrefix + imageUrl + imageUrlEndfix;

        jsonObject.put("message",
                nickname+"，查询出来咯"+
                        "\n角色:"+characters+
                        "\n作者:"+creator+
                        "\n相似度:"+similarity+
                        "\n[CQ:image,file=https://img3.gelbooru.com//images/"+imageUrl+"]");
        logger.info("https://img3.gelbooru.com//images/"+imageUrl);
        HttpEntity<String> entity = new HttpEntity<String>(jsonObject.toString(),headers);
        restTemplate.postForObject(BASE_BOT_URL+"/send_msg", entity, String.class);
    }

}
