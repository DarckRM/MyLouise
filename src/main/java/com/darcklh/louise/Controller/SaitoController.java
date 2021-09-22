package com.darcklh.louise.Controller;

import com.darcklh.louise.Model.Result;
import com.darcklh.louise.Model.Saito.SysUser;
import com.darcklh.louise.Service.PluginService;
import com.darcklh.louise.Service.SysUserService;
import com.darcklh.louise.Utils.PluginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author DarckLH
 * @date 2021/9/8 18:42
 * @Description 处理后台请求
 */
@RestController
@RequestMapping("saito/")
public class SaitoController {

    @Autowired
    SysUserService sysUserService;

    @Autowired
    List<PluginService> pluginServices;

    @Autowired
    PluginManager pluginManager;

    @RequestMapping("login")
    public Result<SysUser> Login(@RequestBody SysUser sysUser) {

        Result<SysUser> result = sysUserService.Login(sysUser);

        return result;
    }

    @RequestMapping("plugin-init")
    public void PluginInit(Integer number) {
        try {
            for (int i = 0; i < number; i++) {
                pluginServices.add(pluginManager.getInstance(pluginManager.pluginInfos.get(i).getClass_name()));
                pluginServices.get(i).service();
            }
        } catch (Exception e) {

        }
    }

}
