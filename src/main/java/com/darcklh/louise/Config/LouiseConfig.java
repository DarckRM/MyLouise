package com.darcklh.louise.Config;

import com.darcklh.louise.Model.Saito.SysConfig;
import com.darcklh.louise.Utils.YamlReader;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置信息类 通过自动注入实现配置刷新
 */
@Component
@PropertySource(value = "classpath:application.yml")
@Data
@Slf4j
public class LouiseConfig {

    public static String SERVER_PORT;

    //Louise相关配置
    public static String LOUISE_PROXY;
    public static int LOUISE_PROXY_PORT;
    public static String LOUISE_WELCOME_SENTENCE;
    public static String LOUISE_HELP_PAGE;
    public static String LOUISE_ADMIN_NUMBER;
    public static String LOUISE_CACHE_IMAGE_LOCATION;
    public static String LOUISE_CACHE_LOCATION;
    public static String LOUISE_ERROR_UNKNOWN_COMMAND;
    public static String LOUISE_ERROR_THIRD_API_REQUEST_FAILED;
    public static String LOUISE_ERROR_BANNED_USER;
    public static String LOUISE_ERROR_UNKNOWN_USER;
    public static String LOUISE_ERROR_UPLOAD_IMAGE_FAILED;

    //CQ-HTTP相关配置
    public static String BOT_LOUISE_CACHE_IMAGE;
    public static String BOT_BASE_URL;
    public static String BOT_HTTP_POST_KEY;

    //API相关配置
    public static String PIXIV_REVERSE_PROXY;
    public static String PIXIV_PROXY_URL;
    public static String SOURCENAO_URL;
    public static String SOURCENAO_API_KEY;
    public static String SOURCENAO_ERROR_REQUEST_FAILED;

    /**
     * 初始化参数
     */
    public LouiseConfig() {
        // SERVER_PORT = YamlReader.instance.getValueByKey("sever.port").toString();

        LOUISE_PROXY = YamlReader.instance.getValueByKey("LOUISE.HTTP_PROXY.URL").toString();
        LOUISE_PROXY_PORT = Integer.parseInt(YamlReader.instance.getValueByKey("LOUISE.HTTP_PROXY.PORT").toString());
        LOUISE_WELCOME_SENTENCE = YamlReader.instance.getValueByKey("LOUISE.WELCOME_SENTENCE").toString();
        LOUISE_HELP_PAGE = YamlReader.instance.getValueByKey("LOUISE.HELP_PAGE").toString();
        LOUISE_ADMIN_NUMBER = YamlReader.instance.getValueByKey("LOUISE.ADMIN_NUMBER").toString();
        LOUISE_CACHE_IMAGE_LOCATION = YamlReader.instance.getValueByKey("LOUISE.CACHE.IMAGE_LOCATION").toString();
        LOUISE_CACHE_LOCATION = YamlReader.instance.getValueByKey("LOUISE.CACHE.LOCATION").toString();
        LOUISE_ERROR_UNKNOWN_COMMAND = YamlReader.instance.getValueByKey("LOUISE.ERROR.UNKNOWN_COMMAND").toString();
        LOUISE_ERROR_THIRD_API_REQUEST_FAILED = YamlReader.instance.getValueByKey("LOUISE.ERROR.THIRD_API_REQUEST_FAILED").toString();
        LOUISE_ERROR_BANNED_USER = YamlReader.instance.getValueByKey("LOUISE.ERROR.BANNED_USER").toString();
        LOUISE_ERROR_UNKNOWN_USER = YamlReader.instance.getValueByKey("LOUISE.ERROR.UNKNOWN_USER").toString();
        LOUISE_ERROR_UPLOAD_IMAGE_FAILED = YamlReader.instance.getValueByKey("LOUISE.ERROR.UPLOAD_IMAGE_FAILED").toString();

        BOT_LOUISE_CACHE_IMAGE = YamlReader.instance.getValueByKey("BOT.LOUISE_CACHE_IMAGE").toString();
        BOT_BASE_URL = YamlReader.instance.getValueByKey("BOT.BASE_URL").toString();
        BOT_HTTP_POST_KEY = YamlReader.instance.getValueByKey("BOT.HTTP_POST_KEY").toString();

        PIXIV_REVERSE_PROXY = YamlReader.instance.getValueByKey("API.PIXIV.REVERSE_PROXY").toString();
        PIXIV_PROXY_URL = YamlReader.instance.getValueByKey("API.PIXIV.PROXY_URL").toString();
        SOURCENAO_URL = YamlReader.instance.getValueByKey("API.SOURCENAO.URL").toString();
        SOURCENAO_API_KEY = YamlReader.instance.getValueByKey("API.SOURCENAO.API_KEY").toString();
        SOURCENAO_ERROR_REQUEST_FAILED = YamlReader.instance.getValueByKey("API.SOURCENAO.ERROR.REQUEST_FAILED").toString();
    }

    /**
     * 从数据库中更新配置
     * @param sysConfigs
     */
    public static void refreshConfig(List<SysConfig> sysConfigs) {

        //获取系统配置项
        HashMap<String, String> configs = new HashMap<>();

        for ( SysConfig sysConfig: sysConfigs ) {
            configs.put(sysConfig.getConfig_name(), sysConfig.getConfig_value());
        }

        //从数据库中取得配置(默认读取一次配置文件)

        LOUISE_PROXY = configs.get("LOUISE.HTTP_PROXY.URL");
        LOUISE_PROXY_PORT = Integer.parseInt(configs.get("LOUISE.HTTP_PROXY.PORT"));
        LOUISE_WELCOME_SENTENCE = configs.get("LOUISE.WELCOME_SENTENCE");
        LOUISE_HELP_PAGE = configs.get("LOUISE.HELP_PAGE");
        LOUISE_ADMIN_NUMBER = configs.get("LOUISE.ADMIN_NUMBER");
        LOUISE_CACHE_LOCATION = configs.get("LOUISE.CACHE.LOCATION");
        LOUISE_CACHE_IMAGE_LOCATION = configs.get("LOUISE.CACHE.IMAGE_LOCATION");

        BOT_LOUISE_CACHE_IMAGE = configs.get("BOT.LOUISE_CACHE_IMAGE");
        BOT_BASE_URL = configs.get("BOT.BASE_URL");
        BOT_HTTP_POST_KEY = configs.get("BOT.HTTP_POST_KEY");

        SOURCENAO_URL = configs.get("API.SOURCENAO.URL");
        SOURCENAO_API_KEY = configs.get("API.SOURCENAO.API_KEY");
        PIXIV_PROXY_URL = configs.get("API.PIXIV.PROXY_URL");
    }


}
