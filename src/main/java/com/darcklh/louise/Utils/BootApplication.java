package com.darcklh.louise.Utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Controller.PluginInfoController;
import com.darcklh.louise.Controller.SaitoController;
import com.darcklh.louise.Mapper.PluginInfoDao;
import com.darcklh.louise.Mapper.SysConfigDao;
import com.darcklh.louise.Mapper.TaskDao;
import com.darcklh.louise.Model.InnerException;
import com.darcklh.louise.Model.Messages.Message;
import com.darcklh.louise.Model.Messages.OutMessage;
import com.darcklh.louise.Model.Saito.PluginInfo;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Model.Saito.SysConfig;
import com.darcklh.louise.Model.Saito.Task;
import com.darcklh.louise.Service.DynamicTaskService;
import com.darcklh.louise.Service.TaskService;
import com.darcklh.louise.Service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class BootApplication {

    @Autowired
    PluginManager pluginManager;

    @Autowired
    SysConfigDao sysConfigDao;

    @Autowired
    SaitoController saitoController;

    @Autowired
    TaskDao taskDao;

    @Autowired
    DynamicTaskService dynamicTaskService;

    @Autowired
    PluginInfoController pluginInfoController;

    public static Date bootDate;

    @PostConstruct
    public void run() {

        //获取系统启动时间
        bootDate = new Date();// 获取当前时间

        //从数据库中更新配置
        LouiseConfig.refreshConfig(sysConfigDao.selectList(null));

        log.info("<--加载 MyLouise 插件-->");
        pluginInfoController.loadPlugins();

        // 加载定时任务
        log.info("<--加载 MyLouise 定时任务-->");
        QueryWrapper<Task> wrapper = new QueryWrapper<>();
        wrapper.ne("is_enabled", 0);
        List<Task> tasks = taskDao.selectList(wrapper);
        if (tasks.size() > 0) {
            for (Task task: tasks) {
                dynamicTaskService.add(task);
                log.info("加载定时任务 <-- " + task.getTask_name() + "---" + task.getInfo() + " -->");
            }
        }
        Message msg = Message.build();
        msg.setUser_id(Long.parseLong(LouiseConfig.LOUISE_ADMIN_NUMBER));
        msg.text("启动时间 " + bootDate + " Louise 系统已启动")
                .send();
    }

}
