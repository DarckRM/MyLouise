package com.darcklh.louise.Api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Model.Louise.ProcessImage;
import com.darcklh.louise.Model.Louise.Role;
import com.darcklh.louise.Model.Louise.User;
import com.darcklh.louise.Model.Result;
import com.darcklh.louise.Model.Saito.PluginInfo;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Model.SpecificException;
import com.darcklh.louise.Service.CBIRService;
import com.darcklh.louise.Service.RoleService;
import com.darcklh.louise.Service.UserService;
import com.darcklh.louise.Utils.UniqueGenerator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.darcklh.louise.Utils.isEmpty.isEmpty;

@Slf4j
@RestController
public class MyLouiseApi implements ErrorController {
    Logger logger = LoggerFactory.getLogger(MyLouiseApi.class);

    @Autowired
    List<PluginInfo> pluginInfoList;

    @Autowired
    private SendPictureApi sendPictureApi;

    @Autowired
    private R r;

    @Autowired
    private SearchPictureApi searchPictureApi;

    @Autowired
    LouiseConfig louiseConfig;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private CBIRService cbirService;

    /**
     * 插件调用中心
     * @param plugin
     * @param message
     * @return
     */
    @RequestMapping("/louise/p/{plugin}")
    public JSONObject pluginsCenter(@PathVariable String plugin, @RequestBody JSONObject message) {
        logger.info("rua");

        return null;
    }

    /**
     * 刷新配置
     */
    @RequestMapping("/louise/show")
    public String refreshConfig() {
        return "API: " + louiseConfig.getBOT_LOUISE_CACHE_IMAGE();
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
        reply.put("reply", userService.banUser(user_id));
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

    /**
     * 查询当前系统配置
     * @return
     */
    @RequestMapping("louise/config")
    public JSONObject queryConfig() {
        JSONObject reply = new JSONObject();
        reply.put("reply", louiseConfig.toString());
        return reply;
    }

    @RequestMapping("louise/test")
    public String testRequestCenter(HttpServletRequest request, @RequestBody String message) throws NoSuchAlgorithmException {
        JSONObject result = new JSONObject();
        result.put("reply","现在测试中");
        return result.toString() + louiseConfig.getLOUISE_ERROR_UNKNOWN_COMMAND();

    }

    @RequestMapping("louise/meta")
    public JSONObject metaRequestCenter() {
        return null;
    }

    /**
     * 注册新用户
     * @param jsonObject
     * @return
     */
    @RequestMapping("louise/join")
    public JSONObject join(@RequestBody JSONObject jsonObject) {

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
            return userService.joinLouise(user_id, group_id);
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
        userService.updateCount(user_id,1);
        return sendPictureApi.sendPicture(number, nickname, senderType, message);
    }

    /**
     * 根据图片以及参数调用识图接口
     * @param message
     * @return
     */
    @RequestMapping("louise/find")
    private JSONObject findPicture(@RequestBody JSONObject message) {

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

        //TODO 可能线程不安全
        r.setNickname(nickname);
        r.setSenderType(senderType);
        r.setNumber(number);
        r.setMessage(message);
        searchPictureApi.setUploadImgUrl(url);

        logger.info("上传图片的地址:"+ url);
        //封装信息
        new Thread(() -> searchPictureApi.searchPictureCenter(message, r)).start();

        returnJson.put("reply", nickname+"!露易丝在搜索了哦！" +
                "\n目前Ascii2d搜索引擎仍在测试中，受网络影响较大！");
        return returnJson;

    }

    @RequestMapping("louise/search")
    private JSONObject searchPicture(@RequestBody JSONObject message) {

        //返回值
        JSONObject returnJson = new JSONObject();
        //解析上传的信息 拿到图片URL还有一些相关参数
        String uri = message.getString("message");
        uri = uri.substring(uri.indexOf("url=")+4, uri.length()-1);
        //获取请求元数据信息
        String message_type = message.getString("message_type");
        String number = "";
        String nickname = message.getJSONObject("sender").getString("nickname");

        URL url = null;
        String filePath = louiseConfig.getLOUISE_CACHE_IMAGE_LOCATION() + "/";
        String fileName = "Image_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "_" + UniqueGenerator.generateShortUuid() + "." + "jpg";
        String imageName = filePath + fileName;
        try {
            log.info("开始下载" + imageName + " 图片地址: " + uri);
            url = new URL(uri);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(new File(imageName));
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;
            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
            output.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            returnJson.put("reply", "下载图片失败了，请检查命令是否包含图片");
            return returnJson;
        } catch (IOException e) {
            log.info("下载图片" + imageName + "失败");
            returnJson.put("reply", "获取上传图片失败");
            return returnJson;
        }

        // 下载成功开始执行搜索任务
        try {
            returnJson.put("result", cbirService.compareImageCompress(imageName));
        } catch (Exception e) {
            returnJson.put("reply", "检索图片失败了");
            return returnJson;
        }
        StringBuilder hiList = new StringBuilder();
        JSONArray hiArray = returnJson.getJSONObject("result").getJSONArray("result_hiList");

        for (Object o : hiArray) {
            JSONObject jsonObj = (JSONObject) o;
            hiList.append("[CQ:image,file=http://127.0.0.1:8099/saito/image").append(jsonObj.getString("image_name")).append("]\n");
        }

        returnJson.put("reply", "搜索出来了，按准确度从高到低排列" +
                "\n" + hiList);

        return returnJson;
    }

    @RequestMapping("louise/pid/{pixiv_id}")
    private JSONObject findPixivId(@PathVariable String pixiv_id, @RequestBody JSONObject message) {
        //返回值
        JSONObject returnJson = new JSONObject();
        String nickname = message.getJSONObject("sender").getString("nickname");

        returnJson.put("reply", nickname + "，你要的图片" + pixiv_id + "找到了" +
                "\n[CQ:image,file=" +louiseConfig.getPIXIV_PROXY_URL() + pixiv_id + ".jpg]" +
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

        JSONObject returnJson = new JSONObject();

        User user = userService.selectById(user_id);
        Role role = roleService.selectById(user.getRole_id());
        if (isEmpty(user)) {
            returnJson.put("reply", "没有你的信息诶");
        } else {
            String nickname = user.getNickname();
            Timestamp create_time = user.getCreate_time();
            Integer count_setu = user.getCount_setu();
            Integer count_upload = user.getCount_upload();
            returnJson.put("reply", nickname + "，你的个人信息" +
                    "\n总共请求功能次数：" + count_setu +
                    "\n总共上传文件次数：" + count_upload +
                    "\n在露易丝这里注册的时间；" + create_time +
                    "\n-----------DIVIDER LINE------------" +
                    "\n你的权限级别：<" + role.getRole_name() + ">" +
                    "\n剩余CREDIT：" + user.getCredit() +
                    "\nCREDIT BUFF：" + user.getCredit_buff()
            );
        }
        return returnJson;

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
        reply.put("reply", "你上传的文件已经记录下来了");
        return reply;
    }
}
