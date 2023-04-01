package com.darcklh.louise.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.darcklh.louise.Mapper.TaskDao;
import com.darcklh.louise.Model.Saito.Task;
import com.darcklh.louise.Service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author DarckLH
 * @date 2022/10/21 9:14
 * @Description
 */
@Service
public class TaskImpl implements TaskService {

    @Autowired
    private TaskDao taskDao;

    @Override
    public List<Task> findBy() {
        QueryWrapper<Task> wrapper = new QueryWrapper<>();
        return taskDao.selectList(wrapper);
    }

    @Override
    public String delBy(Integer id) {
        return null;
    }

    @Override
    public String editBy(Task object) {
        return null;
    }

    @Override
    public String add(Task object) {
        return null;
    }
}
