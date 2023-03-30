package com.darcklh.louise.Service;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Model.Messages.InMessage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
public interface PluginService {

    String pluginName();

    public JSONObject service(InMessage inMessage);
    public JSONObject service();

    /**
     * 初始化函数
     * @return 返回是否成功初始化
     */
    public boolean init();

}
