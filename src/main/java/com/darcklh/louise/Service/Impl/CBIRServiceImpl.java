package com.darcklh.louise.Service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Model.Louise.ProcessImage;
import com.darcklh.louise.Service.CBIRService;
import com.darcklh.louise.Service.MultiTaskService;
import com.darcklh.louise.Utils.EncryptUtils;
import com.darcklh.louise.Utils.ImageCompress;
import com.darcklh.louise.Utils.TaskDistributor;
import com.darcklh.louise.Utils.WorkThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CBIRServiceImpl implements CBIRService {

    public JSONObject startCBIR(String compare_image) throws InterruptedException, IOException, NoSuchAlgorithmException {

        File file = new File("cache/images/compress/");

        List taskList = new ArrayList();
        initTaskList(file, taskList);
        log.info("共有: " + taskList.size() + " 个任务");
        // 设定要启动的工作线程数为 4 个
        int threadCount = 12;
        List[] taskListPerThread = TaskDistributor.distributeTasks(taskList, threadCount);
        System.out.println("实际要启动的工作线程数：" + taskListPerThread.length);
        for (int j = 0; j < taskListPerThread.length; j++) {
            Thread workThread = new WorkThread(taskListPerThread[j], j);
            workThread.start();
        }
        while(CalcImageTask.NewMap.size() != 395) {
            log.info("当前处理数: " + CalcImageTask.NewMap.size());
            Thread.sleep(500);
        }

        return compareImage(compare_image);
    }

    public JSONObject compareImageCompress(String compare_image) throws IOException, NoSuchAlgorithmException, InterruptedException {

        File testFile = new File(compare_image);
        Image src = ImageIO.read(testFile);
        BufferedImage bufferedImage = ImageCompress.resizeImage(src, ((BufferedImage) src).getWidth() / 2, ((BufferedImage) src).getHeight() / 2);
        ImageCompress.compress(bufferedImage, "cache/ready_compare/ready.jpg");
        if (CalcImageTask.NewMap.size() != 0)
            return compareImage(compare_image);
        return startCBIR("cache/ready_compare/ready.jpg");
    }

    private JSONObject compareImage(String compare_image) {

        File compare_file = new File(compare_image);
        ProcessImage ig = null;
        try {
            ig = new ProcessImage(compare_file.getPath(), compare_file.getName());
        }
        catch(NullPointerException | IOException | NoSuchAlgorithmException e) {
            log.info("读取图片: " + compare_file.getName() + " 失败了");
        }
        JSONObject jsonObject = new JSONObject();
        float max = 5000;
        String result_image = "";
        List<ProcessImage> result_imageList = new ArrayList<>();
        Map<String, ProcessImage> finalList = CalcImageTask.NewMap;
        for(Map.Entry<String, ProcessImage> entry : finalList.entrySet()) {
            float hisID = relativeDeviationDist(ig.getHistogram_info(), entry.getValue().getHistogram_info());
            if (hisID < max) {
                max = hisID;
                result_image = entry.getValue().getImage_name();
                result_imageList.add(new ProcessImage("/saito/image/", result_image, false));
                log.info("测试图片和数据图片" + result_image + "的距离为: " + max);
            }
        }
        jsonObject.put("result_imageList", result_imageList);
        return jsonObject;
    }

    public void initTaskList(File file, List taskList) {
        int i = 0;

        for (File image : file.listFiles()) {
            if (image.isDirectory()) {
                initTaskList(image, taskList);
                continue;
            }
            CalcImageTask task = new CalcImageTask(i, "task: " + i,  image);
            if (task.getStatus() != 9)
                taskList.add(task);
            i++;
        }
    }

    @Override
    public float minkowskiDist(double[] InHist, double[] sampleHist) {
        float minkDist = 0;
        for (int i=0;i<InHist.length-1;i++)
            minkDist+=Math.pow((InHist[i]-sampleHist[i]),2);
        return (float) Math.sqrt(minkDist);
    }

    @Override
    public float histogramIntersectionDist(double[] InHist, double[] sampleHist) {
        float HIDist = 0;
        for (int i=0;i<InHist.length-1;i++)
            HIDist+=Math.min(InHist[i],sampleHist[i]);
        return HIDist;
    }

    @Override
    public float relativeDeviationDist(double[] InHist, double[] sampleHist) {
        float RDDist = 0;
        float Numerator=0;
        float Denom1=0;
        float Denom2=0;

        for (int i=0;i<InHist.length-1;i++)
        {
            Numerator+= Math.sqrt(Math.pow((InHist[i]-sampleHist[i]),2));
            Denom1+=Math.sqrt(Math.pow(InHist[i],2));
            Denom2+=Math.sqrt(Math.pow(sampleHist[i],2));
        }
        RDDist=2*Numerator/(Denom1+Denom2);
        return RDDist;
    }
}
