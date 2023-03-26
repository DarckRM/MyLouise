package com.darcklh.louise.Model.MultiThreadTask;

import com.darcklh.louise.Api.FileControlApi;
import com.darcklh.louise.Service.MultiTaskService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author DarckLH
 * @date 2023/3/26 11:40
 * @Description 用于下载图片的子任务
 */
@Data
@Slf4j
public class DownloadPicTask implements MultiTaskService {

    private int status;
    private int taskId;

    private String urlList;
    private String fileName;
    private String fileOrigin;
    // 总任务计数器
    public static List<Integer> already_downloaded = Collections.synchronizedList(new ArrayList<>());

    private FileControlApi fileControlApi;

    public DownloadPicTask(String urlList, String fileName, String fileOrigin, FileControlApi fileControlApi) {
        this.urlList = urlList;
        this.fileName = fileName;
        this.fileOrigin = fileOrigin;
        this.fileControlApi = fileControlApi;
    }

    @Override
    public synchronized boolean execute() throws NoSuchAlgorithmException, IOException {
        setStatus(MultiTaskService.RUNNING);
        fileControlApi.downloadPicture_RestTemplate(urlList, fileName, fileOrigin);
        already_downloaded.add(0);
        log.info("当前下载数: " + DownloadPicTask.already_downloaded.size());
        setStatus(FINISHED);
        return true;
    }
}
