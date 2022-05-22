package com.darcklh.louise.Service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Mapper.ProcessImageDao;
import com.darcklh.louise.Model.Louise.ProcessImage;
import com.darcklh.louise.Service.CBIRService;
import com.darcklh.louise.Utils.ImageCompress;
import com.darcklh.louise.Utils.TaskDistributor;
import com.darcklh.louise.Utils.WorkThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.List;

@Service
@Slf4j
public class CBIRServiceImpl implements CBIRService {

    //默认启用多线程数
    private static int THREAD_COUNT = 12;

    @Autowired
    private LouiseConfig louiseConfig;

    @Autowired
    private ProcessImageDao processImageDao;

    public int reCalculateImageLib() throws InterruptedException, IOException, NoSuchAlgorithmException {

        log.info("开始执行图片数据库重构任务");
        // 重新生成缩略图文件夹
        log.info("重新生成缩略图文件夹");
        startAllCompress();
        startAllCBIR(false);
        log.info("清除数据库原有记录");
        processImageDao.removeAllData();
        log.info("写入新记录");
        for(Map.Entry<String, ProcessImage> entry : CalcImageTask.NewMap.entrySet()) {
            entry.getValue().setHistogram_json(Arrays.deepToString(entry.getValue().getHistogram_info()));
            processImageDao.insert(entry.getValue());
        }
        return 1;
    }

    public JSONObject startAllCBIR(boolean isFinding) throws InterruptedException, IOException, NoSuchAlgorithmException {

        File file = new File(louiseConfig.getLOUISE_CACHE_LOCATION() + "/images_index/");

        List<CalcImageTask> taskList = new ArrayList<>();
        initCalcImageTaskList(file, taskList);
        log.info("共有: " + taskList.size() + " 个 CalcImage 任务");
        List[] taskListPerThread = TaskDistributor.distributeTasks(taskList, THREAD_COUNT);
        log.debug("实际启动的工作线程数：" + taskListPerThread.length);
        startCalcImageWorkThread(taskListPerThread, taskList.size());
        if (isFinding)
            return compareImage(new File("cache/ready_compare/ready.jpg"));
        return null;
    }

    public void startAllCompress() throws InterruptedException {

        File file = new File(louiseConfig.getLOUISE_CACHE_IMAGE_LOCATION());
        List<CompressImageTask> taskList = new ArrayList<>();
        initCompressImageTask(file, taskList);
        log.info("共有: " + taskList.size() + " 个 CompressImage 任务");
        List[] taskListPerThread = TaskDistributor.distributeTasks(taskList, THREAD_COUNT);
        log.debug("实际要启动的工作线程数：" + taskListPerThread.length);
        startCompressWorkThread(taskListPerThread, taskList.size());
    }

    public int startCompressAndCalc(String file_path) throws IOException, NoSuchAlgorithmException {
        File file = new File(file_path);
        ImageCompress.resize(file.getParent(), file.getName());
        ProcessImage ig = new ProcessImage(file.getParent(), file.getName());
        CalcImageTask.NewMap.put(ig.getHash_code(), ig);
        processImageDao.insert(ig);
        return 1;
    }

    private void startCompressWorkThread(List[] taskListPerThread, int taskList_size) throws InterruptedException {
        int i = 0;
        for (List singleTask : taskListPerThread) {
            Thread workThread = new WorkThread(singleTask, i);
            workThread.start();
            i++;
        }
        Thread.sleep(1500);
        int status = 0;
        int flag = 0;
        while(CompressImageTask.totalTask != taskList_size) {
            log.info("当前处理数: " + CompressImageTask.totalTask);
            if (status != CompressImageTask.totalTask) {
                status = CompressImageTask.totalTask;
                Thread.sleep(1000);
            } else {
                if (flag == 3) {
                    log.info("任务 CompressImage 执行超时");
                    break;
                }
                flag++;
            }
        }
    }

    private void startCalcImageWorkThread(List[] taskListPerThread, int taskList_size) throws InterruptedException {
        for (int j = 0; j < taskListPerThread.length; j++) {
            Thread workThread = new WorkThread(taskListPerThread[j], j);
            workThread.start();
        }
        Thread.sleep(1500);
        int status = 0;
        int flag = 0;
        while(CalcImageTask.NewMap.size() != taskList_size) {
            log.info("当前处理数: " + CalcImageTask.NewMap.size());
            if (status != CalcImageTask.NewMap.size()) {
                status = CalcImageTask.NewMap.size();
                Thread.sleep(1000);
            } else {
                if (flag == 3) {
                    log.info("任务 CalImage 执行超时");
                    break;
                }
                flag++;
            }
        }
    }

    private void startGetEDSWorkThread(List[] taskListPerThread, int taskList_size) throws InterruptedException {
        int i = 0;
        for (List singleTask : taskListPerThread) {
            Thread workThread = new WorkThread(singleTask, i);
            workThread.start();
            i++;
        }
        Thread.sleep(1500);
        int status = 0;
        int flag = 0;
        while(GetEDSTask.totalTask != taskList_size) {
            log.info("当前处理数: " + GetEDSTask.totalTask);
            if (status != GetEDSTask.totalTask) {
                status = GetEDSTask.totalTask;
                Thread.sleep(1000);
            } else {
                if (flag == 3) {
                    log.info("任务 GetEDS 执行超时");
                    break;
                }
                flag++;
            }
        }
    }

    private void startGetHISWorkThread(List[] taskListPerThread, int taskList_size) throws InterruptedException {
        int i = 0;
        for (List singleTask : taskListPerThread) {
            Thread workThread = new WorkThread(singleTask, i);
            workThread.start();
            i++;
        }
        Thread.sleep(1500);
        int status = 0;
        int flag = 0;
        while(GetHISTask.totalTask != taskList_size) {
            log.info("当前处理数: " + GetHISTask.totalTask);
            if (status != GetHISTask.totalTask) {
                status = GetHISTask.totalTask;
                Thread.sleep(1000);
            } else {
                if (flag == 3) {
                    log.info("任务 GetHIS 执行超时");
                    break;
                }
                flag++;
            }
        }
    }

    public JSONObject compareImageCompress(String compare_image) throws IOException, NoSuchAlgorithmException, InterruptedException {

        File compareFile = new File(compare_image);
        BufferedImage src = ImageIO.read(compareFile);
        BufferedImage bufferedImage = ImageCompress.resizeImage(src, src.getWidth() / 2, src.getHeight() / 2);
        ImageCompress.compress(bufferedImage, "cache/ready_compare/", "ready.jpg");

        if (ProcessImageImpl.isImageLibUpdate) {
            Map<String, ProcessImage> ImageMap = new HashMap<>();
        }

        if (CalcImageTask.NewMap.size() != 0)
            return compareImage(compareFile);
        return startAllCBIR(true);
    }

    private JSONObject compareImage(File compare_image) throws InterruptedException {

        // 清空之前的结果缓存
        GetEDSTask.resultMap.clear();
        GetHISTask.resultMap.clear();
        int number = 0;
        ProcessImage ig = null;
        try {
            ig = new ProcessImage(compare_image.getParent(), compare_image.getName());
        }
        catch(NullPointerException | IOException | NoSuchAlgorithmException e) {
            log.info("读取图片: " + compare_image.getName() + " 失败了");
        }
        JSONObject jsonObject = new JSONObject();

        List<ProcessImage> result_mkList = new ArrayList<>();
        List<ProcessImage> result_hiList = new ArrayList<>();
        List<ProcessImage> result_rdList = new ArrayList<>();

        List<GetEDSTask> edsTaskList = new ArrayList<>();
        List<GetHISTask> hisTaskList = new ArrayList<>();
        assert ig != null;
        initGetEDSTask(ig.getHistogram_info(), edsTaskList);
        initGetHISTask(ig.getHistogram_info(), hisTaskList);
        log.info("共有: " + edsTaskList.size() + " 个 相似度计算 任务");
        List[] getEDSPerThread = TaskDistributor.distributeTasks(edsTaskList, THREAD_COUNT);
        List[] getHISPerThread = TaskDistributor.distributeTasks(hisTaskList, THREAD_COUNT);
        log.debug("实际要启动的工作线程数：" + getEDSPerThread.length);
        startGetEDSWorkThread(getEDSPerThread, edsTaskList.size());
        startGetEDSWorkThread(getHISPerThread, edsTaskList.size());

        while( number <= 4) {
            result_rdList.add(new ProcessImage("/saito/image/", GetEDSTask.resultMap.firstEntry().getValue(), false));
            result_hiList.add(new ProcessImage("/saito/image/", GetHISTask.resultMap.lastEntry().getValue(), false));

            GetEDSTask.resultMap.remove(GetEDSTask.resultMap.firstKey());
            GetHISTask.resultMap.remove(GetHISTask.resultMap.lastKey());

            number++;
        }
        jsonObject.put("result_mkList", result_mkList);
        jsonObject.put("result_hiList", result_hiList);
        jsonObject.put("result_rdList", result_rdList);
        return jsonObject;
    }

    private void initCalcImageTaskList(File file, List<CalcImageTask> taskList) {
        int i = 0;
        for (File image : Objects.requireNonNull(file.listFiles())) {
            if (image.isDirectory()) {
                initCalcImageTaskList(image, taskList);
                continue;
            }
            CalcImageTask task = new CalcImageTask(i, "task: " + i,  image);
            if (task.getStatus() != 9) {
                taskList.add(task);
                task = null;
            }
            i++;
        }
    }

    private void initCompressImageTask(File file, List<CompressImageTask> taskList) {
        int i = 0;
        for (File image : Objects.requireNonNull(file.listFiles())) {
            if (image.isDirectory()) {
                initCompressImageTask(image, taskList);
                continue;
            }
            CompressImageTask task = new CompressImageTask(i, "task: " + i,  image);
            if (task.getStatus() != 9)
                taskList.add(task);
            i++;
        }
    }

    private void initGetEDSTask(double[][] inHistogram, List<GetEDSTask> getEDSTaskList) {
        int i = 0;
        for(Map.Entry<String, ProcessImage> entry : CalcImageTask.NewMap.entrySet()) {
            GetEDSTask getEDSTask = new GetEDSTask(i, entry.getValue(), inHistogram);
            if (getEDSTask.getStatus() != 9)
                getEDSTaskList.add(getEDSTask);
            i++;
        }
    }

    private void initGetHISTask(double[][] inHistogram, List<GetHISTask> getHISTaskList) {
        int i = 0;
        for(Map.Entry<String, ProcessImage> entry : CalcImageTask.NewMap.entrySet()) {
            GetHISTask getHISTask = new GetHISTask(i, entry.getValue(), inHistogram);
            if (getHISTask.getStatus() != 9)
                getHISTaskList.add(getHISTask);
            i++;
        }
    }

    //传统的直方图相交法  统计RGB  归一化 后用交来求两个图片的相似度
    private double getHISimilarity(double [][] inHist,double  [][] sampleHist)
    {
        double similar = 0.0;//相似度
        double total = 0.0;
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < inHist[i].length; j++) {
                similar += Math.min(inHist[i][j], sampleHist[i][j]);
                total += inHist[i][j];
            }
        }
        // 标准化
        similar = similar / total;
        return similar;
    }
}
