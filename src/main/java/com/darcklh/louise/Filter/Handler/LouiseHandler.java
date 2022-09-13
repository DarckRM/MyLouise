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
import com.darcklh.louise.Model.Messages.InMessage;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Model.ReplyException;
import com.darcklh.louise.Model.Saito.FeatureInfo;
import com.darcklh.louise.Model.Saito.PluginInfo;
import com.darcklh.louise.Model.VO.FeatureInfoMin;
import com.darcklh.louise.Service.*;
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
    UserService userService;

    @Autowired
    GroupService groupService;

    @Autowired
    GroupDao groupDao;

    @Autowired
    FeatureInfoService featureInfoService;

    @Autowired
    PluginInfoService pluginInfoService;

    @Autowired
    R r;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        logger.info("拦截器合法性校验");

        //获取相关信息
        HttpServletWrapper wrapper = new HttpServletWrapper(request);
        String body = wrapper.getBody();
        logger.debug("拦截器请求Body: " + body);

        InMessage inMessage = JSONObject.parseObject(body).toJavaObject(InMessage.class);

        String group_id = inMessage.getGroup_id().toString();
        String user_id = inMessage.getSender().getUser_id().toString();
        String message = inMessage.getMessage();

        //对command预处理
        String[] commands = message.split(" ");
        String command = commands[0];
        if (commands.length > 1)
            command += " %";
        else {
            if (command.contains("/"))
                command = command.substring(0, command.indexOf("/") + 1) + "{%";
        }
        boolean tag = false;

        // 获取请求的功能对象
        FeatureInfo featureInfo = featureInfoService.findWithFeatureCmd(command);
        // 如果是请求插件类功能且不是转发请求则进行转发
        if (featureInfo.getType() == 1 && !request.getRequestURI().contains("/louise/invoke/")) {
            PluginInfo pluginInfo = pluginInfoService.findByCmd(command);
            request.getRequestDispatcher("invoke/" + pluginInfo.getPlugin_id()).forward(request, response);
            return false;
        }

        // 放行不需要鉴权的命令
        if (featureInfo.getIs_auth() == 0) {
            return true;
        }

        int isAvailable = userService.isUserAvaliable(user_id);

        // 判断用户是否存在并启用
        if (isAvailable == 0) {
            logger.info("未登记的用户" + user_id);
            throw new ReplyException(LouiseConfig.LOUISE_ERROR_UNKNOWN_USER);
        } else if (isAvailable == -1) {
            logger.info("未启用的用户" + user_id);
            throw new ReplyException(LouiseConfig.LOUISE_ERROR_BANNED_USER);
        }

        //判断功能是否启用
        if (featureInfo.getIs_enabled() != 1) {
            throw new ReplyException("功能<" + featureInfo.getFeature_name() + ">未启用");
        }


        // 判断群聊还是私聊
        if (inMessage.getGroup_id() != -1) {
            if (groupService.isGroupExist(group_id)) {
                if (!groupService.isGroupEnabled(group_id)) {
                    logger.info("未启用的群组: " + group_id);
                    throw new ReplyException("主人不准露易丝在这个群里说话哦");
                }
            } else {
                logger.info("未注册的群组: " + group_id);
                throw new ReplyException("群聊还没有在 Louise 中注册哦");
            }

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
                throw new ReplyException("这个群聊的权限不准用这个功能哦");
        }

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
            throw new ReplyException("你的权限还不准用这个功能哦");

        //合法性校验通过 扣除CREDIT
        int credit = userService.minusCredit(user_id, featureInfo.getCredit_cost());
        if (credit < 0) {
            throw new ReplyException("你的CREDIT余额不足哦");
        }

        // 更新调用统计数据
        featureInfoService.addCount(featureInfo.getFeature_id(), group_id, user_id);
        logger.info("功能 " +featureInfo.getFeature_name() + " 消耗用户 " + user_id +" CREDIT " + featureInfo.getCredit_cost());

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }
}
