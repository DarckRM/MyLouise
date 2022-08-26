package com.darcklh.louise.Service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Api.FileControlApi;
import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Model.Messages.InMessage;
import com.darcklh.louise.Model.Messages.OutMessage;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Model.Messages.Messages;
import com.darcklh.louise.Model.ReplyException;
import com.darcklh.louise.Service.SearchPictureService;
import com.darcklh.louise.Utils.HttpProxy;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DarckLH
 * @date 2022/8/13 0:05
 * @Description
 */
@Service
@Slf4j
public class SearchPictureImpl implements SearchPictureService {

    @Autowired
    FileControlApi fileControlApi;

    @Autowired
    R r;

    /**
     * 通过SourceNAO开放API搜图
     * @param inMessage 昵称
     * @param url
     * @return
     */
    @Override
    public void findWithSourceNAO(InMessage inMessage, String url) {
        log.info("进入SourceNAO识别流程");
        // 构造返回体
        OutMessage outMessage = new OutMessage(inMessage);
        try {
            // 构造Rest请求模板
            RestTemplate restTemplate = new RestTemplate();

            // 借助代理请求
            if (LouiseConfig.LOUISE_PROXY_PORT > 0)
                restTemplate.setRequestFactory(new HttpProxy().getFactory("sourceNAO 请求"));

            // 构造请求SourceNAO的请求体
            Map<String, String> map = new HashMap<>();
            map.put("url", url);
            map.put("api_key", LouiseConfig.SOURCENAO_API_KEY);
            map.put("db", "999");
            map.put("output_type", "2");
            map.put("numres", "3");

            JSONObject sauceNAO = JSON.parseObject(restTemplate.getForObject(LouiseConfig.SOURCENAO_URL + "?url={url}&db={db}&api_key={api_key}&output_type={output_type}&numres={numres}", String.class, map));
            log.debug("查询到的结果: " + sauceNAO);

            // 判断结果 Header
            int status = sauceNAO.getJSONObject("header").getInteger("status");
            if (status != 0) {
                if (status > 0) {
                    outMessage.setMessage(LouiseConfig.LOUISE_ERROR_THIRD_API_REQUEST_FAILED);
                } else {
                    outMessage.setMessage(LouiseConfig.LOUISE_ERROR_UPLOAD_IMAGE_FAILED);
                }
                r.sendTestMsg(outMessage);
                return;
            }

            JSONArray results = sauceNAO.getJSONArray("results");
            JSONObject header = new JSONObject();
            JSONObject data = new JSONObject();
            Integer indexId = -9;
            String similarity = "";
            String index_name = "";
            boolean flag = false;

            for (Object object : results) {

                if (flag)
                    break;

                // 获取单个结果的信息
                JSONObject result = (JSONObject) object;
                header = result.getJSONObject("header");
                data = result.getJSONObject("data");

                indexId = header.getInteger("index_id");
                similarity = header.getString("similarity");
                index_name = header.getString("index_name");

                // 跳过无法处理的来源
                switch (header.getInteger("index_id")) {
                    case 5:
                    case 4:
                    case 12:
                    case 9:
                    case 25: flag = true; break;
                    default: {
                        outMessage.setMessage("暂不支持返回该来源的具体信息" +
                                "来源信息: " + index_name +
                                "相似度: " + similarity +
                                "[CQ:image,file=" + header.getString("thumbnail") + "]");
                        log.info("请求Bot的响应结果: " + r.sendMessage(outMessage));
                    }
                }
            }

            // 格式化结果
            log.info("最合理结果: " + data.toString());
            header.put("invoker", "NAO");

            // 相似度低于70%的结果以缩略图显示 排除Twitter来源
            if (Float.parseFloat(similarity) < 70.0 && indexId != 41) {
                log.info("结果可能性低");
                outMessage.setMessage("找到的结果相似度为" + similarity +
                        "\n此为不大可能的结果: \n" +
                        data.getJSONArray("ext_urls").toString() +
                        "\n[CQ:image,file=" + header.getString("thumbnail") + "]");
                log.info("请求Bot的响应结果: " + r.sendMessage(outMessage));
                return;
            }
            //判断结果来源以及是否可以处理 如twitter之流来源很难获取图片 会补充URL以供查看
            switch (indexId) {
                //来自Pixiv
                case 5:
                    handleFromPixiv(outMessage, similarity, data, header);
                    break;
                //TODO 暂时禁用推特来源 未解决图片缓存路径问题
                case 4:
                    handleFromTwitter(outMessage, similarity, data, header);
                    break;
                case 12:
                    handleFromYande(outMessage, similarity, data, header);
                    break;
                case 9:
                case 25:
                    handleFromGelbooru(outMessage, similarity, data, header);
                    break;
                default: {
                    outMessage.setMessage("暂不支持返回该来源的具体信息" +
                            "来源信息: " + index_name +
                            "相似度: " + similarity +
                            "[CQ:image,file=" + header.getString("thumbnail") + "]");
                    log.info("请求Bot的响应结果: " + r.sendMessage(outMessage));
                }
            }
            log.info("请求Bot的响应结果: " + r.sendMessage(outMessage));
        } catch (Exception e) {
            outMessage.setMessage("SourceNAO未能找到合理的结果，或者来源不支持\n" +
                    "堆栈信息: "+ e.getMessage());
            log.info("请求Bot的响应结果: " + r.sendMessage(outMessage));
        }
    }

    @Override
    public void findWithAscii2d(InMessage inMessage, String url) {
        log.info("进入Ascii2d识别流程");

        JSONObject resultData = new JSONObject();
        OutMessage outMessage = new OutMessage(inMessage);
        String nickname = outMessage.getSender().getNickname();
        //由于Ascii2d返回的是HTML文档 借助Jsoup进行解析
        try {

            /**
             * TODO 现在的做法是先把搜索的图片作为参数去请求一次Ascii2d
             *      Ascii2d这样才能得到图片的MD5编码
             *      获取到的Document中的色度检索和特征检索URL
             *      再以获取到的URL做请求Ascii2d
             */
            log.info("正在向Ascii2d上传图片 URL https://ascii2d.net/search/uri/?utf8=✓&uri=" + url);
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

            // 借助代理请求
            if (LouiseConfig.LOUISE_PROXY_PORT > 0)
                restTemplate.setRequestFactory(new HttpProxy().getFactory("Ascii2d 上传"));

            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

            String colorSearchUrl = response.getBody().substring(35,100);
            String bovwSearchUrl = "https://ascii2d.net/search/bovw/" + colorSearchUrl.substring(colorSearchUrl.length() - 32);

            //解析Document查询出来的第一个数据
            log.info("上传完成 图片的检索URL为 " + bovwSearchUrl);
            log.info("开始进行特征检索");
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
                outMessage.setMessage(nickname + "，这是Ascii2d的结果" +
                        "\n标题: " + title +
                        "\n作者: " + member_name +
                        "\n来源推特: " + origin +
                        "\n[CQ:image,file="+thumbnail+"]");
                r.sendMessage(outMessage);
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
            handleFromPixiv(outMessage, "来自Ascii2d", resultData, resultData);
            r.sendMessage(outMessage);

        } catch (Exception e) {
            log.info("请求失败： "+e.getMessage());
            outMessage.setMessage("请求Ascii2d失败了！");
            r.sendMessage(outMessage);
        }
    }


    /**
     * 处理来自Pixiv的图
     * @param outMessage OutMessage
     * @param similarity String
     * @param resultData JSONObject
     * @param resultHeader JSONObject
     * @return JSONObject
     */
    private OutMessage handleFromPixiv(OutMessage outMessage, String similarity, JSONObject resultData, JSONObject resultHeader) {

        String nickname = outMessage.getSender().getNickname();
        String invoker = resultHeader.getString("invoker");
        String pixiv_id = resultData.getString("pixiv_id");
        String title = resultData.getString("title");
        String thumbnail = resultHeader.getString("thumbnail");
        String member_name = resultData.getString("member_name");
        String ext_urls = resultData.getJSONArray("ext_urls").toArray()[0].toString();
        String url = LouiseConfig.BOT_LOUISE_CACHE_IMAGE + "pixiv/" + pixiv_id + ".jpg";

        //牺牲速度获得更好的图片显示 后台预解析图片信息
        try {
            Document document = Jsoup.connect(LouiseConfig.PIXIV_PROXY_URL + pixiv_id + ".jpg").ignoreHttpErrors(true).post();
            //试着确认是否多图结果
            String images_number = document.body().getElementsByTag("p").first().text();
            images_number = images_number.substring(images_number.indexOf(" ") + 1, images_number.lastIndexOf(" "));
            int count = Integer.parseInt(images_number);
            log.info("总共有 " + count + " 张图片");

            //如果是从Ascii2d调用的handleFromPixiv()那么跳过获取图片次序的逻辑
            //确认是多图结果 从JSON中获取匹配结果图片的次序
            int index;
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
                log.info("精确匹配结果为第 " + index + " 张");
            } else {
                index = 1;
                log.info("无法判断精确位置，默认为第 " + index + " 张");
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
                fileControlApi.downloadPicture_RestTemplate(LouiseConfig.PIXIV_PROXY_URL + pixiv_id + "-" + i + ".jpg", pixiv_id + "-" + i + ".jpg", "pixiv");
                images += "[CQ:image,file=" + LouiseConfig.BOT_LOUISE_CACHE_IMAGE + "pixiv/" + pixiv_id + "-" + i + ".jpg]";
            }
            outMessage.setMessage(nickname + "，查询出来咯，有" + count + "张结果" + "，精确结果在第" + index + "张" +
                "\n来源Pixiv" +
                "\n标题:" + title +
                "\n作者:" + member_name +
                "\n相似度:" + similarity +
                "\n可能的图片地址:" + ext_urls +
                "\n[CQ:image,file="+thumbnail+"]" +
                "\n" + images + "");
            log.info("图片地址:" + images);
            return outMessage;
        } catch (Exception e) {
            log.info(e.getLocalizedMessage());
            fileControlApi.downloadPicture_RestTemplate(LouiseConfig.PIXIV_PROXY_URL + pixiv_id + ".jpg", pixiv_id + ".jpg", "pixiv");
            outMessage.setMessage(nickname+"，查询出来咯"+
                "\n来源Pixiv"+
                "\n标题:"+title+
                "\n作者:"+member_name+
                "\n相似度:"+similarity+
                "\n可能的图片地址:"+ext_urls+
                "\n[CQ:image,file="+thumbnail+"]" +
                "\n[CQ:image,file="+url+"]");
            log.info("图片地址:"+url);
            return outMessage;
        }
    }
    /**
     * 处理来自Twitter的图
     * @param outMessage OutMessage
     * @param similarity String
     * @param sourceNaoData JSONObject
     * @param sourceNaoHeader JSONObject
     * @return JSONObject
     */
    private OutMessage handleFromTwitter(OutMessage outMessage, String similarity, JSONObject sourceNaoData, JSONObject sourceNaoHeader) {
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
        fileControlApi.downloadPicture_RestTemplate(finalUrl, imageUrl, "Twiiter");

        outMessage.setMessage(outMessage.getSender().getNickname()+"，查询出来咯"+
            "\n来源Twitter" +
            "\n推文用户:" + twitter_user_handle+
            "\n用户ID:" + twitter_user_id+
            "\n相似度:" + similarity+
            "\n图片可能无法正常显示，说明缺乏网络环境，请点击链接访问"+
            "\n推文地址:" + sourceNaoArray+
            "\n图片地址:" + finalUrl+
            "\n[CQ:image,file=" + finalUrl+"]");
        log.info("图片地址" + finalUrl);
        return outMessage;
    }
    /**
     * 处理来自Danbooru的图
     * @param outMessage OutMessage
     * @param similarity String
     * @param sourceNaoData JSONObject
     * @param sourceNaoHeader JSONObject
     * @return JSONObject
     */
    private OutMessage handleFromGelbooru(OutMessage outMessage, String similarity, JSONObject sourceNaoData, JSONObject sourceNaoHeader) {

        log.info("处理Gelbooru来源");
        String sourceNaoArray = sourceNaoData.getJSONArray("ext_urls").toString();
        String characters = sourceNaoData.getString("characters");
        String creator = sourceNaoData.getString("creator");
        String gelbooru_id = sourceNaoData.getString("gelbooru_id");

        String uri = "https://gelbooru.com/index.php?page=dapi&s=post&q=index&json=1&id=" + gelbooru_id;
        JSONObject imgJsonObj = requestBooru(uri, "GelBooru API", outMessage).getJSONArray("post").getJSONObject(0);

        String jpegUrl = imgJsonObj.getString("file_url");
        String fileName = "image_" + imgJsonObj.getString("image");
        String tags = imgJsonObj.getString("tags");
        fileControlApi.downloadPicture_RestTemplate(jpegUrl, fileName, "Gelbooru");

        String message = outMessage.getSender().getNickname() + "，查询出来咯"+
            "\n来源Gelbooru" +
            "\n角色:" + characters +
            "\n作者:" + creator +
            "\n标签:" + tags +
            "\n相似度:" + similarity +
            "\n可能的图片地址:" + sourceNaoArray +
            "\n[CQ:image,file=" + LouiseConfig.BOT_LOUISE_CACHE_IMAGE + "Gelbooru/" + fileName + "]" +
            "\n信息来自Gelbooru，结果可能不准确，请通过上面的链接访问";
        outMessage.setMessage(message);
        return outMessage;
    }

    private OutMessage handleFromYande(OutMessage outMessage, String similarity, JSONObject sourceNaoData, JSONObject sourceNaoHeader) {
        log.info("处理Yande来源");
        String sourceNaoArray = sourceNaoData.getJSONArray("ext_urls").toString();
        String post_id = sourceNaoData.getString("yandere_id");
        String characters = sourceNaoData.getString("characters");
        String creator = sourceNaoData.getString("creator");

        String uri = "https://yande.re/post.json" + "?tags=id:" + post_id;
        JSONObject imgJsonObj = requestBooru(uri, "Yande API", outMessage);

        String jpegUrl = imgJsonObj.getString("sample_url");
        String tags = imgJsonObj.getString("tags");
        String fileName = imgJsonObj.getString("md5") + "." + imgJsonObj.getString("file_ext");
        fileControlApi.downloadPicture_RestTemplate(jpegUrl, fileName, "Yande");

        String message = outMessage.getSender().getNickname() + "，查询出来咯"+
                "\n来源Yande.re" +
                "\n角色:" + characters +
                "\n作者:" + creator +
                "\n标签:" + tags +
                "\n相似度:" + similarity +
                "\n可能的图片地址:" + sourceNaoArray +
                "\n[CQ:image,file=" + LouiseConfig.BOT_LOUISE_CACHE_IMAGE + "Yande/" + fileName + "]" +
                "\n信息来自Yande.re，结果可能不准确，请通过上面的链接访问";
        outMessage.setMessage(message);
        return outMessage;
    }

    private JSONObject requestBooru(String uri, String booru_type, OutMessage outMessage) {
        // 构造请求图站的请求体
        log.info("请求地址: " + uri);
        // 使用代理请求 Yande
        RestTemplate restTemplate = new RestTemplate();
        // 借助代理请求
        if (LouiseConfig.LOUISE_PROXY_PORT > 0)
            restTemplate.setRequestFactory(new HttpProxy().getFactory(booru_type));

        JSONObject returnJson;
        JSONArray returnArray;

        String result = restTemplate.getForObject(uri, String.class);
        if (booru_type == "GelBooru API") {
            returnJson = JSON.parseObject(result);
            return returnJson;
        } else {
            returnArray = JSON.parseArray(result);
        }

        if (returnArray == null) {
            outMessage.setMessage("遗憾, 图片可能已经被删除了😢");
            throw new ReplyException(outMessage);
        }
        return (JSONObject)returnArray.get(0);
    }

}
