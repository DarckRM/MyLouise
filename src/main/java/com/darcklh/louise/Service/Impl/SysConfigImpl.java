package com.darcklh.louise.Service.Impl;

import com.darcklh.louise.Mapper.SysConfigDao;
import com.darcklh.louise.Model.Saito.SysConfig;
import com.darcklh.louise.Service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author DarckLH
 * @date 2021/9/20 23:02
 * @Description
 */
@Service
public class SysConfigImpl implements SysConfigService {

    @Autowired
    SysConfigDao sysConfigDao;

    @Override
    public List<SysConfig> findSysConfigByType(String type) {

        int x = 0;
        switch (type) {
            case "louise": x = 0; break;
            case "saito": x = 1; break;
        }
        List<SysConfig> sysConfigs = sysConfigDao.findSysConfigByType(x);
        return sysConfigs;
    }
}
