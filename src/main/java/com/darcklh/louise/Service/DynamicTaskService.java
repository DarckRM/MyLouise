package com.darcklh.louise.Service;

import com.darcklh.louise.Model.Saito.Task;
import org.springframework.stereotype.Service;

/**
 * @author DarckLH
 * @date 2022/10/17 15:40
 * @Description
 */

@Service
public interface DynamicTaskService {
    public boolean add(Task task);
    public Runnable getRunnable(Task task);
    public boolean stop(String name);
}
