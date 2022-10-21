package com.darcklh.louise.Service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Model.Messages.OutMessage;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Model.Saito.Task;
import com.darcklh.louise.Model.Sender;
import com.darcklh.louise.Service.DynamicTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
        if (null != taskMap.get(task.getTask_name())) {
            stop(task.getTask_name());
        }

        // schedule :调度给定的Runnable ，在指定的执行时间调用它。
        //一旦调度程序关闭或返回的ScheduledFuture被取消，执行将结束。
        //参数：
        //任务 – 触发器触发时执行的 Runnable
        //startTime – 任务所需的执行时间（如果这是过去，则任务将立即执行，即尽快执行）
        ScheduledFuture<?> schedule = syncScheduler.schedule(getRunnable(task), new CronTrigger(task.getCron()));
        taskMap.put(task.getTask_name(), schedule);
        taskList.add(task.getTask_name());
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

            log.info("---执行动态任务 - " + task.getTask_name() +  "---");
            try {
                log.info("系统时间 - " + LocalDateTime.now());
                // 转化参数构造 OutMessage 对象
                OutMessage out = new OutMessage();

                if (task.getSender_type().equals("user")) {
                    out.setGroup_id((long)-1);
                    out.setUser_id(Long.parseLong(task.getNumber()));
                }
                else
                    out.setGroup_id(Long.parseLong(task.getNumber()));

                Sender sender = new Sender();
                sender.setUser_id(Long.parseLong(task.getNumber()));

                out.setSender(sender);
                out.setMessage(task.getParameter());
                // 判断任务类型
                switch (task.getType()) {
                    // 0 系统任务
                    case 0: break;
                    // 1 发送消息
                    case 1: {
                        out.setPost_type("message");
                        r.sendMessage(out);
                    }; break;
                    // 2 执行功能
                    case 2: {
                        out.setPost_type("message");
                        // 构造请求体
                        RestTemplate rest = new RestTemplate();
                        HttpEntity request = new HttpEntity<>(out, null);
                        JSONObject result = rest.postForObject("http://localhost:8099/louise/" + task.getTarget(), request, JSONObject.class);
                    }
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
