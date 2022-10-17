package com.darcklh.louise.Controller;

import com.darcklh.louise.Model.Saito.Task;
import com.darcklh.louise.Service.DynamicTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author DarckLH
 * @date 2022/10/17 15:24
 * @Description 控制定时任务
 */

@RestController
@RequestMapping("/saito/dynamicTask")
public class DynamicTaskController {

    @Autowired
    DynamicTaskService dynamicTaskService;

    /**
     * 开启一个动态任务
     * @param task
     * @return
     */
    @RequestMapping("/dynamic")
    public String startDynamicTask(@RequestBody Task task){
        // 将这个添加到动态定时任务中去
        dynamicTaskService.add(task);
        return "动态任务:"+task.getSchedule_name()+" 已开启";
    }

    /**
     *  根据名称 停止一个动态任务
     * @param name
     * @return
     */
    @PostMapping("/{name}")
    public String stopDynamicTask(@PathVariable("name") String name){
        // 将这个添加到动态定时任务中去
        if(!dynamicTaskService.stop(name)){
            return "停止失败,任务已在进行中.";
        }
        return "任务已停止";
    }
}
