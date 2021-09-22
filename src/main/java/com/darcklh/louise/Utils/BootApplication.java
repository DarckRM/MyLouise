package com.darcklh.louise.Utils;

import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Controller.SaitoController;
import com.darcklh.louise.Mapper.PluginInfoDao;
import com.darcklh.louise.Model.Saito.PluginInfo;
import com.darcklh.louise.Model.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class BootApplication {

    Logger logger = LoggerFactory.getLogger(BootApplication.class);
    @Autowired
    PluginManager pluginManager;

    @Autowired
    PluginInfoDao pluginInfoDao;

    @Autowired
    LouiseConfig louiseConfig;

    @Autowired
    SaitoController saitoController;

    @PostConstruct
    public void run() {
        logger.info("<--加载MyLouise插件-->");
        List<PluginInfo> pluginInfos = pluginInfoDao.selectList(null);
        if (pluginInfos == null) {
            logger.info("MyLouise未安装插件");
            return;
        }
        int i = 0;
        try {
            pluginManager.loadPlugins(pluginInfos);
            for (PluginInfo pluginInfo : pluginInfos) {
                logger.info("加载插件 <--" + pluginInfo.getName() + "---" + pluginInfo.getAuthor() +"-- >");
                i++;
            }
            saitoController.PluginInit(i);
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