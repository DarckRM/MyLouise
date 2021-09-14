package com.darcklh.louise.Utils;

import com.darcklh.louise.Model.Plugin;
import com.darcklh.louise.Service.PluginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * 插件管理类
 */
public class PluginManager {

    Logger logger = LoggerFactory.getLogger(PluginManager.class);

    private URLClassLoader urlClassLoader;

    public PluginManager(List<Plugin> plugins) throws MalformedURLException {
        init(plugins);
    }

    private void init(List<Plugin> plugins) throws MalformedURLException {
        int size = plugins.size();
        URL[] urls = new URL[size];

        for (int i = 0; i < size; i++) {
            Plugin plugin = plugins.get(i);
            String filePath = plugin.getPath();
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
