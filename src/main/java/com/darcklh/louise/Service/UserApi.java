package com.darcklh.louise.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Api.MyLouiseApi;
import com.darcklh.louise.Mapper.UserDao;
import com.darcklh.louise.Model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * @author DarckLH
 * @date 2021/8/7 19:14
 * @Description 用户信息相关接口
 */
@Service
public class UserApi {

    Logger logger = LoggerFactory.getLogger(UserApi.class);

    @Autowired
    private UserDao userDao;

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

}
