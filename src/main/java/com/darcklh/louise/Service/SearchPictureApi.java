package com.darcklh.louise.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Utils.UniqueGenerator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

    @Autowired
    LouiseConfig louiseConfig;

    @Autowired
    FileControlApi fileControlApi;

    /**
     * 搜图入口
     * @param message
     * @return
     */
    public JSONObject searchPictureCenter(JSONObject message, R r) {

        logger.info("进入搜图流程, 发起用户为:"+r.getNickname()+" QQ:"+r.getNumber());
        logger.debug(message.toString());
        logger.info("上传图片的地址:"+r.getMessage().getString("url"));

        //TODO 线程名过长
        new Thread(() -> findWithAscii2d(r.getNickname(), r), UniqueGenerator.uniqueThreadName("", "A2d")).start();
        new Thread(() -> findWithSourceNAO(r.getNickname(), r), UniqueGenerator.uniqueThreadName("", "NAO")).start();

        return null;
    }

    /**
     * 通过Ascii2d搜索图片
     * @param nickname
     * @param r
     */
    public void findWithAscii2d(String nickname, R r) {

        String url = r.getMessage().getString("url");
        logger.info("进入Ascii2d识别流程");
        //由于Ascii2d返回的是HTML文档 借助Jsoup进行解析
        try {

            /**
             * TODO 现在的做法是先把搜索的图片作为参数去请求一次Ascii2d
             *      Ascii2d这样才能得到图片的MD5编码
             *      获取到的Document中的色度检索和特征检索URL
             *      再以获取到的URL做请求Ascii2d
             */
            logger.info("正在向Ascii2d上传图片 URL https://ascii2d.net/search/uri/?utf8=✓&uri=" + url);
            String uri = "https://ascii2d.net/search/uri";
            String utf8 = "✓";

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("utf8", utf8);
            params.add("uri", url);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.USER_AGENT, "PostmanRuntime/7.26.8");
            headers.add("Connection","keep-alive");
//            headers.add(HttpHeaders.DATE, n);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

            String colorSearchUrl = response.getBody().substring(35,100);
            String bovwSearchUrl = "https://ascii2d.net/search/bovw/" + colorSearchUrl.substring(colorSearchUrl.length() - 32);

            //解析Document查询出来的第一个数据
            logger.info("上传完成 图片的检索URL为 " + bovwSearchUrl);
            logger.info("开始进行特征检索");
            params.clear();
            HttpEntity<MultiValueMap<String, String>> request2 = new HttpEntity<>(params, headers);
            ResponseEntity<String> result = restTemplate.exchange(bovwSearchUrl, HttpMethod.GET, request2, String.class);
            Document document = Jsoup.parse(result.getBody());

            Element element = document.getElementsByClass("info-box").get(1).select(".detail-box h6").get(0);
            Element img = document.getElementsByClass("image-box").get(1);

            String thumbnail = img.child(0).attr("src");
            String title = element.child(1).text();
            String author = element.child(2).text();

            r.put("message", nickname+"，这是Ascii2d的结果" +
                    "\n标题: " + title +
                    "\n作者: " + author +
                    "\n[CQ:image,file=https://ascii2d.net" + thumbnail + "]");
            r.sendMessage(r.getMessage());
        } catch (Exception e) {
            logger.info("请求失败： "+e.getMessage());
            r.put("message", "请求Ascii2d失败了！");
            r.sendMessage(r.getMessage());
        }
    }

    /**
     * 通过SourceNAO开放API搜图
     * @param nickname 昵称
     * @param r R
     * @return
     */
    public void findWithSourceNAO(String nickname, R r) {
        logger.info("进入SourceNAO识别流程");
        try {
            String url = r.getMessage().getString("url");

            //构造Rest请求模板
            RestTemplate restTemplate = new RestTemplate();

            //构造请求SourceNAO的请求体
            Map<String, String> map = new HashMap<>();
            map.put("url", url);
            map.put("api_key", louiseConfig.getSOURCENAO_API_KEY());
            map.put("db", "999");
            map.put("output_type", "2");
            map.put("numres", "1");

            JSONObject result = JSON.parseObject(restTemplate.getForObject(louiseConfig.getSOURCENAO_URL()+"?url={url}&db={db}&api_key={api_key}&output_type={output_type}&numres={numres}", String.class, map));
            logger.debug("查询到的结果: "+result);

            //判断结果
            int status = result.getJSONObject("header").getInteger("status");
            if (status != 0) {
                if (status > 0) {
                    r.put("message", louiseConfig.getLOUISE_ERROR_THIRD_API_REQUEST_FAILED());
                } else {
                    r.put("message", louiseConfig.getLOUISE_ERROR_UPLOAD_IMAGE_FAILED());
                }
                r.sendMessage(r.getMessage());
                return;
            }

            JSONObject sourceNAO = result.getJSONArray("results").getJSONObject(0);
            logger.info("最匹配的结果: "+sourceNAO.toString());

            //格式化结果
            JSONObject sourceNaoData = sourceNAO.getJSONObject("data");
            JSONObject sourceNaoHeader = sourceNAO.getJSONObject("header");
            String similarity = sourceNAO.getJSONObject("header").getString("similarity");
            Integer indexId = sourceNAO.getJSONObject("header").getInteger("index_id");
            //相似度低于78%的结果以缩略图显示
            if (Float.parseFloat(similarity) < 78.0) {
                logger.info("结果可能性低");
                r.put("message", "找到的结果相似度为"+similarity+"显示缩略图" +
                        "\n此为不大可能的结果: \n"+
                        sourceNaoData.getJSONArray("ext_urls").toString()+
                        "\n[CQ:image,file="+sourceNaoHeader.getString("thumbnail")+"]");
                r.sendMessage(r.getMessage());
                return;
            }

            //返回结果集
            JSONObject returnJson = new JSONObject();

            //判断结果来源 如twitter之流来源很难获取图片 会补充URL以供查看
            switch (indexId) {
                //来自Pixiv
                case 5: returnJson = handleFromPixiv(nickname, similarity, r.getMessage(), sourceNaoData, sourceNaoHeader); break;
                case 41: returnJson = handleFromTwitter(nickname, similarity, r.getMessage(), sourceNaoData, sourceNaoHeader); break;
                case 9:
                case 12:
                    returnJson = handleFromDanbooru(nickname, similarity, r.getMessage(), sourceNaoData, sourceNaoHeader); break;
                default: {
                    returnJson.put("reply", "");
                    return;
                }
            }
            logger.info("请求Bot的响应结果: "+r.sendMessage(returnJson));
        } catch (Exception e) {
            r.put("message", "坏掉了！救我！\n" +
                    "堆栈信息: "+ e.getMessage());
            r.sendMessage(r.getMessage());
        }
    }

    /**
     * 处理来自Pixiv的图
     * @param nickname String
     * @param similarity String
     * @param reply JSONObject
     * @param resultData JSONObject
     * @param resultHeader JSONObject
     * @return JSONObject
     */
    private JSONObject handleFromPixiv(String nickname, String similarity, JSONObject reply, JSONObject resultData, JSONObject resultHeader) throws IOException{

        String pixiv_id = resultData.getString("pixiv_id");
        String title = resultData.getString("title");
        String member_name = resultData.getString("member_name");
        String ext_urls = resultData.getJSONArray("ext_urls").toArray()[0].toString();
        String url = louiseConfig.getLOUISE_CACHE_IMAGE_LOCATION() + pixiv_id + ".jpg";

        //牺牲速度获得更好的图片显示 后台预解析图片信息
        try {
            Document document = Jsoup.connect("https://pixiv.cat/" + pixiv_id + ".jpg").ignoreHttpErrors(true).post();

            //试着确认是否多图结果
            String images_number = document.body().getElementsByTag("p").first().text();
            images_number = images_number.substring(images_number.indexOf(" ") + 1, images_number.lastIndexOf(" "));
            Integer count = Integer.parseInt(images_number);
            logger.info("总共有 " + count + " 张图片");

            //确认是多图结果 从JSON中获取匹配结果图片的次序
            String image_index = resultHeader.getString("index_name");
            image_index = image_index.substring(image_index.indexOf("_p") + 2);
            String indexString = "";
            for (char cc : image_index.toCharArray()) {
                if (Character.isDigit(cc)) {
                    indexString += cc;
                } else break;
            }
            Integer index = Integer.parseInt(indexString) + 1;
            logger.info("精确匹配结果为第 " + index + " 张");

            int start = index - 2;
            int end = index + 2;
            int number = 0;

            while(number < 5) {

                if (start < 1 || end > count) {
                    if (start < 1)
                        start++;
                    else end--;
                    number++;
                    continue;
                }
                number++;
            }

            if (count <= 5) {
                start = 1;
                end = count;
            }

            //大于1张图的情况
            String images = "";
            for (int i = start; i <= end; i++) {
                //下载图片到本地
                fileControlApi.downloadPicture("https://pixiv.cat/" + pixiv_id + "-" + i + ".jpg", pixiv_id + "-" + i);
                images += "[CQ:image,file=" + louiseConfig.getBOT_LOUISE_CACHE_IMAGE() + pixiv_id + "-" + i + ".jpg]";
            }
            reply.put("message",
                    nickname + "，查询出来咯，有" + count + "张结果" + "，精确结果在第" + index + "张" +
                            "\n来源Pixiv" +
                            "\n标题:" + title +
                            "\n作者:" + member_name +
                            "\n相似度:" + similarity +
                            "\n可能的图片地址:" + ext_urls +
                            "\n" + images + "");
            logger.info("图片地址:" + images);
            return reply;


        } catch (Exception e) {
            logger.info(e.getLocalizedMessage());
            fileControlApi.downloadPicture("https://pixiv.cat/" + pixiv_id + ".jpg", pixiv_id);
            reply.put("message",
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

        reply.put("message",
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
        String imageUrlEndfix = index_name.substring(index_name.length()-4);
        imageUrl = imageUrl.substring(0, imageUrl.indexOf("_"));
        String imageFinalUrlPrefix = imageUrl.substring(0,2) + "/" + imageUrl.substring(2,4) + "/";
        String imageExampleUrlPrefix = imageUrl.substring(0,2) + "/" + imageUrl.substring(2,4) + "/sample_";
        String finalUrl = "https://img3.gelbooru.com//images/" + imageFinalUrlPrefix + imageUrl + imageUrlEndfix;
        String exampleUrl = "https://img3.gelbooru.com//samples/" + imageExampleUrlPrefix + imageUrl + imageUrlEndfix;

        fileControlApi.downloadPicture(finalUrl, imageUrl);
        fileControlApi.downloadPicture(exampleUrl, imageUrl);

        if (index_id == 12) {
            reply.put("message",
                    nickname+"，查询出来咯"+
                    "\n来源Yande.re"+
                    "\n角色:"+characters+
                    "\n作者:"+creator+
                    "\n相似度:"+similarity+
                    "\n可能的图片地址:" + sourceNaoArray +
                    "\n[CQ:image,file=" + louiseConfig.getBOT_LOUISE_CACHE_IMAGE() + imageUrl + ".jpg]" +
                    "\n信息来自Yande.re，结果可能不准确，请通过上面的链接访问");
        } else {
            reply.put("message",
                    nickname+"，查询出来咯"+
                    "\n来源Danbooru"+
                    "\n角色:"+characters+
                    "\n作者:"+creator+
                    "\n相似度:"+similarity+
                    "\n可能的图片地址:" + sourceNaoArray +
                    "\n[CQ:image,file=" + louiseConfig.getBOT_LOUISE_CACHE_IMAGE() + imageUrl +".jpg]");
        }

        logger.info("图片地址1:"+finalUrl);
        logger.info("图片地址2:"+exampleUrl);
        return reply;
    }

}
