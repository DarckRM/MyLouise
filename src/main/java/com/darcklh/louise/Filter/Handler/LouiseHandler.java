package com.darcklh.louise.Filter.Handler;

import com.alibaba.fastjson.JSON;
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
import com.darcklh.louise.Service.FeatureInfoService;
import com.darcklh.louise.Service.GroupService;
import com.darcklh.louise.Service.Impl.GroupImpl;
import com.darcklh.louise.Service.Impl.UserImpl;
import com.darcklh.louise.Service.UserService;
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
    UserService userService;

    @Autowired
    GroupService groupService;

    @Autowired
    GroupDao groupDao;

    @Autowired
    FeatureInfoService featureInfoService;

    @Autowired
    R r;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        logger.info("拦截器合法性校验");

        String command = request.getRequestURI();

        //对command预处理
        if (command.contains("/louise/pid/"))
            command = "/louise/pid/{pid}";

        //对command预处理
        if (command.contains("/louise/yande/"))
            command = "/louise/yande/{type}";

        response.setContentType("application/json; charset=utf-8");

        //获取相关信息
        HttpServletWrapper wrapper = new HttpServletWrapper(request);
        String body = wrapper.getBody();
        logger.debug("拦截器请求Body: " + body);

        JSONObject jsonObject = JSONObject.parseObject(body);
        String group_id = jsonObject.getString("group_id");
        String user_id = jsonObject.getJSONObject("sender").getString("user_id");

        Boolean tag = false;

        //放行join
        if (command.equals("/louise/join")) {
            return true;
        }

        //放行help
        if (command.equals("/louise/help")) {
            return true;
        }

        //放行group_join
        if (command.equals("/louise/group_join")) {
            return true;
        }

        int isAvaliable = userService.isUserAvaliable(user_id);

        //判断用户是否存在并启用
        if (isAvaliable == 0) {
            return returnFalseMessage(LouiseConfig.LOUISE_ERROR_UNKNOWN_USER, "未登记的用户" + user_id, response);
        } else if (isAvaliable == -1) {
            return returnFalseMessage(LouiseConfig.LOUISE_ERROR_BANNED_USER, "未启用的用户" + user_id, response);
        }

        //判断群是否启用
        if (group_id != null && !groupService.isGroupEnabled(group_id)) {
            return returnFalseMessage("主人不准露易丝在这个群里说话哦", "未启用的群组: " + group_id, response);
        }

        //获取请求的功能对象
        FeatureInfo featureInfo = featureInfoService.findWithFeatureURL(command);

        try {
            //判断功能是否启用
            if (featureInfo.getIs_enabled()!=1) {
                return returnFalseMessage("功能<" + featureInfo.getFeature_name() + ">未启用", "功能未启用: " + group_id, response);
            }
        } catch (Exception e) {
            return returnFalseMessage("未知的命令", "请求未知命令"+command, response);
        }

        //判断群是否具有请求权限
        if(!isEmpty.isEmpty(group_id)) {
            Group group = groupService.selectById(group_id);

            List<FeatureInfoMin> featureInfoMins = featureInfoService.findWithRoleId(group.getRole_id());
            logger.info("群聊允许的功能列表: " +featureInfoMins);
            for ( FeatureInfoMin featureInfoMin: featureInfoMins) {
                if (featureInfoMin.getFeature_id().equals(featureInfo.getFeature_id())) {
                    tag = true;
                    break;
                }
            }
            if (!tag)
                return returnFalseMessage("这个群聊的权限不准用这个功能哦", "群" + group_id +"权限不足", response);
        } else {
            tag = true;
        }

        if(tag) {
            tag = false;
            User user = userService.selectById(user_id);

            List<FeatureInfoMin> featureInfoMins = featureInfoService.findWithRoleId(user.getRole_id());
            logger.info("用户允许的功能列表: " +featureInfoMins);
            for ( FeatureInfoMin featureInfoMin: featureInfoMins) {
                if (featureInfoMin.getFeature_id().equals(featureInfo.getFeature_id())) {
                    tag = true;
                    break;
                }
            }
            if (!tag)
                return returnFalseMessage("你的权限还不准用这个功能哦", "用户" + user_id +"权限不足", response);
        }

        //合法性校验通过 扣除CREDIT
        int credit = userService.minusCredit(user_id, featureInfo.getCredit_cost());
        if (credit < 0) {
            return returnFalseMessage("你的CREDIT余额不足哦", "用户 " + user_id + " CREDIT不足", response);
        }

        logger.info("功能 " +featureInfo.getFeature_name() + " 消耗用户 " + user_id +" CREDIT " + featureInfo.getCredit_cost());

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }

    public Boolean returnFalseMessage(String msg, String log, HttpServletResponse response) throws Exception{
        PrintWriter writer = response.getWriter();
        JSONObject returnJson = new JSONObject();
        logger.info(log);
        returnJson.put("reply", msg);
        writer.print(returnJson);
        writer.close();
        return false;
    }

}
