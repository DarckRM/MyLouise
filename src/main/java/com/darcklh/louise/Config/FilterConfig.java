package com.darcklh.louise.Config;

import com.darcklh.louise.Filter.LouiseFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author DarckLH
 * @date 2021/8/12 12:20
 * @Description
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean registFilter() {

        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new LouiseFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("CommandLegalityCheckFilter");
        registrationBean.setOrder(1);
        return registrationBean;

    }

}
