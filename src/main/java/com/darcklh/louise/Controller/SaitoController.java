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
    PluginManager pluginManager;

    @RequestMapping("login")
    public Result<SysUser> Login(@RequestBody SysUser sysUser) {

        Result<SysUser> result = sysUserService.Login(sysUser);

        return result;
    }

    @RequestMapping("test")
    public void test() {
        try {
            PluginService pluginService = pluginManager.getInstance(pluginManager.pluginInfos.get(1).getClass_name());
            pluginService.service();
        } catch (Exception e) {

        }
    }

}
