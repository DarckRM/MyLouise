package com.darcklh.louise;

import com.darcklh.louise.Mapper.PluginDao;
import com.darcklh.louise.Model.Plugin;
import com.darcklh.louise.Service.PluginService;
import com.darcklh.louise.Utils.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BootApplication implements ApplicationRunner {

    Logger logger = LoggerFactory.getLogger(BootApplication.class);


    public static List<PluginService> pluginServiceList;

    @Autowired
    public void setPluginServiceList(List<PluginService> pluginServiceList) {
        BootApplication.pluginServiceList = pluginServiceList;
    }

    @Autowired
    public List<PluginService> getPluginServiceList() {
        return BootApplication.pluginServiceList;
    }

    @Autowired
    PluginDao pluginDao;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.info("<--加载MyLouise插件-->");
        List<Plugin> plugins = pluginDao.selectList(null);
        if (plugins == null) {
            logger.info("MyLouise未安装插件");
            return;
        }
        int i = 0;
        PluginManager pluginManager = new PluginManager(plugins);
        List<PluginService> ps;
        for (Plugin plugin: plugins) {
            logger.info("加载插件 <--" + plugin.getName() + "---" + plugin.getAuthor() +"-- >");
            pluginServiceList.add(pluginManager.getInstance(plugin.getClass_name()));
            i++;
        }

        logger.info("插件加载完毕，共" + i + "个");
    }

}
