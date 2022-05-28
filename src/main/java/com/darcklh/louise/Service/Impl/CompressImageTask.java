package com.darcklh.louise.Service.Impl;

import com.darcklh.louise.Model.Louise.ProcessImage;
import com.darcklh.louise.Service.MultiTaskService;
import com.darcklh.louise.Utils.EncryptUtils;
import com.darcklh.louise.Utils.ImageCompress;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DarckLH
 * @date 2022/4/22 19:20
 * @Description
 */
@Data
@Slf4j
public class CompressImageTask implements MultiTaskService {
    private int status;
    // 声明一个任务的自有业务含义的变量，用于标识任务
    private int taskId;
    private String taskInfo;
    private File image;

    public static int totalTask = 0;

    public CompressImageTask(int taskId, String taskInfo, File image) {
        this.status = READY;
        this.taskId = taskId;
        this.taskInfo = image.getPath();
        this.image = image;
    }

    @Override
    public boolean execute() throws IOException {
        setStatus(MultiTaskService.RUNNING);
        try {
            ImageCompress.resize(image.getParent(), image.getName());
        }
        catch(NullPointerException e) {
            this.status = ERROR;
            log.info("压缩图片: " + image.getName() + " 失败了");
        }
        totalTask++;
        setStatus(FINISHED);
        return true;
    }
}
