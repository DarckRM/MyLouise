package com.darcklh.louise.Config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 配置信息类 通过自动注入实现配置刷新
 */
@Component
@PropertySource(value = "classpath:application.yml")
@Data
public class LouiseConfig {

    @Value("${server.port}")
    private String SERVER_PORT;

    //Louise相关配置
    @Value("${LOUISE.HELP_PAGE}")
    private String LOUISE_HELP_PAGE;
    @Value("${LOUISE.ADMIN_NUMBER}")
    private String LOUISE_ADMIN_NUMBER;
    @Value("${LOUISE.CACHE.IMAGE_LOCATION}")
    private String LOUISE_CACHE_IMAGE_LOCATION;
    @Value("${LOUISE.ERROR.UNKNOWN_COMMAND}")
    private String LOUISE_ERROR_UNKNOWN_COMMAND;
    @Value("${LOUISE.ERROR.THIRD_API_REQUEST_FAILED}")
    private String LOUISE_ERROR_THIRD_API_REQUEST_FAILED;
    @Value("${LOUISE.ERROR.BANNED_USER}")
    private String LOUISE_ERROR_BANNED_USER;
    @Value("${LOUISE.ERROR.UNKNOWN_USER}")
    private String LOUISE_ERROR_UNKNOWN_USER;
    @Value("${LOUISE.ERROR.UPLOAD_IMAGE_FAILED}")
    private String LOUISE_ERROR_UPLOAD_IMAGE_FAILED;

    //CQ-HTTP相关配置
    @Value("${BOT.LOUISE_CACHE_IMAGE}")
    private String BOT_LOUISE_CACHE_IMAGE;
    @Value("${BOT.BASE_URL}")
    private String BOT_BASE_URL;
    @Value("${BOT.HTTP_POST_KEY}")
    private String BOT_HTTP_POST_KEY;

    //API相关配置
    @Value("${API.PIXIV.REVERSE_PROXY}")
    private String PIXIV_REVERSE_PROXY;
    @Value("${API.PIXIV.PROXY_URL}")
    private String PIXIV_PROXY_URL;
    @Value("${API.SOURCENAO.URL}")
    private String SOURCENAO_URL;
    @Value("${API.SOURCENAO.API_KEY}")
    private String SOURCENAO_API_KEY;
    @Value("${API.SOURCENAO.ERROR.REQUEST_FAILED}")
    private String SOURCENAO_ERROR_REQUEST_FAILED;

}
