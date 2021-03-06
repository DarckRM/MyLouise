package com.darcklh.louise.Api;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 识别发送的图片的Api
 */
@Service
public class SearchPictureApi{

    Logger logger = LoggerFactory.getLogger(SearchPictureApi.class);

    @Autowired
    LouiseConfig louiseConfig;

    @Autowired
    FileControlApi fileControlApi;

    @Autowired
    R r;

    private String uploadImgUrl;

    public void setUploadImgUrl(String uploadImgUrl) {
        this.uploadImgUrl = uploadImgUrl;
    }

    /**
     * 搜图入口
     * @param message
     * @return
     */
    public void searchPictureCenter(JSONObject message, R r) {

        logger.info("进入搜图流程, 发起用户为:"+r.getNickname()+" QQ:"+r.getNumber());
        logger.debug(message.toString());

        JSONObject sendJson = new JSONObject();
        sendJson.put(r.getSenderType(), r.getNumber());

        //TODO 线程名过长
        new Thread(() -> findWithAscii2d(r.getNickname(), sendJson), UniqueGenerator.uniqueThreadName("", "A2d")).start();
        new Thread(() -> findWithSourceNAO(r.getNickname(), sendJson), UniqueGenerator.uniqueThreadName("", "NAO")).start();
    }

    /**
     * 通过Ascii2d搜索图片
     * @param nickname
     * @param sendJson
     */
    public void findWithAscii2d(String nickname, JSONObject sendJson) {
        logger.info("进入Ascii2d识别流程");

        String url = uploadImgUrl;
        JSONObject resultData = new JSONObject();
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
            Element img = document.getElementsByClass("image-box").get(1).getElementsByAttribute("loading").get(0);

            //判断来源 Twitter跳过
            String source = element.getElementsByTag("small").text();
            String title = element.child(1).text();
            String thumbnail = "https://ascii2d.net"+img.attr("src");
            ArrayList<String> origin = new ArrayList<>();
            origin.add(element.child(1).attr("href"));
            String member_name = element.child(2).text();
            String author_page = element.child(2).attr("href");

            if (source.equals("twitter")) {
                sendJson.put("message", nickname+"，这是Ascii2d的结果" +
                        "\n标题: " + title +
                        "\n作者: " + member_name +
                        "\n来源推特: " + origin +
                        "\n[CQ:image,file="+thumbnail+"]");
                r.sendMessage(sendJson);
                return;
            }

            String pixiv_id = origin.get(0).substring(31);
            resultData.put("pixiv_id", pixiv_id);
            resultData.put("title", title);
            resultData.put("member_name", member_name);
            resultData.put("ext_urls", origin);
            resultData.put("index_name", 0);
            resultData.put("thumbnail", thumbnail);
            resultData.put("invoker", "A2d");
            sendJson = handleFromPixiv(nickname, "来自Ascii2d", sendJson, resultData, resultData);
            r.sendMessage(sendJson);

        } catch (Exception e) {
            logger.info("请求失败： "+e.getMessage());
            sendJson.put("message", "请求Ascii2d失败了！");
            r.sendMessage(sendJson);
        }
    }

    /**
     * 通过SourceNAO开放API搜图
     * @param nickname 昵称
     * @param sendJson
     * @return
     */
    public void findWithSourceNAO(String nickname, JSONObject sendJson) {
        logger.info("进入SourceNAO识别流程");
        try {
            String url = uploadImgUrl;

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
                    sendJson.put("message", louiseConfig.getLOUISE_ERROR_THIRD_API_REQUEST_FAILED());
                } else {
                    sendJson.put("message", louiseConfig.getLOUISE_ERROR_UPLOAD_IMAGE_FAILED());
                }
                r.sendMessage(sendJson);
                return;
            }

            JSONObject sourceNAO = result.getJSONArray("results").getJSONObject(0);
            logger.info("最匹配的结果: "+sourceNAO.toString());

            //格式化结果
            JSONObject sourceNaoData = sourceNAO.getJSONObject("data");
            sourceNaoData.put("thumbnail", "thumbnail");
            JSONObject sourceNaoHeader = sourceNAO.getJSONObject("header");
            String similarity = sourceNAO.getJSONObject("header").getString("similarity");
            Integer indexId = sourceNAO.getJSONObject("header").getInteger("index_id");
            String index_name = sourceNAO.getJSONObject("header").getString("index_name");
            sourceNaoHeader.put("invoker", "NAO");
            //相似度低于70%的结果以缩略图显示 排除Twitter来源
            if (Float.parseFloat(similarity) < 70.0 && indexId != 41) {
                logger.info("结果可能性低");
                sendJson.put("message", "找到的结果相似度为"+similarity+"显示缩略图" +
                        "\n此为不大可能的结果: \n"+
                        sourceNaoData.getJSONArray("ext_urls").toString()+
                        "\n[CQ:image,file="+sourceNaoHeader.getString("thumbnail")+"]");
                logger.info("请求Bot的响应结果: "+r.sendMessage(sendJson));
                return;
            }

            //判断结果来源 如twitter之流来源很难获取图片 会补充URL以供查看
            switch (indexId) {
                //来自Pixiv
                case 5: sendJson = handleFromPixiv(nickname, similarity, r.getMessage(), sourceNaoData, sourceNaoHeader); break;
                //TODO 暂时禁用推特来源 未解决图片缓存路径问题
                case 41: sendJson = handleFromTwitter(nickname, similarity, r.getMessage(), sourceNaoData, sourceNaoHeader); break;
                case 9:
                case 12: sendJson = handleFromGelbooru(nickname, similarity, r.getMessage(), sourceNaoData, sourceNaoHeader); break;
                default: {
                    sendJson.put("message", "暂不支持返回该来源的具体信息" +
                            "来源信息: " + index_name +
                            "相似度: " + similarity +
                            "[CQ:image,file=" + sourceNaoHeader.getString("thumbnail") + "]");
                    logger.info("请求Bot的响应结果: "+r.sendMessage(sendJson));
                    return;
                }
            }
            logger.info("请求Bot的响应结果: "+r.sendMessage(sendJson));
        } catch (Exception e) {
            sendJson.put("message", "SourceNAO未能找到合理的结果，或者来源不支持\n" +
                    "堆栈信息: "+ e.getMessage());
            logger.info("请求Bot的响应结果: "+r.sendMessage(sendJson));
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

        String invoker = resultHeader.getString("invoker");
        String pixiv_id = resultData.getString("pixiv_id");
        String title = resultData.getString("title");
        String thumbnail = resultHeader.getString("thumbnail");
        String member_name = resultData.getString("member_name");
        String ext_urls = resultData.getJSONArray("ext_urls").toArray()[0].toString();
        String url = louiseConfig.getBOT_LOUISE_CACHE_IMAGE() + "pixiv/" + pixiv_id + ".jpg";

        //牺牲速度获得更好的图片显示 后台预解析图片信息
        try {
            Document document = Jsoup.connect(louiseConfig.getPIXIV_PROXY_URL() + pixiv_id + ".jpg").ignoreHttpErrors(true).post();
            //试着确认是否多图结果
            String images_number = document.body().getElementsByTag("p").first().text();
            images_number = images_number.substring(images_number.indexOf(" ") + 1, images_number.lastIndexOf(" "));
            int count = Integer.parseInt(images_number);
            logger.info("总共有 " + count + " 张图片");

            //如果是从Ascii2d调用的handleFromPixiv()那么跳过获取图片次序的逻辑
            //确认是多图结果 从JSON中获取匹配结果图片的次序
            int index = 1;
            if (invoker.equals("NAO")) {
                String image_index = resultHeader.getString("index_name");
                image_index = image_index.substring(image_index.indexOf("_p") + 2);
                String indexString = "";
                for (char cc : image_index.toCharArray()) {
                    if (Character.isDigit(cc)) {
                        indexString += cc;
                    } else break;
                }
                index = Integer.parseInt(indexString) + 1;
                logger.info("精确匹配结果为第 " + index + " 张");
            } else {
                index = 1;
                logger.info("无法判断精确位置，默认为第 " + index + " 张");
            }

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
                fileControlApi.downloadPictureURL(louiseConfig.getPIXIV_PROXY_URL() + pixiv_id + "-" + i + ".jpg", pixiv_id + "-" + i, "pixiv");
                images += "[CQ:image,file=" + louiseConfig.getBOT_LOUISE_CACHE_IMAGE() + "pixiv/" + pixiv_id + "-" + i + ".jpg]";
            }
            reply.put("message",
                    nickname + "，查询出来咯，有" + count + "张结果" + "，精确结果在第" + index + "张" +
                            "\n来源Pixiv" +
                            "\n标题:" + title +
                            "\n作者:" + member_name +
                            "\n相似度:" + similarity +
                            "\n可能的图片地址:" + ext_urls +
                            "\n[CQ:image,file="+thumbnail+"]" +
                            "\n" + images + "");
            logger.info("图片地址:" + images);
            return reply;


        } catch (Exception e) {
            logger.info(e.getLocalizedMessage());
            fileControlApi.downloadPictureURL(louiseConfig.getPIXIV_PROXY_URL() + pixiv_id + ".jpg", pixiv_id, "pixiv");
            reply.put("message",
                    nickname+"，查询出来咯"+
                            "\n来源Pixiv"+
                            "\n标题:"+title+
                            "\n作者:"+member_name+
                            "\n相似度:"+similarity+
                            "\n可能的图片地址:"+ext_urls+
                            "\n[CQ:image,file="+thumbnail+"]" +
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
        String finalUrl = imageUrlPrefix + imageUrl + "?format=" + imageUrlEndfix + "&name=large";
        //TODO 暂时无法下载Twitter的图片
        //fileControlApi.downloadPicture(finalUrl, imageUrl, "Twiiter");

        reply.put("message",
                nickname+"，查询出来咯"+
                        "\n来源Twitter"+
                        "\n推文用户:"+twitter_user_handle+
                        "\n用户ID:"+twitter_user_id+
                        "\n相似度:"+similarity+
                        "\n图片可能无法正常显示，说明缺乏网络环境，请点击链接访问"+
                        "\n推文地址:"+sourceNaoArray+
                        "\n图片地址:"+finalUrl+
                        "\n[CQ:image,file="+finalUrl+"]");
        logger.info("图片地址"+finalUrl);
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
    private JSONObject handleFromGelbooru(String nickname, String similarity, JSONObject reply, JSONObject sourceNaoData, JSONObject sourceNaoHeader) {

        logger.info("处理Gelbooru来源");
        String sourceNaoArray = sourceNaoData.getJSONArray("ext_urls").toString();
        String thumbnail = sourceNaoHeader.getString("thumbnail");
        String characters = sourceNaoData.getString("characters");
        String creator = sourceNaoData.getString("creator");
        String index_name = sourceNaoHeader.getString("index_name");

        String imageUrl;
        imageUrl = index_name.substring(index_name.indexOf(" - ")+3, index_name.length()-4);
        String imageUrlEndfix = index_name.substring(index_name.length()-4);
        imageUrl = imageUrl.substring(0, imageUrl.indexOf("_"));
        String imageFinalUrlPrefix = imageUrl.substring(0,2) + "/" + imageUrl.substring(2,4) + "/";
        String imageExampleUrlPrefix = imageUrl.substring(0,2) + "/" + imageUrl.substring(2,4) + "/sample_";
        String finalUrl = "https://img3.gelbooru.com//images/" + imageFinalUrlPrefix + imageUrl + imageUrlEndfix;
        String exampleUrl = "https://img3.gelbooru.com//samples/" + imageExampleUrlPrefix + imageUrl + imageUrlEndfix;

        boolean isImage = fileControlApi.downloadPictureURL(finalUrl, "image_" + imageUrl, "Gelbooru");
        boolean isSample = fileControlApi.downloadPictureURL(exampleUrl, "sample_" + imageUrl, "Gelbooru");

        String message = nickname+"，查询出来咯"+
                "\n来源Gelbooru"+
                "\n角色:"+characters+
                "\n作者:"+creator+
                "\n相似度:"+similarity+
                "\n可能的图片地址:" + sourceNaoArray +
                "\n[CQ:image,file="+thumbnail+"]" +
                "\n[CQ:image,file=" + louiseConfig.getBOT_LOUISE_CACHE_IMAGE() + (isImage ? "Gelbooru/image_" : "Gelbooru/sample_") + imageUrl + ".jpg]" +
                "\n信息来自Yande.re，结果可能不准确，请通过上面的链接访问";
        reply.put("message", message);
        return reply;
    }

}
