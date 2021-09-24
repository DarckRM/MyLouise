package com.darcklh.louise.Service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Mapper.UserDao;
import com.darcklh.louise.Model.Louise.User;
import com.darcklh.louise.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.List;

import static com.darcklh.louise.Utils.isEmpty.isEmpty;

/**
 * @author DarckLH
 * @date 2021/8/7 19:14
 * @Description 用户信息相关接口
 */
@Service
public class UserImpl implements UserService {

    Logger logger = LoggerFactory.getLogger(UserImpl.class);

    @Autowired
    public UserDao userDao;

    public JSONObject joinLouise(String user_id, String group_id) {

        logger.info("进入注册流程");
        logger.info("用户来自群: " + group_id + " QQ号: " + user_id);
        //构造Rest请求模板
        RestTemplate restTemplate = new RestTemplate();
        //请求go-cqhhtp的参数和请求头
        HttpHeaders headers= new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject jsonObject = new JSONObject();
        //构造请求体
        JSONObject userInfo = new JSONObject();
        userInfo.put("user_id", user_id);
        userInfo.put("group_id", group_id);

        //请求bot获取用户信息
        JSONObject result = JSON.parseObject(restTemplate.postForObject("http://localhost:5700/get_group_member_info", userInfo, String.class));
        logger.info(result.toString());

        User user = new User();
        result = result.getJSONObject("data");

        user.setGroup_id(group_id);
        user.setUser_id(user_id);
        user.setNickname(result.getString("nickname"));
        user.setAvatar("https://q1.qlogo.cn/g?b=qq&nk=" + user_id + "&s=640");
        user.setCredit(10000);
        user.setCredit_buff(0);
        user.setCount_setu(0);
        user.setCount_upload(0);
        //TODO 没搞懂腾讯返回的时间格式 日后再搞
        //user.setJoin_time(result.getTimestamp("join_time"));
        try {
            if (userDao.insert(user) == 0)
                jsonObject.put("reply","注册失败了，遗憾！请稍后再试吧");
            else
                jsonObject.put("reply","注册成功了！请输入!help获得进一步帮助");
        } catch (Exception e) {
            if (e.getCause() instanceof java.sql.SQLIntegrityConstraintViolationException) {
                logger.debug("用户"+user_id+"重复注册了");
                jsonObject.put("reply","你已经注册过了哦！");
            }
        }

        return jsonObject;
    }

    /**
     * 根据用户qq查询相关用户信息
     * @param user_id String
     * @return
     */
    public JSONObject myInfo(String user_id) {

        JSONObject returnJson = new JSONObject();

        User user = userDao.selectById(user_id);
        if (isEmpty(user)) {
            returnJson.put("reply", "没有你的信息诶");
        } else {
            String nickname = user.getNickname();
            Timestamp create_time = user.getCreate_time();
            Integer count_setu = user.getCount_setu();
            Integer count_upload = user.getCount_upload();
            returnJson.put("reply", nickname + "，你的个人信息" +
                    "\n总共请求涩图次数：" + count_setu +
                    "\n总共上传文件次数：" +count_upload);
        }
        return returnJson;
    }

    /**
     * 更新用户某类数据
     * @param user_id String 用户qq
     * @param option String 某个字段
     */
    public void updateCount(String user_id, int option) {

        switch (option) {
            case 1: userDao.updateCountSetu(user_id); return;
            case 2: userDao.updateCountUpload(user_id); return;
        }

    }

    public String banUser(String user_id) {
        String reply = "变更状态失败";
        if (userDao.banUser(user_id) == 1) {
            reply = isUserEnabled(user_id) ? "用户"+user_id+"已解封" : "用户"+user_id+"已暂时烟了";
        }
        return reply;
    }

    public boolean isUserExist(String user_id) {
        //判断用户是否已注册
        if (userDao.isUserExist(user_id) == 0)
            return false;
        return true;
    }

    public boolean isUserEnabled(String user_id) {
        //判断用户是否启用
        if (userDao.isUserEnabled(user_id) <= 0)
            return false;
        return true;
    }

    @Override
    public List<User> findAll() {
        return userDao.selectList(null);
    }
}
