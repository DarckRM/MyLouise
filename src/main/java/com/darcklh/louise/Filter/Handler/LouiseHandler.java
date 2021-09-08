package com.darcklh.louise.Filter.Handler;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Service.UserApi;
import com.darcklh.louise.Utils.HttpServletWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

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
    UserApi userApi;
    @Autowired
    R r;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        logger.info("拦截器合法性校验");
        JSONObject returnJson = new JSONObject();
        String command = request.getRequestURI();

        response.setContentType("application/json; charset=utf-8");
        //如果请求失败了
        if (command.equals("/error")) {
            PrintWriter writer = response.getWriter();
            logger.debug("未知的命令请求: "+command);
            returnJson.put("reply", louiseConfig.getLOUISE_ERROR_UNKNOWN_COMMAND());
            writer.print(returnJson);
            writer.close();
            return false;
        }
        //获取相关信息
        HttpServletWrapper wrapper = new HttpServletWrapper(request);
        String body = wrapper.getBody();
        logger.debug("拦截器请求Body: " + body);

        JSONObject jsonObject = JSONObject.parseObject(body);
        String user_id = jsonObject.getJSONObject("sender").getString("user_id");

        if (command.equals("/join")) {
            return true;
        }

        if (!userApi.isUserExist(user_id)) {
            PrintWriter writer = response.getWriter();
            logger.debug("未登记的用户: " + user_id);
            returnJson.put("reply", louiseConfig.getLOUISE_ERROR_UNKNOWN_USER());
            writer.print(returnJson);
            writer.close();
            return false;
        }
        //判断用户是否启用
        if (!userApi.isUserEnabled(user_id)) {
            PrintWriter writer = response.getWriter();
            logger.debug("未启用的用户: " + user_id);
            returnJson.put("reply", louiseConfig.getLOUISE_ERROR_UNKNOWN_COMMAND());
            writer.print(returnJson);
            writer.close();
            return false;
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
