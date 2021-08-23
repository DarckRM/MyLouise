package com.darcklh.louise.Api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Mapper.UserDao;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Service.SearchPictureApi;
import com.darcklh.louise.Service.SendPictureApi;
import com.darcklh.louise.Service.UserApi;
import com.darcklh.louise.Utils.EncryptUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;

@RestController
public class MyLouiseApi implements ErrorController {
    Logger logger = LoggerFactory.getLogger(MyLouiseApi.class);

    @Autowired
    private SendPictureApi sendPictureApi;

    @Autowired
    private SearchPictureApi searchPictureApi;

    @Autowired
    private UserApi userApi;

    @Autowired
    private EncryptUtils encryptUtils;

    //机器人上报密钥
    @Value("${HTTP_POST_KEY}")
    String HTTP_POST_KEY;

    //帮助页地址
    @Value("${LOUISE_HELP_PAGE}")
    String LOUISE_HELP_PAGE;

    //管理员QQ
    @Value("${LOUISE_ADMIN_NUMBER}")
    String LOUISE_ADMIN_NUMBER;

    @RequestMapping("/error")
    public JSONObject commandError() {
        return null;
    }

    /**
     * 返回帮助信息
     * @return
     */
    @RequestMapping("/help")
    public JSONObject help() {
        JSONObject returnJson = new JSONObject();
        //TODO 暂时先请求网络图片 Linux和Windows对于本地路径的解析不同 很烦
        returnJson.put("reply","[CQ:image,file="+LOUISE_HELP_PAGE+"]");
        return returnJson;
    }

    /**
     * 封禁用户
     * @param message
     * @return
     */
    @RequestMapping("/ban")
    public JSONObject banUser(@RequestBody JSONObject message) {
        JSONObject reply = new JSONObject();
        String admin = message.getString("user_id");
        if (!admin.equals(LOUISE_ADMIN_NUMBER)) {
            reply.put("reply", "我只认"+LOUISE_ADMIN_NUMBER+"这个账号哦");
            return reply;
        }
        String user_id = message.getString("message").substring(5);
        reply.put("reply",userApi.banUser(user_id));
        return reply;
    }

    @RequestMapping("/test")
    public JSONObject testRequestProcessCenter(HttpServletRequest request, @RequestBody String message) throws NoSuchAlgorithmException {

        String cryptCode = request.getHeader("X-signature");
        String encryptCode = encryptUtils.hamcsha1(message +"\n", HTTP_POST_KEY);
        logger.info("HTTP明文: " + message);
        logger.info(cryptCode);
        logger.info(encryptCode);

        JSONObject result = new JSONObject();
        result.put("reply","现在测试中");
        return result;

    }

    @RequestMapping("/meta")
    public JSONObject requestProcessCenter() {
        return null;
    }

    /**
     * 注册新用户
     * @param jsonObject
     * @return
     */
    @RequestMapping("/join")
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
            return userApi.joinLouise(user_id, group_id);
        }
        return null;
    }

    /**
     * 发送随机色图
     * @param message
     * @return JSONObject
     */
    @RequestMapping("/setu")
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
        userApi.updateCount(user_id,1);
        return sendPictureApi.sendPicture(number, nickname, senderType, message);
    }

    /**
     * 根据图片以及参数调用识图接口
     * @param message
     * @return
     */
    @RequestMapping("/find")
    private JSONObject findPicture(@RequestBody JSONObject message) {
        //返回值
        JSONObject returnJson = new JSONObject();
        String nickname = message.getJSONObject("sender").getString("nickname");

        new Thread(new Runnable() {
            @Override
            public void run() {
                searchPictureApi.searchPictureCenter(message);
            }
        }).start();

        returnJson.put("reply", nickname+"!露易丝在搜索了哦！" +
                "\n目前Ascii2d搜索引擎仍在测试中，受网络影响较大！");
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
    @RequestMapping("/myinfo")
    public JSONObject myInfo(@RequestBody JSONObject message) {

        String user_id = message.getString("user_id");
        return userApi.myInfo(user_id);

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
