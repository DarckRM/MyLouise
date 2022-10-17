package com.darcklh.louise.Service.Impl;

import com.darcklh.louise.Model.Messages.OutMessage;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Model.Saito.Task;
import com.darcklh.louise.Service.DynamicTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

/**
 * @author DarckLH
 * @date 2022/10/17 15:25
 * @Description 定时任务具体实现
 */
@Slf4j
@Service
public class DynamicTaskImpl implements DynamicTaskService {
    /**
     * 以下两个都是线程安全的集合类。
     */
    public Map<String, ScheduledFuture<?>> taskMap = new ConcurrentHashMap<>();
    public List<String> taskList = new CopyOnWriteArrayList<String>();

    @Autowired
    R r;

    private final ThreadPoolTaskScheduler syncScheduler;

    public DynamicTaskImpl(ThreadPoolTaskScheduler syncScheduler) {
        this.syncScheduler = syncScheduler;
    }

    /**
     * 查看已开启但还未执行的动态任务
     * @return
     */
    public List<String> getTaskList() {
        return taskList;
    }


    /**
     * 添加一个动态任务
     *
     * @param task
     * @return
     */
    public boolean add(Task task) {
        // 此处的逻辑是 ，如果当前已经有这个名字的任务存在，先删除之前的，再添加现在的。（即重复就覆盖）
        if (null != taskMap.get(task.getSchedule_name())) {
            stop(task.getSchedule_name());
        }

        Date startTime = new Date();
        // schedule :调度给定的Runnable ，在指定的执行时间调用它。
        //一旦调度程序关闭或返回的ScheduledFuture被取消，执行将结束。
        //参数：
        //任务 – 触发器触发时执行的 Runnable
        //startTime – 任务所需的执行时间（如果这是过去，则任务将立即执行，即尽快执行）
        ScheduledFuture<?> schedule = syncScheduler.schedule(getRunnable(task), new CronTrigger("*/5 * * * * ?"));
        taskMap.put(task.getSchedule_name(), schedule);
        taskList.add(task.getSchedule_name());
        return true;
    }


    /**
     * 运行任务
     *
     * @param task
     * @return
     */
    public Runnable getRunnable(Task task) {
        return () -> {
            log.info("---动态定时任务运行---");
            try {
                log.info("此时时间==>" + LocalDateTime.now());
                // 判断任务类型
                switch (task.getType()) {
                    // 0 系统任务
                    case 0: break;
                    // 1 发送消息
                    case 1: {
                        OutMessage outMessage = new OutMessage();
                        if (task.getSender_type().equals("user"))
                            outMessage.setUser_id(Long.parseLong(task.getNumber()));
                        else
                            outMessage.setGroup_id(Long.parseLong(task.getNumber()));
                        outMessage.setMessage(task.getParameter());
                        r.sendMessage(outMessage);
                    }
                }
                log.info("task中设定的时间==>" + task);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("---end--------");
        };
    }

    /**
     * 停止任务
     *
     * @param name
     * @return
     */
    public boolean stop(String name) {
        if (null == taskMap.get(name)) {
            return false;
        }
        ScheduledFuture<?> scheduledFuture = taskMap.get(name);
        scheduledFuture.cancel(true);
        taskMap.remove(name);
        taskList.remove(name);
        return true;
    }
}
