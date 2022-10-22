package com.darcklh.louise.Controller;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Model.Result;
import com.darcklh.louise.Model.Saito.PluginInfo;
import com.darcklh.louise.Service.PluginInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Plugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author DarckLH
 * @date 2022/10/22 18:27
 * @Description
 */
@RestController
@Slf4j
@RequestMapping("saito/plugin-info")
public class PluginInfoController {

    @Autowired
    PluginInfoService pluginInfoService;

    @PostMapping("/loadPlugins")
    public Result<PluginInfo> loadPlugins() {
        Result<PluginInfo> result = new Result<>();
        List<PluginInfo> pluginInfoList = pluginInfoService.loadPlugins();
        if (pluginInfoList.size() != 0) {
            result.setMsg("成功加载了 " + pluginInfoList.size() + " 个插件");
            result.setDatas(pluginInfoList);
        } else {
            result.setMsg("未安装任何插件");
        }
        return result;
    }

    // 手动卸载所有插件
    @PostMapping("/unloadPlugins")
    public Result<String> unloadPlugins() {
        Result<String> result = new Result<>();
        result.setMsg(pluginInfoService.unloadPlugins());
        return result;
    }

}
