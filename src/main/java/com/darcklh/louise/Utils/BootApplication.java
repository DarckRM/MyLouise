package com.darcklh.louise.Utils;

import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Controller.SaitoController;
import com.darcklh.louise.Mapper.PluginInfoDao;
import com.darcklh.louise.Mapper.SysConfigDao;
import com.darcklh.louise.Model.Messages.OutMessage;
import com.darcklh.louise.Model.Saito.PluginInfo;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Model.Saito.SysConfig;
import com.darcklh.louise.Service.WebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class BootApplication {

    Logger logger = LoggerFactory.getLogger(BootApplication.class);
    @Autowired
    PluginManager pluginManager;

    @Autowired
    PluginInfoDao pluginInfoDao;

    @Autowired
    SysConfigDao sysConfigDao;

    @Autowired
    SaitoController saitoController;

    @Autowired
    R r = new R();

    public static Date bootDate;

    @PostConstruct
    public void run() {

        //获取系统启动时间
        bootDate = new Date();// 获取当前时间

        //从数据库中更新配置
        LouiseConfig.refreshConfig(sysConfigDao.selectList(null));

//        logger.info("<--加载MyLouise插件-->");
//        List<PluginInfo> pluginInfos = pluginInfoDao.selectList(null);
//        if (pluginInfos == null) {
//            logger.info("MyLouise未安装插件");
//            return;
//        }
//        int i = 0;
//        try {
//            pluginManager.loadPlugins(pluginInfos);
//            for (PluginInfo pluginInfo : pluginInfos) {
//                logger.info("加载插件 <--" + pluginInfo.getName() + "---" + pluginInfo.getAuthor() +"-- >");
//                i++;
//            }
//            saitoController.PluginInit(i);
//        } catch (Exception e) {
//            logger.info("加载插件失败: " + e.getMessage());
//        }
        OutMessage outMessage = new OutMessage();
        outMessage.setUser_id(Long.valueOf(LouiseConfig.LOUISE_ADMIN_NUMBER));
        outMessage.setMessage(LouiseConfig.LOUISE_WELCOME_SENTENCE);
        r.sendMessage(outMessage);
//        logger.info("插件加载完毕，共" + i + "个");

    }

}
