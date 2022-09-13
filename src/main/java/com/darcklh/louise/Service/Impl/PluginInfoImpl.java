package com.darcklh.louise.Service.Impl;

import com.darcklh.louise.Mapper.PluginInfoDao;
import com.darcklh.louise.Model.Saito.PluginInfo;
import com.darcklh.louise.Service.PluginInfoService;
import com.darcklh.louise.Service.PluginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author DarckLH
 * @date 2022/9/14 1:29
 * @Description
 */
@Service
public class PluginInfoImpl implements PluginInfoService {

    @Autowired
    PluginInfoDao pluginInfoDao;

    @Override
    public List<PluginInfo> findBy() {
        return null;
    }

    @Override
    public String delBy(Integer id) {
        return null;
    }

    @Override
    public String editBy(PluginInfo object) {
        return null;
    }

    @Override
    public String add(PluginInfo object) {
        return null;
    }

    @Override
    public PluginInfo findByCmd(String cmd) {
        return pluginInfoDao.findByCmd(cmd);
    }
}
