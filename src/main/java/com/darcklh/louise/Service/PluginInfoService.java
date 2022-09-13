package com.darcklh.louise.Service;

import com.darcklh.louise.Model.Saito.PluginInfo;
import org.springframework.stereotype.Service;

/**
 * @author DarckLH
 * @date 2022/9/14 1:28
 * @Description
 */
@Service
public interface PluginInfoService extends BaseService<PluginInfo>{
    public PluginInfo findByCmd(String cmd);
}
