package com.darcklh.louise.Service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Model.Messages.InMessage;
import com.darcklh.louise.Service.PluginService;
import org.springframework.stereotype.Service;

@Service
public class PluginImpl implements PluginService {

    @Override
    public String pluginName() {
        return null;
    }

    @Override
    public JSONObject service(InMessage inMessage) {
        return null;
    }

    @Override
    public JSONObject service() {
        return null;
    }
}
