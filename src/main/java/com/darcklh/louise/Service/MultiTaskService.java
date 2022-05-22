package com.darcklh.louise.Service;

import com.darcklh.louise.Model.Louise.ProcessImage;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 多线程任务接口
 */
public interface MultiTaskService {

    public static final int READY = 0;
    public static final int RUNNING = 1;
    public static final int FINISHED = 2;
    public static final int ERROR = 9;

    /**
     * 执行任务
     */
    public boolean execute() throws NoSuchAlgorithmException, IOException;

    public int getTaskId();
    public int getStatus();
    public void setTaskId(int taskId);
    public void setStatus(int taskId);

}
