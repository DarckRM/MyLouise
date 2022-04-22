package com.darcklh.louise;

import com.darcklh.louise.Model.Louise.ProcessImage;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Service.CBIRService;
import com.darcklh.louise.Service.Impl.CBIRServiceImpl;
import com.darcklh.louise.Service.Impl.CalcImageTask;
import com.darcklh.louise.Service.Impl.CompressImageTask;
import com.darcklh.louise.Utils.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.List;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MyLouiseApplicationTests {

    @Autowired
    CBIRService cbirService;

    @Autowired
    R r;

    @Test
    public void imageReadTest() {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        File file = new File("cache/images/Lpip/");
        Map<String, ProcessImage> NewMap = Collections.synchronizedMap(new HashMap<String, ProcessImage>());
        int noOfFiles = file.listFiles().length;
        List threads1 = new ArrayList();

        System.out.println("Total Files Discovered : " + noOfFiles);

        for (File image : file.listFiles()) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String md5 = EncryptUtils.checkSumMD5(image.getAbsolutePath());
                        ProcessImage ig = new ProcessImage(image.getPath(), image.getName());
                        //Values for ImageAttr[]
                        NewMap.put(md5, ig);
                    } catch (Exception e) {
                        System.out.println("rua");
                    }
                }
            });
            t.start();
            threads1.add(t);
        }

        stopWatch.stop();
        System.out.println("执行时长: " + stopWatch.getTotalTimeSeconds());
//        Image image1 = new Image("cache/images/Gelbooru/1.jpg", "A");
//        Image image2 = new Image("cache/images/Gelbooru/2.jpg", "B");
//        float a = cbirService.minkowskiDist(image1.getHistogramInfo(), image2.getHistogramInfo());
//        float b = cbirService.histogramIntersectionDist(image1.getHistogramInfo(), image2.getHistogramInfo());
//        float c = cbirService.relativeDeviationDist(image1.getHistogramInfo(), image2.getHistogramInfo());
//
//        System.out.println("闵可夫斯基距离: " + a);
//        System.out.println("直方图交点距离: " + b);
//        System.out.println("相对偏差距离: " + c);
    }

    @Test
    public void multiTaskTest(String compare_image) throws InterruptedException, IOException, NoSuchAlgorithmException {
        File file = new File("cache/images/compress/");

        List taskList = new ArrayList();
        testFunc2(file, taskList);
        log.info("共有: " + taskList.size() + " 个任务");
        // 设定要启动的工作线程数为 4 个
        int threadCount = 12;
        List[] taskListPerThread = TaskDistributor.distributeTasks(taskList, threadCount);
        System.out.println("实际要启动的工作线程数：" + taskListPerThread.length);
        for (int j = 0; j < taskListPerThread.length; j++) {
            Thread workThread = new WorkThread(taskListPerThread[j], j);
            workThread.start();
        }
        Thread.sleep(10000);

        File testFile = new File(compare_image);
        Image src = ImageIO.read(testFile);
        ProcessImage ig = null;
        try {
            ig = new ProcessImage(testFile.getPath(), testFile.getName());
        }
        catch(NullPointerException e) {
            log.info("读取图片: " + testFile.getName() + " 失败了");
        }
        float max = 5000;
        String result_image = "";
        Map<String, ProcessImage> finalList = CalcImageTask.NewMap;
        for(Map.Entry<String, ProcessImage> entry : finalList.entrySet()) {
            float hisID = cbirService.relativeDeviationDist(ig.getHistogram_info(), entry.getValue().getHistogram_info());
            if (hisID < max) {
                max = hisID;
                result_image = entry.getValue().getImage_name();
                log.info("测试图片和数据图片" + result_image + "的距离为: " + max);
            }
        }

    }

    @Test
    public void singleThreadTest() throws IOException, NoSuchAlgorithmException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        File file = new File("cache/images/Lpip/");
        Map<String, ProcessImage> NewMap = Collections.synchronizedMap(new HashMap<String, ProcessImage>());
        int noOfFiles = file.listFiles().length;

        System.out.println("Total Files Discovered : " + noOfFiles);

        testFunc(file);
        stopWatch.stop();
        System.out.println("执行时长: " + stopWatch.getTotalTimeSeconds());

    }

    public void testFunc(File file) throws IOException, NoSuchAlgorithmException {
        for (File image : file.listFiles()) {
            if (image.isDirectory()) {
                testFunc(image);
                continue;
            }
            String md5 = EncryptUtils.checkSumMD5(image.getAbsolutePath());
            try {
                ProcessImage ig = new ProcessImage(image.getPath(), image.getName());
            } catch (IOException e) {
                System.out.println("读取图片 " + image.getName() + "失败");
            }
            //NewMap.put(md5, ig);
        }
    }

    public void testFunc2(File file, List taskList) {
        int i = 0;

        for (File image : file.listFiles()) {
            if (image.isDirectory()) {
                testFunc2(image, taskList);
                continue;
            }
            CalcImageTask task = new CalcImageTask(i, "task: " + i,  image);
            if (task.getStatus() != 9)
                taskList.add(task);
            i++;
        }
    }

    @Test
    public void testBotSendPicture() throws InterruptedException {

        File file = new File("cache/images/");
        List taskList = new ArrayList();
        testFunc3(file, taskList);
        log.info("共有: " + taskList.size() + " 个任务");

        // 设定要启动的工作线程数为 4 个
        int threadCount = 12;
        List[] taskListPerThread = TaskDistributor.distributeTasks(taskList, threadCount);
        System.out.println("实际要启动的工作线程数：" + taskListPerThread.length);
        for (int j = 0; j < taskListPerThread.length; j++) {
            Thread workThread = new WorkThread(taskListPerThread[j], j);
            workThread.start();
        }
        Thread.sleep(30000);
    }

    public void testFunc3(File file, List taskList) {
        int i = 0;

        for (File image : file.listFiles()) {
            if (image.isDirectory()) {
                testFunc3(image, taskList);
                continue;
            }
            CompressImageTask task = new CompressImageTask(i, "task: " + i,  image);
            if (task.getStatus() != 9)
                taskList.add(task);
            i++;
        }
    }

    @Test
    public void checkRepeat() throws IOException, NoSuchAlgorithmException {
        List<String> list = new ArrayList();
        for( File check : Objects.requireNonNull(new File("cache/images/compress").listFiles())) {
            String mad5 = EncryptUtils.checkSumMD5(check.getAbsolutePath());
            log.info(check.getAbsolutePath() + " : " + mad5);
            list.add(mad5);
        }
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        for (String string : list) {
            if (hashMap.get(string) != null) {
                Integer value = hashMap.get(string);
                hashMap.put(string, value+1);
                System.out.println("the element:"+string+" is repeat");
            } else {
                hashMap.put(string, 1);
            }
        }
    }

    @Test
    public void testImageStreamCompress() throws IOException, NoSuchAlgorithmException, InterruptedException {
        File testFile = new File("cache/temp/75377838.jpg");
        Image src = ImageIO.read(testFile);
        BufferedImage bufferedImage = ImageCompress.resizeImage(src, ((BufferedImage) src).getWidth() / 2, ((BufferedImage) src).getHeight() / 2);
        ImageCompress.compress(bufferedImage, "cache/ready_compare/ready.jpg");
        multiTaskTest("cache/ready_compare/ready.jpg");
    }
}
