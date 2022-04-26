package com.darcklh.louise.Controller;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Model.LoggerQueue;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Service.WebSocketService;
import com.darcklh.louise.Utils.BootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author DarckLH
 * @date 2021/9/24 21:49
 * @Description
 */
@RestController
@EnableScheduling
@RequestMapping("saito/saito_ws")
public class WebSocketController {

    /**
     * 心跳检测 检查各系统运行状况
     */
    @GetMapping("/system_check")
    @Scheduled(cron = "*/3 * * * * *")
    public void pushOne() {

        JSONObject jsonObject = new JSONObject();
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间

        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记
        jsonObject.put("bootTime", sdf.format(BootApplication.bootDate));
        WebSocketService.sendMessage("status_conn",jsonObject.toString());

    }

}
