package com.darcklh.louise.Utils;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Mapper.PluginDao;
import com.darcklh.louise.Model.Plugin;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Service.PluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import javax.annotation.PostConstruct;
import java.net.ConnectException;
import java.util.List;

@Component
public class BootApplication {

    Logger logger = LoggerFactory.getLogger(BootApplication.class);


    public static List<PluginService> pluginServiceList;

    @Autowired
    PluginDao pluginDao;

    @Autowired
    LouiseConfig louiseConfig;

    @PostConstruct
    public void run() {
        logger.info("<--加载MyLouise插件-->");
        List<Plugin> plugins = pluginDao.selectList(null);
        if (plugins == null) {
            logger.info("MyLouise未安装插件");
            return;
        }
        int i = 0;
        try {
            PluginManager pluginManager = new PluginManager(plugins);
            for (Plugin plugin: plugins) {
                logger.info("加载插件 <--" + plugin.getName() + "---" + plugin.getAuthor() +"-- >");
                pluginServiceList.add(pluginManager.getInstance(plugin.getClass_name()));
                i++;
            }
        } catch (Exception e) {
            logger.info("加载插件失败: " + e.getMessage());
        }
        R r = new R();
        r.put("user_id", louiseConfig.getLOUISE_ADMIN_NUMBER());
        r.put("message", "露易丝启动了哦");
        try {
            r.sendMessage(r.getMessage());
        } catch (ResourceAccessException e) {
            logger.info("与BOT建立连接失败");
        }

        logger.info("插件加载完毕，共" + i + "个");

    }

}
