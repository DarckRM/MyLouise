package com.darcklh.louise.Api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Controller.CqhttpWSController;
import com.darcklh.louise.Model.Louise.BooruTags;
import com.darcklh.louise.Model.Messages.InMessage;
import com.darcklh.louise.Model.Messages.Node;
import com.darcklh.louise.Model.Messages.OutMessage;
import com.darcklh.louise.Model.MultiThreadTask.DownloadPicTask;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Model.ReplyException;
import com.darcklh.louise.Service.BooruTagsService;
import com.darcklh.louise.Service.Impl.CalcImageTask;
import com.darcklh.louise.Utils.HttpProxy;
import com.darcklh.louise.Utils.TaskDistributor;
import com.darcklh.louise.Utils.UniqueGenerator;
import com.darcklh.louise.Utils.WorkThread;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
    private BooruTagsService booruTagsService;

    @Autowired
    R r;

    /**
     * 向数据库追加一条图站词条对照记录
     * @param inMessage
     * @return
     */
    @RequestMapping("louise/yande/add")
    public JSONObject addBooruTag(@RequestBody InMessage inMessage) {

        JSONObject reply = new JSONObject();
        String user_id = inMessage.getUser_id().toString();
        // 解析命令
        String message = inMessage.getMessage();
        String[] tags = message.split(" ");
        ArrayList<String> tag_array = new ArrayList<>(Arrays.asList(tags));
        tag_array.removeIf(s -> s.equals(""));
        tags = tag_array.toArray(new String[0]);

        // 合法性校验
        if (tags.length <= 1)
            throw new ReplyException("[CQ:at,qq=" + user_id + "]你没有给出需要添加的词条哦 |д`)");
        if (tags.length > 6)
            throw new ReplyException("[CQ:at,qq=" + user_id + "]太多词条别名啦，一次只能添加4个别名 (>д<)");


        BooruTags booruTag = new BooruTags();

        booruTag.setCn_name(tags[1]);
        List<BooruTags> booruTags = booruTagsService.findBy(booruTag);
        // 写入创建人 QQ
        booruTag.setInfo(user_id);

        // 判断是否有别名
        if (tags.length == 2) {

            if (booruTags.size() != 0)
                throw new ReplyException("[CQ:at,qq=" + user_id + "]已经存在“" + tags[1] + "”这个标签了 (>д<)");
            // 没有词条则写入新词条
            if(booruTagsService.save(booruTag)) {
                reply.put("reply", "[CQ:at,qq=" + user_id + "]已经添加“" + tags[1] + "”标签，请为它添加需要的别名吧 （<ゝω・）☆");
                return reply;
            }
            else
                throw new ReplyException("[CQ:at,qq=" + user_id + "]添加“" + tags[1] + "”失败，请联系开发者 (´д`)");
        } else {
            // 取出查询到的记录，复写 cn_name 字段并追加到数据库
            int index = 2;

            if (booruTags.size() != 0) {
                booruTag = booruTags.get(0);
                booruTag.setAlter_name(booruTag.getCn_name());
                // 写入创建人 QQ
                booruTag.setInfo(user_id);
            } else {
                // 写入新的根词条
                // 写入创建人 QQ
                booruTag.setInfo(user_id);
                if(!booruTagsService.saveAlter(booruTag))
                    throw new ReplyException("[CQ:at,qq=" + user_id + "]追加新的词条失败，请联系开发者 (>д<)");
            }

            // 遍历所有的别名并写入数据库
            for (; index < tags.length; index++) {
                booruTag.setCn_name(tags[index]);
                booruTag.setAlter_name(tags[1]);
                if(!booruTagsService.saveAlter(booruTag))
                    throw new ReplyException("[CQ:at,qq=" + user_id + "]添加“" + tags[index] + "”失败，请联系开发者 (´д`)");
            }
            reply.put("reply", "[CQ:at,qq=" + user_id + "]已成功追加所有标签 (´∀`)");
            return reply;
        }
    }

    /**
     * 提供 Booru 请求的一些帮助
     * @param inMessage
     * @return
     */
    @RequestMapping("louise/yande/help")
    public JSONObject booruHelp(@RequestBody InMessage inMessage) {

        JSONObject reply = new JSONObject();

        String message = "[CQ:at,qq=" + inMessage.getUser_id() + "]所有符号请使用英文符号\n"
               + "用例: !yande/day \n说明: 请求今日好图，day可为week,month\n\n"
               + "用例: !yande hatsune_miku \n说明: 初音未来相关图片，可用空格分隔最大4个标签\n\n"
               + "用例: !yande hatsune_miku | 2 10 \n说明: 初音未来相关图片第2页的10张，只有一个参数就只算页数\n\n"
               + "用例: !yande/tags miku \n说明: 查询和miku有关的标签，翻页方法和上面一致\n\n"
               + "用例: !yande/add 初音未来 ミク \n说明: 对初音未来的标签追加别名ミク，也可以不指定别名";
        reply.put("reply", message);
        return reply;
    }

    /**
     * Konachan 同属 Booru 类型图站
     * 和 Yande 放在一起处理
     */
    @RequestMapping("louise/konachan/tags")
    public JSONObject KonachanTags(@RequestBody InMessage inMessage) {
        // 处理命令前缀
        String[] msg;
        msg = inMessage.getMessage().split(" ");
        if (msg.length <= 1)
            throw new ReplyException("参数错误，请按如下格式尝试 !konachan/tags [参数]");
        return requestTags(msg, "https://konachan.com/tag.json?name=", "konachan", inMessage);
    }


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
        return requestTags(msg, "https://yande.re/tag.json?name=", "yande", inMessage);
    }

    /**
     * 获取涩涩的流行 Konachan 壁纸
     * @param inMessage
     * @param type
     * @return
     */
    @RequestMapping("louise/konachan/{type}")
    public JSONObject KonachanPic(@RequestBody InMessage inMessage, @PathVariable String type) {
        return requestPopular("https://konachan.com/post/popular_by_", "Konachan", type, inMessage);
    }

    /**
     *  获取 Yandere 每日图片
     */
    @RequestMapping("louise/yande/{type}")
    public JSONObject YandePic(@RequestBody InMessage inMessage, @PathVariable String type) {
        return requestPopular("https://yande.re/post/popular_by_", "Yande", type, inMessage);
    }

    @RequestMapping("louise/konachan")
    public JSONObject KonachanSearch(@RequestBody InMessage inMessage) {
        return requestBooru("https://konachan.com/post.json?tags=", "Konachan", inMessage);
    }

    @RequestMapping("louise/yande")
    public JSONObject YandeSearch(@RequestBody InMessage inMessage) {
        return requestBooru("https://yande.re/post.json?tags=", "Yande", inMessage);
    }

    /**
     *
     * @param inMessage
     * @param resultJsonArray
     * @param limit 如果是精选图集则只展示 15 张
     */
    private void sendYandeResult(InMessage inMessage, JSONArray resultJsonArray, Integer limit, String fileOrigin) throws InterruptedException {

        String replyImgList = "";
        List<DownloadPicTask> taskList = new ArrayList<>();
        OutMessage outMessage = new OutMessage(inMessage);
        int nsfw = 0;
        for ( Object object: resultJsonArray) {
            if (limit == 0)
                break;
            JSONObject imgJsonObj = (JSONObject) object;
            String[] tagList = imgJsonObj.getString("tags").split(" ");
            // 如果是群聊跳过成人内容
            if (outMessage.getGroup_id() >= 0)
                if (isNSFW(tagList)) {
                    nsfw++;
                    continue;
                }
            String fileName = imgJsonObj.getString("md5") + "." + imgJsonObj.getString("file_ext");
            // 分配下载任务
//            fileControlApi.downloadPicture_RestTemplate(imgJsonObj.getString("jpeg_url"), fileName, fileOrigin);
            taskList.add(new DownloadPicTask(imgJsonObj.getString("jpeg_url"), fileName, fileOrigin, fileControlApi));
            replyImgList += "[CQ:image,file=" + LouiseConfig.BOT_LOUISE_CACHE_IMAGE + fileOrigin + "/" + fileName + "]\r\n";
            limit--;
        }
        List[] taskListPerThread = TaskDistributor.distributeTasks(taskList, 4);

        for (int j = 0; j < taskListPerThread.length; j++) {
            Thread workThread = new WorkThread(taskListPerThread[j], j);
            workThread.start();
        }
        int status = -1;
        while(DownloadPicTask.already_downloaded.size() != taskList.size()) {
            if (status != DownloadPicTask.already_downloaded.size()) {
                status = DownloadPicTask.already_downloaded.size();
                Thread.sleep(5000);
            }
        }

        String announce = "现已支持部分中文搜索，如果想追加中文词条请使用!yande/help查看说明\n";
        String msg = replyImgList;
        if (nsfw != 0)
            msg += "已过滤 " + nsfw + " 张禁止内容图片，请私聊获取完整结果";
        if (outMessage.getGroup_id() < 0)
            outMessage.setMessage(announce + "[CQ:at,qq=" + inMessage.getSender().getUser_id() + "], 你的请求结果出来了，你输入的参数是: " + inMessage.getMessage().substring(7) + "\n" + msg);
        else
            outMessage.getMessages().add(new Node(announce + inMessage.getSender().getNickname() + ", 你的请求结果出来了，你输入的参数是: " + inMessage.getMessage().substring(7) + "\n" + msg, inMessage.getSelf_id()));
        log.info(JSONObject.toJSONString(outMessage));
        r.sendMessage(outMessage);
    }

    private boolean isNSFW(String[] tagList) {
        for ( String tag : tagList ) {
            switch (tag) {
                case "naked":
                case "nipples":
                case "sex":
                case "anus":
                case "breasts":
                case "pussy":
                case "naked_cape":
                case "no_bra":
                case "nopan":
                case "bikini":
                case "undressing":
                case "pantsu":
                case "monochrome":
                case "bondage":
                    return true;
            }
        }
        return false;
    }

    private JSONObject requestBooru(String url, String target, InMessage inMessage) {
        OutMessage outMsg = new OutMessage(inMessage);

        // 判断是否携带 Tags 参数
        if (inMessage.getMessage().length() < 7) {
            outMsg.setMessage("请至少携带一个 Tag 参数，像这样 !yande 参数1 参数2 | 页数 条数\n页数和条数可以不用指定");
            r.sendMessage(outMsg);
            return null;
        }

        // 处理命令前缀
        String message = inMessage.getMessage();
        message = message.substring(message.indexOf(' '));
        String[] tags;
        String[] pageNation = new String[2];

        pageNation[0] = "1";
        pageNation[1] = "10";

        // 判断是否携带分页参数
        if (message.contains("|")) {
            // 获取参数列表
            String paramOne = message.substring(0, message.indexOf("|") - 1);
            String paramTwo = message.substring(message.indexOf("|") + 2);

            tags = paramOne.trim().split(" ");
            ArrayList<String> tag_array = new ArrayList<>(Arrays.asList(tags));
            tag_array.removeIf(s -> s.equals(""));
            tags = tag_array.toArray(new String[0]);
            int index = 0;
            BooruTags booruTags = new BooruTags();
            while (index < tags.length) {
                if (tags[index].matches("[^\\x00-\\xff]+$")) {
                    booruTags.setCn_name(tags[index]);
                    List<BooruTags> booru_list = booruTagsService.findByAlter(booruTags);
                    if (booru_list.size() != 0)
                        tags[index] = booru_list.get(0).getOrigin_name();
                        // 如果无法处理则替换为 girl
                    else tags[index] = "*";
                }
                index++;
            }
            String[] temp = paramTwo.split(" ");

            // 如果只有一个分页参数代表 页数
            if (temp.length == 1) {
                pageNation[0] = temp[0];
            } else {
                pageNation = temp;
                if (Integer.parseInt(pageNation[1]) > 20)
                    pageNation[1] = "20";
            }

        } else {
            tags = message.trim().split(" ");
            ArrayList<String> tag_array = new ArrayList<>(Arrays.asList(tags));
            tag_array.removeIf(s -> s.equals(""));
            tags = tag_array.toArray(new String[0]);
            // 处理参数如果遇到中文参数则进行替换
            int index = 0;
            BooruTags booruTags = new BooruTags();
            while (index < tags.length) {
                if (tags[index].matches("[^\\x00-\\xff]+$")) {
                    booruTags.setCn_name(tags[index]);
                    List<BooruTags> booru_list = booruTagsService.findByAlter(booruTags);
                    if (booru_list.size() != 0)
                        tags[index] = booru_list.get(0).getOrigin_name();
                        // 如果无法处理则替换为 girl
                    else tags[index] = "*";
                }
                index++;
            }
        }

        // pageNation 只准接收两个参数
        if (tags.length > 4) {
            outMsg.setMessage("标签参数最大只允许 4 个");
            r.sendMessage(outMsg);
            return null;
        }

        // pageNation 只准接收两个参数
        if (pageNation.length > 2) {
            outMsg.setMessage("分页参数只允许传入 页数 和 结果数 位置 两个参数");
            r.sendMessage(outMsg);
            return null;
        }

        // 对 tag 进行中文对照

        // 处理线程安全问题
        String[] finalPageNation = pageNation;

        String[] finalTags = tags;
        new Thread(() -> {
            // 构造消息请求体
            OutMessage outMessage = new OutMessage(inMessage);
            outMessage.setMessage("[CQ:at,qq=" + inMessage.getSender().getUser_id() + "]" + inMessage.getSender().getNickname() + ", 开始检索 Yande 图片咯");
            r.sendMessage(outMessage);
            StringBuilder tagsParam = new StringBuilder();
            // 构造 Tags 参数
            for(String tag : finalTags)
                tagsParam.append(tag).append("+");

            StringBuilder uri = new StringBuilder();
            uri.append(url).append(tagsParam.toString()).append("&limit=").append(finalPageNation[1]).append("&page=").append(finalPageNation[0]);

            log.info("请求地址: " + uri.toString());
            // 使用代理请求 Yande
            RestTemplate restTemplate = new RestTemplate();
            // 借助代理请求
            if (LouiseConfig.LOUISE_PROXY_PORT > 0)
                restTemplate.setRequestFactory(new HttpProxy().getFactory(target + " API"));

            String result = restTemplate.getForObject(uri.toString(), String.class);
            JSONArray resultJsonArray = JSON.parseArray(result);

            assert resultJsonArray != null;
            if(resultJsonArray.size() == 0) {
                outMessage.setMessage("[CQ:at,qq=" + inMessage.getSender().getUser_id() + "]" + "没有找到你想要的结果呢");
                r.sendMessage(outMessage);
                return;
            }
            try {
                sendYandeResult(inMessage, resultJsonArray, Integer.parseInt(finalPageNation[1]), target);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        return null;
    }

    private JSONObject requestPopular(String uri, String target, String type, InMessage inMessage) {
        // 返回值
        JSONObject sendJson = new JSONObject();

        // 校验参数合法性
        if (!type.equals("day") && !type.equals("week") && !type.equals("month")) {
            sendJson.put("reply", target + " 功能仅支持参数 day | week | month，请这样 !" + target.toLowerCase() + "/[参数] 请求哦");
            return sendJson;
        }

        uri += type + ".json";

        String finalUri = uri;
        new Thread(() -> {
            // 构造消息请求体
            OutMessage outMessage = new OutMessage(inMessage);
            outMessage.setMessage("[CQ:at,qq=" + inMessage.getSender().getUser_id() + "]" + ", 开始请求 " + target + " 的 Every " + type + " 精选图片");
            r.sendMessage(outMessage);
            // 使用代理请求 Yande
            RestTemplate restTemplate = new RestTemplate();
            // 借助代理请求
            if (LouiseConfig.LOUISE_PROXY_PORT > 0)
                restTemplate.setRequestFactory(new HttpProxy().getFactory(target +" API"));

            String result = restTemplate.getForObject(finalUri + "?limit=" + LIMIT, String.class);
            log.info("请求 " + target + ": " + finalUri + "?limit=" + LIMIT);
            JSONArray resultJsonArray = JSON.parseArray(result);

            assert resultJsonArray != null;
            if(resultJsonArray.size() == 0) {
                outMessage.setMessage("[CQ:at,qq=" + inMessage.getSender().getUser_id() + "]" + "没有找到你想要的结果呢");
                r.sendMessage(outMessage);
                return;
            }
            try {
                sendYandeResult(inMessage, resultJsonArray, LIMIT, target);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();
        return null;
    }

    private JSONObject requestTags(String[] msg, String uri, String target, InMessage inMessage) {
        String tag = msg[1];

        // 返回值
        JSONObject returnJson = new JSONObject();

        // TODO: 总记录条数太多 会引起 QQ 风控
        uri += tag + "&limit=" + 20;
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
}
