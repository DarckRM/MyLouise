package com.darcklh.louise.Filter;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Utils.HttpServletWrapper;
import com.darcklh.louise.Utils.isEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author DarckLH
 * @date 2021/8/12 12:16
 * @Description
 */
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

        JSONObject jsonObject = JSONObject.parseObject(body);
        String post_type = "";

        if (!isEmpty.isEmpty(jsonObject)) {
            post_type = jsonObject.getString("post_type");
            if (!isEmpty.isEmpty(post_type)) {
                logger.info("请求 <Louise> ");
                switch (post_type) {
                    case "meta_event": {logger.debug("心跳检测"); return;}
                    case "notice": {logger.info("暂不处理notice消息"); return;}
                }
                logger.info("过滤器 1 流程结束"); // 调用filter链中的下一个filter
                // 在chain.doFiler方法中传递新的request对象
                filterChain.doFilter(wrapper, servletResponse);
            }
        }


    }

    @Override
    public void destroy() {

    }

}
