package com.darcklh.louise.Api;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Model.Plugin;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Service.SearchPictureApi;
import com.darcklh.louise.Service.SendPictureApi;
import com.darcklh.louise.Service.Impl.UserImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
public class MyLouiseApi implements ErrorController {
    Logger logger = LoggerFactory.getLogger(MyLouiseApi.class);

    @Autowired
    List<Plugin> pluginList;

    @Autowired
    private SendPictureApi sendPictureApi;

    @Autowired
    private R r;

    @Autowired
    private SearchPictureApi searchPictureApi;

    @Autowired
    LouiseConfig louiseConfig;

    @Autowired
    private UserImpl userImpl;

    /**
     * 插件调用中心
     * @param plugin
     * @param message
     * @return
     */
    @RequestMapping("/louise/p/{plugin}")
    public JSONObject PluginsCenter(@PathVariable String plugin, @RequestBody JSONObject message) {
        logger.info("rua");

        return null;
    }

    //TODO 接管了所有的请求错误 需要修改
    @RequestMapping("/error")
    public JSONObject Error() {
        JSONObject returnJson = new JSONObject();
        //如果请求失败了
        logger.debug("未知的命令请求");
        returnJson.put("reply", louiseConfig.getLOUISE_ERROR_UNKNOWN_COMMAND());
        return r.fastResponse(returnJson);
    }

    /**
     * 刷新配置的接口
     */
    @RequestMapping("/louise/show")
    public String RefreshConfig() {
        return "API: " + louiseConfig.getBOT_LOUISE_CACHE_IMAGE();
    }

    @RequestMapping("/louise/refresh")
    public String refresh() {
        louiseConfig.setBOT_LOUISE_CACHE_IMAGE("aaaa");
        //contextRefresher.refresh();
        return louiseConfig.toString();
    }

    /**
     * 返回帮助信息
     * @return
     */
    @RequestMapping("louise/help")
    public JSONObject help() {
        JSONObject returnJson = new JSONObject();
        //TODO 暂时先请求网络图片 Linux和Windows对于本地路径的解析不同 很烦
        returnJson.put("reply","[CQ:image,file="+louiseConfig.getLOUISE_HELP_PAGE()+"]");
        return returnJson;
    }

    /**
     * 封禁用户
     * @param message
     * @return
     */
    @RequestMapping("louise/ban")
    public JSONObject banUser(@RequestBody JSONObject message) {
        JSONObject reply = new JSONObject();
        String admin = message.getString("user_id");
        if (!admin.equals(louiseConfig.getLOUISE_ADMIN_NUMBER())) {
            reply.put("reply", "管理员限定");
            return reply;
        }
        String user_id = message.getString("message").substring(5);
        reply.put("reply", userImpl.banUser(user_id));
        return reply;
    }

    @RequestMapping("louise/config/{type}")
    public JSONObject modifyConfig(@RequestBody JSONObject message, @PathVariable Integer type) {
        JSONObject reply = new JSONObject();
        String admin = message.getString("user_id");
        if (!admin.equals(louiseConfig.getLOUISE_ADMIN_NUMBER())) {
            reply.put("reply", "管理员限定");
            return reply;
        }
        if (type == 0) {
            louiseConfig.setBOT_LOUISE_CACHE_IMAGE("../../../../MyLouise/cache/images/");
            logger.info("切换至 本地开发 配置");
            reply.put("reply", "切换到本地开发环境");
        } else {
            louiseConfig.setBOT_LOUISE_CACHE_IMAGE("../../MyLouise/cache/images/");
            logger.info("切换至 线上部署 配置");
            reply.put("reply", "切换到服务部署环境");
        }
        return reply;
    }

    @RequestMapping("louise/test")
    public String testRequestProcessCenter(HttpServletRequest request, @RequestBody String message) throws NoSuchAlgorithmException {
        JSONObject result = new JSONObject();
        result.put("reply","现在测试中");
        return result.toString() + louiseConfig.getLOUISE_ERROR_UNKNOWN_COMMAND();

    }

    @RequestMapping("louise/meta")
    public JSONObject requestProcessCenter() {
        return null;
    }

    /**
     * 注册新用户
     * @param jsonObject
     * @return
     */
    @RequestMapping("louise/join")
    public JSONObject Join(@RequestBody JSONObject jsonObject) {

        String user_id = jsonObject.getString("user_id");
        String group_id = jsonObject.getString("group_id");

        //快速返回
        JSONObject returnJson = new JSONObject();

        //注册用户
        if (jsonObject.getString("raw_message").substring(1).split(" ")[0].equals("join")) {
            //判断如果是私聊禁止注册
            if (jsonObject.getString("group_id") == null) {
                //TODO go-cqhttp只能根据qq号和群号获取某个用户的信息 但是这会导致数据库中使用双主键 比较麻烦 后期解决一下这个问题
                returnJson.put("reply","露易丝不支持私聊注册哦，\n请在群聊里使用吧");
                return returnJson;
            }
            return userImpl.joinLouise(user_id, group_id);
        }
        return null;
    }

    /**
     * 发送随机色图
     * @param message
     * @return JSONObject
     */
    @RequestMapping("louise/setu")
    private JSONObject sendRandomSetu(@RequestBody JSONObject message) {

        //获取请求元数据信息
        String message_type = message.getString("message_type");
        String number = "";
        String nickname = message.getJSONObject("sender").getString("nickname");
        //TODO 有待优化的变量
        String user_id = message.getString("user_id");

        //判断私聊或是群聊
        String senderType = "";
        if (message_type.equals("group")) {
            number = message.getString("group_id");
            senderType = "group_id";

        } else if (message_type.equals("private")) {
            number = message.getString("user_id");
            senderType = "user_id";
        }

        //调用LoliconAPI随机或根据参数请求色图
        userImpl.updateCount(user_id,1);
        return sendPictureApi.sendPicture(number, nickname, senderType, message);
    }

    /**
     * 根据图片以及参数调用识图接口
     * @param message
     * @return
     */
    @RequestMapping("louise/find")
    private JSONObject findPicture(@RequestBody JSONObject message) {

        R r = new R();

        //返回值
        JSONObject returnJson = new JSONObject();
        //解析上传的信息 拿到图片URL还有一些相关参数
        String url = message.getString("message");
        url = url.substring(url.indexOf("url=")+4, url.length()-1);
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

        r.setNickname(nickname);
        r.setSenderType(senderType);
        r.setNumber(number);

        //封装信息
        r.put("url", url);
        r.put(r.getSenderType(), r.getNumber());

        new Thread(() -> searchPictureApi.searchPictureCenter(message, r)).start();

        returnJson.put("reply", nickname+"!露易丝在搜索了哦！" +
                "\n目前Ascii2d搜索引擎仍在测试中，受网络影响较大！");
        return returnJson;

    }

    @RequestMapping("louise/pid/{pixiv_id}")
    private JSONObject findPixivId(@PathVariable String pixiv_id, @RequestBody JSONObject message) {
        //返回值
        JSONObject returnJson = new JSONObject();
        String nickname = message.getJSONObject("sender").getString("nickname");

        returnJson.put("reply", nickname + "，你要的图片" + pixiv_id + "找到了" +
                "\n[CQ:image,file=https://pixiv.cat/" + pixiv_id + ".jpg]" +
                "\n如果未显示出图片请在pixiv_id后指定第几张作品");

        return returnJson;
    }

    /**
     * 预处理Notice类型上报
     * @param notice
     * @return JSONObject
     */
    private JSONObject handleNoticePost(JSONObject notice) {

        //获取Notice上报元数据
        String notice_type = notice.getString("notice_type");
        String user_id = notice.getString("user_id");
        logger.info("提醒类上报的类型: " + notice_type);
        //判断Notice的类型
        switch (notice_type) {
            default: return null;
            case "group_upload": return initFileUploadInfo(notice, user_id);
        }
    }

    /**
     * 查询用户信息
     * @param message
     * @return
     */
    @RequestMapping("louise/myinfo")
    public JSONObject myInfo(@RequestBody JSONObject message) {

        String user_id = message.getString("user_id");
        return userImpl.myInfo(user_id);

    }

    /**
     * 初次上传文件写入记录
     * @param notice JSONObject
     * @return JSONObject
     */

    private JSONObject initFileUploadInfo(JSONObject notice, String user_id) {

        logger.info("进入初始化上传流程");
        String nickname = user_id;

        //构造快速操作返回信息
        JSONObject reply = new JSONObject();
        reply.put("reply", "你上传的文件已经确实记录下来了");
        return reply;
    }
}
