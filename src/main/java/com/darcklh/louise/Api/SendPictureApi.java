package com.darcklh.louise.Api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

/**
 * 发送随机好图的Api
 */
@Controller
public class SendPictureApi {

    //BOT运行接口
    @Value("${BASE_BOT_URL}")
    String BASE_BOT_URL;

    Logger logger = LoggerFactory.getLogger(SendPictureApi.class);

    public void sendPicture(String id, String nickname, String senderType, JSONObject message) {

        logger.info("进入发图流程, 发起用户为:"+nickname+" QQ:"+id);

        //构造Rest请求模板
        RestTemplate restTemplate = new RestTemplate();
        //请求go-cqhhtp的参数和请求头
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject jsonObject = new JSONObject();

        //构造请求LoliApi V2的请求体
        HttpHeaders loli = new HttpHeaders();
        loli.setContentType(MediaType.APPLICATION_JSON);
        JSONObject requestLoli = generateRequestBody(message.getString("raw_message").substring(5), "tag");
        requestLoli.put("size","regular");

        HttpEntity<String> littlLoli = new HttpEntity<>(requestLoli.toString(), loli);
        String result = restTemplate.postForObject("https://api.lolicon.app/setu/v2", littlLoli, String.class);

        JSONObject goodLoli = JSONObject.parseObject(result);
        logger.info(goodLoli.getJSONArray("data").toString());

        if(goodLoli.getJSONArray("data").toArray().length == 0) {

            jsonObject.put(senderType, id);
            jsonObject.put("message", "没能找到结果，也许你对XP系统有着独特的理解");

            headers.setContentLength(jsonObject.toString().length());
            HttpEntity<String> entity = new HttpEntity<String>(jsonObject.toString(),headers);
            restTemplate.postForObject(BASE_BOT_URL+"/send_msg", entity, String.class);
            return;
        }
        logger.debug(goodLoli.toString());
        //格式化结果
        String title = "";
        String author = "";
        String pid = "";
        String url = "";
        String tags = "";
        JSONArray loliArray = goodLoli.getJSONArray("data");

        for (int i = 0; i < loliArray.size(); i++) {
            title = loliArray.getJSONObject(i).getString("title");
            author = loliArray.getJSONObject(i).getString("author");
            pid = loliArray.getJSONObject(i).getString("pid");
            url = loliArray.getJSONObject(i).getJSONObject("urls").getString("regular");
            tags = loliArray.getJSONObject(i).getJSONArray("tags").toString();
        }
        jsonObject.put(senderType, id);
        jsonObject.put("message",
                nickname+"，你要的涩图已经送达，请注意身体健康哦"+
                "\n标题:"+title+
                "\n作者:"+author+
                "\npid:"+pid+
                "\n[CQ:image,file="+url+"]"+
                "\n标签:"+tags);

        HttpEntity<String> entity = new HttpEntity<String>(jsonObject.toString(),headers);
        restTemplate.postForObject(BASE_BOT_URL+"/send_msg", entity, String.class);
        return;

    }

    /**
     * 请求参数构造器
     * @param testString
     * @param commandType
     */
    JSONObject generateRequestBody(String testString, String commandType) {

        JSONObject loliBody = new JSONObject();
        loliBody.put(commandType,"");
        JSONArray loliParams = new JSONArray();
        for (String params : testString.split(" ")) {
            JSONArray loliParam = new JSONArray();
            for (String param : params.split(",")) {
                loliParam.add(param);
            }
            loliParams.add(loliParam);
        }
        loliBody.put("tag", loliParams);

        return  loliBody;
    }
}
