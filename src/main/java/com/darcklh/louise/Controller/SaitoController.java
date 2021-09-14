package com.darcklh.louise.Controller;

import com.darcklh.louise.BootApplication;
import com.darcklh.louise.Model.Plugin;
import com.darcklh.louise.Model.Result;
import com.darcklh.louise.Model.SysUser;
import com.darcklh.louise.Service.PluginService;
import com.darcklh.louise.Service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

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


    List<PluginService> pluginServiceList;

    @RequestMapping("loginPage")
    public ModelAndView login(){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("login");
        return mv;
    }

    @RequestMapping("login")
    public Result<SysUser> Login(@RequestBody SysUser sysUser) {

        Result<SysUser> result = sysUserService.Login(sysUser);

        return result;
    }

    @RequestMapping("test")
    public void test() {

        BootApplication.pluginServiceList.get(2).service();

    }

}
