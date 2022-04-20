package com.darcklh.louise.Service.Impl;

import com.darcklh.louise.Model.Louise.Image;
import com.darcklh.louise.Service.MultiTaskService;
import com.darcklh.louise.Utils.EncryptUtils;
import com.darcklh.louise.Utils.WorkThread;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Data
@Slf4j
public class CalcImageTask implements MultiTaskService {

    private int status;
    // 声明一个任务的自有业务含义的变量，用于标识任务
    private int taskId;
    private String taskInfo;
    private File image;

    public static Map<String, Image> NewMap = Collections.synchronizedMap(new HashMap<String, Image>());

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
            Image ig = new Image(image.getPath(), image.getName());
        }
        catch(IOException e) {
            log.info("读取图片: " + image.getName() + " 失败了");
        }
        //NewMap.put(md5, ig);
        setStatus(FINISHED);
        return true;
    }
}
