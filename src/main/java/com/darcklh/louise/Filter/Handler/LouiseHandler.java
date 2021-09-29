package com.darcklh.louise.Filter.Handler;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Mapper.FeatureInfoDao;
import com.darcklh.louise.Mapper.GroupDao;
import com.darcklh.louise.Mapper.RoleDao;
import com.darcklh.louise.Mapper.UserDao;
import com.darcklh.louise.Model.Louise.Group;
import com.darcklh.louise.Model.Louise.User;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Model.Saito.FeatureInfo;
import com.darcklh.louise.Model.VO.FeatureInfoMin;
import com.darcklh.louise.Service.Impl.GroupImpl;
import com.darcklh.louise.Service.Impl.UserImpl;
import com.darcklh.louise.Utils.HttpServletWrapper;
import com.darcklh.louise.Utils.isEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author DarckLH
 * @date 2021/8/12 12:41
 * @Description
 */
@Component
public class LouiseHandler implements HandlerInterceptor {

    Logger logger = LoggerFactory.getLogger(LouiseHandler.class);

    @Autowired
    LouiseConfig louiseConfig;

    @Autowired
    UserImpl userImpl;

    @Autowired
    UserDao userDao;

    @Autowired
    GroupImpl groupImpl;

    @Autowired
    GroupDao groupDao;

    @Autowired
    FeatureInfoDao featureInfoDao;

    @Autowired
    R r;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        logger.info("拦截器合法性校验");
        JSONObject returnJson = new JSONObject();
        String command = request.getRequestURI();

        //对command预处理
        if (command.contains("/louise/pid/"))
            command = "/louise/pid/{pid}";

        response.setContentType("application/json; charset=utf-8");

        //获取相关信息
        HttpServletWrapper wrapper = new HttpServletWrapper(request);
        String body = wrapper.getBody();
        logger.debug("拦截器请求Body: " + body);

        JSONObject jsonObject = JSONObject.parseObject(body);
        String group_id = jsonObject.getString("group_id");
        String user_id = jsonObject.getJSONObject("sender").getString("user_id");
        Integer role_id = 0;

        //判断是否具有请求权限
        if (!isEmpty.isEmpty(group_id)) {
            Group group = groupDao.selectById(group_id);
            role_id = group.getRole_id();
            returnJson.put("reply", "当前群聊的权限不准用这个功能哦");
        } else {
            User user = userDao.selectById(user_id);
            role_id = user.getRole_id();
            returnJson.put("reply", "你的权限还不准用这个功能哦");
        }
        FeatureInfo featureInfo = featureInfoDao.findWithFeatureURL(command);

        //判断功能是否启用
        if (featureInfo.getIs_enabled()!=1) {
            PrintWriter writer = response.getWriter();
            logger.debug("功能未启用: " + group_id);
            returnJson.put("reply", "功能<" + featureInfo.getFeature_name() + ">未启用");
            writer.print(returnJson);
            writer.close();
            return false;
        }
        Boolean tag = false;
        List<FeatureInfoMin> featureInfoMins = featureInfoDao.findWithRoleId(role_id);
        for ( FeatureInfoMin featureInfoMin: featureInfoMins) {
            if (featureInfoMin.getFeature_id().equals(featureInfo.getFeature_id()))
                tag = true;
        }

        if (!tag) {
            PrintWriter writer = response.getWriter();
            logger.debug("权限不足: " + group_id);
            writer.print(returnJson);
            writer.close();
            return false;
        }

        if (command.equals("/louise/help")) {
            return true;
        }

        if (!groupImpl.isGroupExist(group_id) && group_id != null) {
            PrintWriter writer = response.getWriter();
            logger.debug("未启用的群组: " + group_id);
            returnJson.put("reply", "主人不准露易丝在这个群里说话哦");
            writer.print(returnJson);
            writer.close();
            return false;
        }


        if (!jsonObject.getString("raw_message").equals("!join")) {
            if (!userImpl.isUserExist(user_id)) {
                PrintWriter writer = response.getWriter();
                logger.debug("未登记的用户: " + user_id);
                returnJson.put("reply", louiseConfig.getLOUISE_ERROR_UNKNOWN_USER());
                writer.print(returnJson);
                writer.close();
                return false;
            }
            //判断用户是否启用
            if (!userImpl.isUserEnabled(user_id)) {
                PrintWriter writer = response.getWriter();
                logger.debug("未启用的用户: " + user_id);
                returnJson.put("reply", louiseConfig.getLOUISE_ERROR_BANNED_USER());
                writer.print(returnJson);
                writer.close();
                return false;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }

}
