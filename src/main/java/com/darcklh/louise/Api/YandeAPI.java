package com.darcklh.louise.Api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Model.Messages.InMessage;
import com.darcklh.louise.Model.Messages.Node;
import com.darcklh.louise.Model.Messages.OutMessage;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Model.ReplyException;
import com.darcklh.louise.Utils.HttpProxy;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author DarckLH
 * @date 2022/4/13 20:21
 * @Description
 */
@Slf4j
@RestController
public class YandeAPI {
    // 每页最大数
    private static int LIMIT = 10;

    @Autowired
    FileControlApi fileControlApi;

    @Autowired
    R r;

    /**
     * 根据 Tag 返回可能的 Tags 列表
     * @param inMessage
     * @return
     */
    @RequestMapping("louise/yande/tags")
    public JSONObject YandeTags(@RequestBody InMessage inMessage) {

        // 处理命令前缀
        String[] msg;
        msg = inMessage.getMessage().split(" ");
        if (msg.length <= 1)
            throw new ReplyException("参数错误，请按如下格式尝试 !yande/tags [参数]");

        String tag = msg[1];

        // 返回值
        JSONObject returnJson = new JSONObject();

        // TODO: 总记录条数太多 会引起 QQ 风控
        String uri = "https://yande.re/tag.json?name=" + tag + "&limit=" + 20;
        // 使用代理请求 Yande
        RestTemplate restTemplate = new RestTemplate();
        // 借助代理请求
        if (LouiseConfig.LOUISE_PROXY_PORT > 0)
            restTemplate.setRequestFactory(new HttpProxy().getFactory("Yande API"));

        String result = restTemplate.getForObject(uri, String.class);
        log.info("请求 Yande: " + uri);
        StringBuilder tagList = new StringBuilder(inMessage.getSender().getNickname() + ", 你是否在找?\n");
        JSONArray resultJsonArray = JSON.parseArray(result);

        assert resultJsonArray != null;

        if(resultJsonArray.size() == 0) {
            returnJson.put("reply", "没有找到你想要的结果呢");
            return returnJson;
        }

        for ( Object object: resultJsonArray) {
            JSONObject tagObj = (JSONObject) object;
            String name = tagObj.getString("name");
            Integer count = tagObj.getInteger("count");
            Integer typeId = tagObj.getInteger("type");

            String type = "";
            switch (typeId) {
                case 0: type = "通常"; break;
                case 1: type = "作者"; break;
                case 3: type = "版权"; break;
                case 4: type = "角色"; break;
            }
            tagList.append(name).append(" 类型: ").append(type).append(" 有 ").append(count).append(" 张 \r\n");
        }

        OutMessage outMessage = new OutMessage(inMessage);
        if (outMessage.getGroup_id() < 0)
            outMessage.setMessage(tagList.toString());
        else
            outMessage.getMessages().add(new Node(tagList.toString(), inMessage.getSelf_id()));
        r.sendMessage(outMessage);

        return null;
    }

    /**
     *  获取 Yandere 每日图片
     */
    @RequestMapping("louise/yande/{type}")
    public JSONObject YandePic(@RequestBody InMessage inMessage, @PathVariable String type) {

        // 返回值
        JSONObject sendJson = new JSONObject();

        // 校验参数合法性
        if (!type.equals("day") && !type.equals("week") && !type.equals("month")) {
            sendJson.put("reply", "Yande 功能仅支持参数 day | week | month，请这样 !yande/[参数] 请求哦");
            return sendJson;
        }

        // 构造消息请求体
        OutMessage outMessage = new OutMessage(inMessage);
        outMessage.setMessage(inMessage.getSender().getNickname() + ", 开始请求 Yande 的 Every " + type + " 精选图片");
        r.sendMessage(outMessage);

        String uri = "https://yande.re/post/popular_by_" + type + ".json";

        new Thread(() -> {
            // 使用代理请求 Yande
            RestTemplate restTemplate = new RestTemplate();
            // 借助代理请求
            if (LouiseConfig.LOUISE_PROXY_PORT > 0)
                restTemplate.setRequestFactory(new HttpProxy().getFactory("Yande API"));

            String result = restTemplate.getForObject(uri + "?limit=" + LIMIT, String.class);
            log.info("请求 Yande: " + uri + "?limit=" + LIMIT);
            JSONArray resultJsonArray = JSON.parseArray(result);

            assert resultJsonArray != null;
            if(resultJsonArray.size() == 0) {
                outMessage.setMessage("没有找到你想要的结果呢");
                r.sendMessage(outMessage);
                return;
            }
            sendYandeResult(inMessage, resultJsonArray, LIMIT);

        }).start();
        return null;
    }

    @RequestMapping("louise/yande")
    public JSONObject YandeSearch(@RequestBody InMessage inMessage) {

        JSONObject sendJson = new JSONObject();

        // 判断是否携带 Tags 参数
        if (inMessage.getMessage().length() < 7) {
            sendJson.put("reply", "请至少携带一个 Tag 参数，像这样 !yande 参数1 参数2 | 页数 条数\n页数和条数可以不用指定");
            return sendJson;
        }

        // 处理命令前缀
        String message = inMessage.getMessage().substring(7);
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
        OutMessage outMessage = new OutMessage(inMessage);
        outMessage.setMessage(inMessage.getSender().getNickname() + ", 开始检索 Yande 图片咯");
        r.sendMessage(outMessage);

        // 处理线程安全问题
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
            // 借助代理请求
            if (LouiseConfig.LOUISE_PROXY_PORT > 0)
                restTemplate.setRequestFactory(new HttpProxy().getFactory("Yande API"));

            String result = restTemplate.getForObject(uri, String.class);
            JSONArray resultJsonArray = JSON.parseArray(result);

            assert resultJsonArray != null;
            if(resultJsonArray.size() == 0) {
                outMessage.setMessage("没有找到你想要的结果呢");
                r.sendMessage(outMessage);
                return;
            }
            sendYandeResult(inMessage, resultJsonArray, Integer.parseInt(finalPageNation[1]));

        }).start();

        return null;
    }

    /**
     *
     * @param inMessage
     * @param resultJsonArray
     * @param limit 如果是精选图集则只展示 15 张
     */
    private void sendYandeResult(InMessage inMessage, JSONArray resultJsonArray, Integer limit) {

        String replyImgList = inMessage.getSender().getNickname() +  ", 你的请求结果出来了，你输入的参数是: " + inMessage.getMessage().substring(7) + "\n";
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
        String msg = inMessage.getSender().getNickname() + " 这是Gelbooru的Post页面\n" + replyImgList;
        OutMessage outMessage = new OutMessage(inMessage);
        if (outMessage.getGroup_id() < 0)
            outMessage.setMessage(msg);
        else
            outMessage.getMessages().add(new Node(msg, inMessage.getSelf_id()));
        log.info(JSONObject.toJSONString(outMessage));
        r.sendMessage(outMessage);
    }
}
