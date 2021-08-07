package com.darcklh.louise.Api;

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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.security.NoSuchAlgorithmException;

@Controller
@RequestMapping("/louise")
public class MyLouiseApi {
    Logger logger = LoggerFactory.getLogger(MyLouiseApi.class);

    @Autowired
    private SendPictureApi sendPictureApi;

    @Autowired
    private SearchPictureApi searchPictureApi;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserApi userApi;

    @Autowired
    private EncryptUtils encryptUtils;

    //机器人上报密钥
    @Value("${HTTP_POST_KEY}")
    String HTTP_POST_KEY;

    @RequestMapping("/test")
    @ResponseBody
    public JSONObject testRequestProcessCenter(HttpServletRequest request, @RequestBody String message) throws NoSuchAlgorithmException {

        String cryptCode = request.getHeader("X-signature");
        String encryptCode = encryptUtils.genHMAC(request.toString(), HTTP_POST_KEY);

        logger.debug(cryptCode);
        logger.debug(encryptCode);

        JSONObject result = new JSONObject();
        result.put("reply","现在测试中");
        return result;

    }

    @RequestMapping("/bot")
    @ResponseBody
    public JSONObject requestProcessCenter(@RequestBody String request) {

        JSONObject jsonObject = JSONObject.parseObject(request);
        //快速返回
        JSONObject returnJson = new JSONObject();
        String post_type = jsonObject.getString("post_type");
        String user_id = jsonObject.getString("user_id");
        String group_id = jsonObject.getString("group_id");
        String raw_message = jsonObject.getString("raw_message");
        //排除心跳检测
        if (post_type.equals("meta_event")) {
            logger.debug("心跳检测");
            return null;
        }

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

        //判断用户是否已注册
        if (0 == userDao.selectById(jsonObject.getString("user_id"))) {
            returnJson.put("reply", "你还没有在露易丝这里留下你的记录哦。" +
                    "\n请使用!join");
            return returnJson;
        }
        logger.info("上报类型: " + post_type);
        switch (post_type) {
            case "message": return handleMessagePost(jsonObject);
            //case "notice": return handleNoticePost(jsonObject);
        }
        return null;
    }

    /**
     * 预处理Message类型上报
     * @param message
     * @return JSONObject
     */
    private JSONObject handleMessagePost(JSONObject message) {

        //获取请求元数据信息
        String message_type = message.getString("message_type");
        String command = message.getString("raw_message").substring(1).split(" ")[0];
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

        //默认返回值
        JSONObject defaultResult = new JSONObject();
        defaultResult.put("reply", "没有听懂诶，如果需要帮助的话请说!help");

        switch (command) {
            //处理默认信息
            default: return defaultResult;
            //TODO 暂时先请求网络图片 Linux和Windows对于本地路径的解析不同 很烦
            //case "help": defaultResult.put("reply", "[CQ:image,file=file:///data/MyLouiseResource/louise_help.jpg]"); return defaultResult;
            case "help": defaultResult.put("reply", "[CQ:image,file=https://chenjie.ink:8096/file/images/Image_20210807215100047_V10M.jpg]"); return defaultResult;
            //调用LoliconAPI随机或根据参数请求色图
            case "setu": return sendPictureApi.sendPicture(number, nickname, senderType, message);
            //TODO 完善其它图库的返回结果
            //调用识图API根据上传图片进行识图
            case "find": return searchPictureApi.findWithSourceNAO(number, nickname, senderType, message);
        }
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
