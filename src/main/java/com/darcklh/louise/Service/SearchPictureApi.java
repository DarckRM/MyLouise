package com.darcklh.louise.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONPOJOBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 识别发送的图片的Api
 */
@Service
public class SearchPictureApi {

    Logger logger = LoggerFactory.getLogger(SearchPictureApi.class);

    //BOT运行接口
    @Value("${BASE_BOT_URL}")
    String BASE_BOT_URL;

    @Value("${SOURCENAO_API}")
    private String SOURCENAO_API;

    @Value("${SOURCENAO_API_KEY}")
    private String SOURCENAO_API_KEY;

    /**
     * 搜图入口
     * @param id
     * @param nickname
     * @param senderType
     * @param message
     * @return
     */
    public JSONObject searchPictureCenter(String id, String nickname, String senderType, JSONObject message) {

        logger.info("进入搜图流程, 发起用户为:"+nickname+" QQ:"+id);
        logger.debug(message.toString());
        //解析上传的信息 拿到图片URL还有一些相关参数
        String url = message.getString("message");
        url = url.substring(url.indexOf("url=")+4, url.length()-1);
        logger.info("上传图片的地址:"+url);

        return findWithSourceNAO(id, nickname, senderType, url);

    }

    /**
     *
     * @return
     */
    public JSONObject findWithAscii2d() {

        //返回JSON
        JSONObject returnJson = new JSONObject();

        //由于Ascii2d返回的是HTML文档 借助Jsoup进行解析
        try {
            Document document = Jsoup.connect("https://ascii2d.net/search/").get();
            //解析Document查询出来的第一个数据
            Element element = document.getElementsByClass("info-box").get(1).select(".detail-box h6").get(0);

            String thumbnail = element.child(0).attr("src");
            String title = element.child(1).text();
            String author = element.child(2).text();

        } catch (IOException e) {
            logger.info("请求失败： "+e.getMessage());
            returnJson.put("reply", "请求Ascii2d失败了！");
            return returnJson;
        }
        return null;
    }

    /**
     * 通过SourceNAO开放API搜图
     * @param id QQ
     * @param nickname 昵称
     * @param senderType 群聊或是私聊
     * @param url 图片地址
     * @return
     */
    public JSONObject findWithSourceNAO(String id, String nickname, String senderType, String url) {
        try {

            //构造Rest请求模板
            RestTemplate restTemplate = new RestTemplate();

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
                    jsonObject.put("relpy", "sourceNAO出问题了，不关咱的事");
                } else {
                    jsonObject.put("reply", "上传的图片失败，或者我出了啥问题");
                }
                return jsonObject;
            }

            JSONObject sourceNAO = result.getJSONArray("results").getJSONObject(0);
            logger.info("最匹配的结果: "+sourceNAO.toString());

            //格式化结果
            JSONObject sourceNaoData = sourceNAO.getJSONObject("data");
            JSONObject sourceNaoHeader = sourceNAO.getJSONObject("header");
            String similarity = sourceNAO.getJSONObject("header").getString("similarity");
            Integer indexId = sourceNAO.getJSONObject("header").getInteger("index_id");
            //相似度低于78%的直接返回
            if (Float.parseFloat(similarity) < 78.0) {
                jsonObject.put("reply", "找到的结果相似度为"+similarity+"不予显示" +
                        "\n此为不大可能的结果: \n"+
                        sourceNaoData.getJSONArray("ext_urls").toString());
                return jsonObject;
            }

            //判断结果来源 如twitter之流来源很难获取图片 会补充URL以供查看
            switch (indexId) {
                //来自Pixiv
                case 5: return handleFromPixiv(nickname, similarity, jsonObject, sourceNaoData);
                case 41: return handleFromTwitter(nickname, similarity, jsonObject, sourceNaoData, sourceNaoHeader);
                case 9: return handleFromDanbooru(nickname, similarity, jsonObject, sourceNaoData, sourceNaoHeader);
            }
            return jsonObject;
        } catch (Exception e) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("reply", "坏掉了！救我！\n" +
                    "堆栈信息: "+ e.getMessage());
            return jsonObject;
        }
    }

    /**
     * 处理来自Pixiv的图
     * @param nickname String
     * @param similarity String
     * @param reply JSONObject
     * @param resultData JSONObject
     * @return JSONObject
     */
    private JSONObject handleFromPixiv(String nickname, String similarity, JSONObject reply, JSONObject resultData) throws IOException{

        String pixiv_id = resultData.getString("pixiv_id");
        String title = resultData.getString("title");
        String member_name = resultData.getString("member_name");
        String ext_urls = resultData.getJSONArray("ext_urls").toArray()[0].toString();
        String url = "https://pixiv.cat/" + pixiv_id + ".jpg";

        //牺牲速度获得更好的图片显示 后台预解析图片信息
        //TODO 预解析的时候直接下载到服务器，发送过后删除
        try {
            Document document = Jsoup.connect(url).ignoreHttpErrors(true).post();

            //TODO 存在隐患 当图的数量大于9的时候count数可能会出错
            Integer count = Integer.valueOf(document.body().getElementsByTag("p").first().text().substring(9,10));
            if (count > 1) {
                //大于1张图的情况
                String images = "";
                for (int i = 1; i <= count; i++) {
                    images += "[CQ:image,file=https://pixiv.cat/"+pixiv_id +"-"+i+".jpg]";
                }
                reply.put("reply",
                        nickname+"，查询出来咯，有"+count+"张结果"+
                                "\n来源Pixiv"+
                                "\n标题:"+title+
                                "\n作者:"+member_name+
                                "\n相似度:"+similarity+
                                "\n可能的图片地址:"+ext_urls+
                                "\n"+images+"");
                logger.info("图片地址:"+images);
                return reply;
            }
        } catch (Exception e) {

            reply.put("reply",
                    nickname+"，查询出来咯"+
                            "\n来源Pixiv"+
                            "\n标题:"+title+
                            "\n作者:"+member_name+
                            "\n相似度:"+similarity+
                            "\n可能的图片地址:"+ext_urls+
                            "\n[CQ:image,file="+url+"]");
            logger.info("图片地址:"+url);
            return reply;
        }
        return null;
    }
    /**
     * 处理来自Twitter的图
     * @param nickname String
     * @param similarity String
     * @param reply JSONObject
     * @param sourceNaoData JSONObject
     * @param sourceNaoHeader JSONObject
     * @return JSONObject
     */
    private JSONObject handleFromTwitter(String nickname, String similarity, JSONObject reply, JSONObject sourceNaoData, JSONObject sourceNaoHeader) {
        String sourceNaoArray = sourceNaoData.getJSONArray("ext_urls").toString();
        String twitter_user_id = sourceNaoData.getString("twitter_user_id");
        String twitter_user_handle = sourceNaoData.getString("twitter_user_handle");
        String index_name = sourceNaoHeader.getString("index_name");

        String imageUrl;
        imageUrl = index_name.substring(index_name.indexOf(" - ")+3, index_name.length()-4);
        String imageUrlEndfix = index_name.substring(index_name.indexOf(".")+1, index_name.indexOf(".")+4);
        String imageUrlPrefix = "https://pbs.twimg.com/media/";
        imageUrl = imageUrlPrefix + imageUrl + "?format=" + imageUrlEndfix + "&name=large";

        reply.put("reply",
                nickname+"，查询出来咯"+
                        "\n来源Twitter"+
                        "\n推文用户:"+twitter_user_handle+
                        "\n用户ID:"+twitter_user_id+
                        "\n相似度:"+similarity+
                        "\n可能的图片地址:"+sourceNaoArray+
                        "\n[CQ:image,file="+imageUrl+"]");
        logger.info("图片地址"+imageUrl);
        return reply;
    }
    /**
     * 处理来自Danbooru的图
     * @param nickname String
     * @param similarity String
     * @param reply JSONObject
     * @param sourceNaoData JSONObject
     * @param sourceNaoHeader JSONObject
     * @return JSONObject
     */
    private JSONObject handleFromDanbooru(String nickname, String similarity, JSONObject reply, JSONObject sourceNaoData, JSONObject sourceNaoHeader) {

        logger.info("处理Danbooru来源");
        String sourceNaoArray = sourceNaoData.getJSONArray("ext_urls").toString();
        String characters = sourceNaoData.getString("characters");
        String creator = sourceNaoData.getString("creator");
        String index_name = sourceNaoHeader.getString("index_name");
        Integer index_id = sourceNaoHeader.getInteger("index_id");

        String imageUrl;
        imageUrl = index_name.substring(index_name.indexOf(" - ")+3, index_name.length()-4);
        String imageUrlEndfix = index_name.substring(index_name.length()-4, index_name.length());
        imageUrl = imageUrl.substring(0, imageUrl.indexOf("_"));
        String imageFinalUrlPrefix = imageUrl.substring(0,2) + "/" + imageUrl.substring(2,4) + "/";
        String imageExampleUrlPrefix = imageUrl.substring(0,2) + "/" + imageUrl.substring(2,4) + "/sample_";
        String finalUrl = "https://img3.gelbooru.com//images/" + imageFinalUrlPrefix + imageUrl + imageUrlEndfix;
        String exampleUrl = "https://img3.gelbooru.com//samples/" + imageExampleUrlPrefix + imageUrl + imageUrlEndfix;
        if (index_id == 12) {
            reply.put("reply",
                    nickname+"，查询出来咯"+
                    "\n来源Yande.re"+
                    "\n角色:"+characters+
                    "\n作者:"+creator+
                    "\n相似度:"+similarity+
                    "\n可能的图片地址:"+sourceNaoArray+
                    "\n暂不支持输出Yande.re的图，请通过上面的链接访问");
        } else {
            reply.put("reply",
                    nickname+"，查询出来咯"+
                    "\n来源Danbooru"+
                    "\n角色:"+characters+
                    "\n作者:"+creator+
                    "\n相似度:"+similarity+
                    "\n可能的图片地址:"+sourceNaoArray+
                    "\n[CQ:image,file="+finalUrl+"]");
        }

        logger.info("图片地址1:"+finalUrl+"" +
                "\n图片地址2:"+exampleUrl);
        return reply;
    }

}
