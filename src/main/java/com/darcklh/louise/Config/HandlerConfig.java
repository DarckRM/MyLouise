package com.darcklh.louise.Config;

import com.darcklh.louise.Filter.Handler.LouiseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author DarckLH
 * @date 2021/8/12 12:43
 * @Description
 */
@Configuration
public class HandlerConfig implements WebMvcConfigurer {

    @Autowired
    LouiseHandler louiseHandler;;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(louiseHandler).addPathPatterns("/**").excludePathPatterns("/saito/**", "/error", "/static/**", "/favicon.ico");
    }
}
