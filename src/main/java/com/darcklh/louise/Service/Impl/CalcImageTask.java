package com.darcklh.louise.Service.Impl;

import com.darcklh.louise.Model.Louise.ProcessImage;
import com.darcklh.louise.Service.MultiTaskService;
import com.darcklh.louise.Utils.EncryptUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
public class CalcImageTask implements MultiTaskService {

    private int status;
    // 声明一个任务的自有业务含义的变量，用于标识任务
    private int taskId;
    private String taskInfo;
    private File image;

    public static Map<String, ProcessImage> NewMap = Collections.synchronizedMap(new HashMap<String, ProcessImage>());

    public CalcImageTask(int taskId, String taskInfo, File image) {
        this.status = READY;
        this.taskId = taskId;
        this.taskInfo = taskInfo;
        this.image = image;
    }

    @Override
    public boolean execute() throws NoSuchAlgorithmException, IOException {
        setStatus(MultiTaskService.RUNNING);

        String md5 = EncryptUtils.checkSumMD5(image.getAbsolutePath());
        try {
            ProcessImage ig = new ProcessImage(image.getParent(), image.getName());
            NewMap.put(md5, ig);
        }
        catch(NullPointerException e) {
            this.status = ERROR;
            log.info("读取图片: " + image.getName() + " 失败了");
        }
        setStatus(FINISHED);
        this.image = null;
        return true;
    }
}
