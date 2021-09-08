package com.darcklh.louise.Filter;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Api.MyLouiseApi;
import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Utils.HttpContextUtils;
import com.darcklh.louise.Utils.HttpServletWrapper;
import com.darcklh.louise.Utils.isEmpty;
import org.jsoup.select.Evaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LoggerGroup;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author DarckLH
 * @date 2021/8/12 12:16
 * @Description
 */
@Component
public class LouiseFilter implements Filter {

    Logger logger = LoggerFactory.getLogger(LouiseFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException { // do something 处理request 或response

        // doFilter()方法中的servletRequest参数的类型是ServletRequest，需要转换为HttpServletRequest类型方便调用某些方法
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        String ip = request.getRemoteHost();
        String url = request.getRequestURL().toString();
        String uri = request.getRequestURI();
        //TODO 端口号可能会导致字符串裁剪错误
        String urlCopy = uri.substring(1);
        //排除心跳检测以及静态资源
        if (urlCopy.contains("meta")) {
            return;
        }
        logger.info( ip + " >>> 进入过滤器 1 流程 >>> " + url);

        //获取请求Body
        //获取请求中的流，将取出来的字符串，再次转换成流，然后把它放入到新request对象中
        HttpServletWrapper wrapper = new HttpServletWrapper(request);
        String body = wrapper.getBody();

        //判断请求来源
        if (urlCopy.contains("saito")) {

            logger.info("请求 <Saito> ");
            filterChain.doFilter(servletRequest, servletResponse);

        } else {

            JSONObject jsonObject = JSONObject.parseObject(body);
            String post_type = "";

            if (!isEmpty.isEmpty(jsonObject)) {
                post_type = jsonObject.getString("post_type");
                if (!isEmpty.isEmpty(post_type)) {
                    logger.info("请求 <Louise> ");
                    //排除心跳检测
                    if (post_type.equals("meta_event")) {
                        logger.debug("心跳检测");
                        return;
                    } else if (post_type.equals("notice")) {
                        logger.debug("暂不处理notice消息");
                        return;
                    }
                    logger.info("过滤器 1 流程结束"); // 调用filter链中的下一个filter

                    // 在chain.doFiler方法中传递新的request对象
                    filterChain.doFilter(wrapper, servletResponse);
                }
            }

        }
    }

    @Override
    public void destroy() {

    }

}
