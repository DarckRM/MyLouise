package com.darcklh.louise.Utils;

import com.darcklh.louise.Model.Saito.PluginInfo;
import com.darcklh.louise.Service.PluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;

/**
 * 插件管理类
 */
@Component
public class PluginManager {

    Logger logger = LoggerFactory.getLogger(PluginManager.class);

    public static HashMap<Integer, PluginInfo> pluginInfos = new HashMap<>();

    private URLClassLoader urlClassLoader;

    public void loadPlugins(List<PluginInfo> pluginList) throws IOException, IllegalAccessException, InstantiationException {
        init(pluginList);
        for(PluginInfo pluginInfo: pluginList) {
            pluginInfo.setPluginService(getInstance(pluginInfo.getClass_name()));
            pluginInfos.put(pluginInfo.getPlugin_id(), pluginInfo);
        }
        // urlClassLoader.close();
    }

    private void init(List<PluginInfo> pluginList) throws MalformedURLException {
        int size = pluginList.size();
        URL[] urls = new URL[size];

        for (int i = 0; i < size; i++) {
            PluginInfo pluginInfo = pluginList.get(i);
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
