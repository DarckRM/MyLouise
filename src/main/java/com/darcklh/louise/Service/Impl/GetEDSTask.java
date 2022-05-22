package com.darcklh.louise.Service.Impl;

import com.darcklh.louise.Model.Louise.ProcessImage;
import com.darcklh.louise.Service.MultiTaskService;
import com.sun.source.tree.Tree;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.TreeMap;

/**
 * @author DarckLH
 * @date 2022/5/23 6:20
 * @Description
 */
@Data
public class GetEDSTask implements MultiTaskService {

    private int status;
    // 声明一个任务的自有业务含义的变量，用于标识任务
    private int taskId;
    private String taskInfo;
    private ProcessImage sampleImage;
    private double[][] inHistogram;

    public static TreeMap<Double, String> resultMap = new TreeMap<>();
    public static int totalTask = 0;

    public GetEDSTask(int taskId, ProcessImage sampleImage, double[][] inHistogram) {
        this.status = READY;
        this.taskId = taskId;
        this.sampleImage = sampleImage;
        this.inHistogram = inHistogram;
        this.taskInfo = sampleImage.getImage_name();
    }

    @Override
    public boolean execute() {

        double similar = 0.0;//相似度
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < inHistogram[i].length; j++){
                similar += Math.pow(inHistogram[i][j] - sampleImage.getHistogram_info()[i][j], 2);
            }
        }
        similar=Math.sqrt(similar);

        String image_path = sampleImage.getImage_path().substring(18);
        String image_name = sampleImage.getImage_name();
        resultMap.put(similar, image_path + "/" + image_name);

        totalTask++;
        setStatus(FINISHED);
        return true;
    }

}
