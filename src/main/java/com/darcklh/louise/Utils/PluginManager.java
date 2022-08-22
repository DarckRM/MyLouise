package com.darcklh.louise.Utils;

import com.darcklh.louise.Model.Saito.PluginInfo;
import com.darcklh.louise.Service.PluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * 插件管理类
 */
@Component
public class PluginManager {

    Logger logger = LoggerFactory.getLogger(PluginManager.class);

    public static List<PluginInfo> pluginInfos;

    private URLClassLoader urlClassLoader;

    public void loadPlugins(List<PluginInfo> pluginInfos) throws MalformedURLException, IllegalAccessException, InstantiationException {
        init(pluginInfos);
        for(PluginInfo pluginInfo: pluginInfos) {
            pluginInfo.setPluginServices(getInstance(pluginInfo.getClass_name()));
        }
        PluginManager.pluginInfos = pluginInfos;
    }

    private void init(List<PluginInfo> pluginInfos) throws MalformedURLException {
        int size = pluginInfos.size();
        URL[] urls = new URL[size];

        for (int i = 0; i < size; i++) {
            PluginInfo pluginInfo = pluginInfos.get(i);
            String filePath = pluginInfo.getPath();
            urls[i] = new URL("jar:file:" +filePath+ "!/");
        }

        //将jar文件组成数组 创建URLClassLoader
        urlClassLoader = new URLClassLoader(urls, getClass().getClassLoader());
    }

    public PluginService getInstance(String className) throws InstantiationException,IllegalAccessException {
        try {
            Class<?> clazz = urlClassLoader.loadClass(className);
            Object instance = clazz.newInstance();
            return (PluginService) instance;
        } catch (ClassNotFoundException e) {
            logger.info("插件"+className+"未找到，加载失败");
            return null;
        }

    }
}
