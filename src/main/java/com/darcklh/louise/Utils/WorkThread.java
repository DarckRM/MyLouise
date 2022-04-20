package com.darcklh.louise.Utils;

import com.darcklh.louise.Model.SpecificException;
import com.darcklh.louise.Service.MultiTaskService;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class WorkThread extends Thread {

    // 本线程待执行的任务列表，你也可以指为任务索引的起始值
    private List<MultiTaskService> taskList = null;
    private int threadId;
    private int restTask;

    /**
     * 构造工作线程，为其指派任务列表，及命名线程 ID
     *
     * @param taskList
     *            欲执行的任务列表
     * @param threadId
     *            线程 ID
     */
    public WorkThread(List taskList, int threadId) {
        this.taskList = taskList;
        this.threadId = threadId;
        this.restTask = taskList.size();
    }

    /**
     * 执行被指派的所有任务
     */
    public void run() {
        try {
            for (MultiTaskService taskService : taskList)
                if (taskService.execute())
                    callBackFunc();
        } catch (IOException e) {
            throw new SpecificException("文件读写错误");
        } catch ( NoSuchAlgorithmException e) {
            throw new SpecificException("不懂这是什么错误");
        }
    }

    public void callBackFunc() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        restTask--;
        if (restTask == 0) {
            stopWatch.stop();
            System.out.println("任务 " + threadId + " 已完成, 执行时间: " + stopWatch.getLastTaskTimeMillis());
        }

    }
}
