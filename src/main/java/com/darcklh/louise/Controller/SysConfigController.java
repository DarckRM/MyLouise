package com.darcklh.louise.Controller;

import com.darcklh.louise.Model.Result;
import com.darcklh.louise.Model.Saito.SysConfig;
import com.darcklh.louise.Service.SysConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author DarckLH
 * @date 2021/9/20 22:36
 * @Description
 */
@RestController
@RequestMapping("saito/sys-config")
public class SysConfigController {

    Logger logger = LoggerFactory.getLogger(SysConfigController.class);

    @Autowired
    SysConfigService sysConfigService;

    @RequestMapping("/{type}")
    public Result<SysConfig> findSysConfigByType(@PathVariable String type) {

        Result<SysConfig> result = new Result<>();
        result.setDatas(sysConfigService.findSysConfigByType(type));

        return result;
    }

}
