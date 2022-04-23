package com.darcklh.louise;

import com.darcklh.louise.Model.Louise.ProcessImage;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Service.CBIRService;
import com.darcklh.louise.Service.Impl.CalcImageTask;
import com.darcklh.louise.Service.Impl.CompressImageTask;
import com.darcklh.louise.Utils.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.List;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MyLouiseApplicationTests {

//    @Autowired
//    CBIRService cbirService;
//
//    @Autowired
//    R r;
//
//    @Test
//    public void test1() throws InterruptedException, IOException {
////        cbirService.startCompress();
//        String url = "cache/images_index/pixiv/roeiur238974.jpg".substring(19);
//        log.info(url);
//    }
//
//    @Test
//    public void imageReadTest() {
//
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//
//        File file = new File("cache/images/Lpip/");
//        Map<String, ProcessImage> NewMap = Collections.synchronizedMap(new HashMap<String, ProcessImage>());
//        int noOfFiles = file.listFiles().length;
//        List threads1 = new ArrayList();
//
//        System.out.println("Total Files Discovered : " + noOfFiles);
//
//        for (File image : file.listFiles()) {
//            Thread t = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        String md5 = EncryptUtils.checkSumMD5(image.getAbsolutePath());
//                        ProcessImage ig = new ProcessImage(image.getPath(), image.getName());
//                        //Values for ImageAttr[]
//                        NewMap.put(md5, ig);
//                    } catch (Exception e) {
//                        System.out.println("rua");
//                    }
//                }
//            });
//            t.start();
//            threads1.add(t);
//        }
//
//        stopWatch.stop();
//        System.out.println("执行时长: " + stopWatch.getTotalTimeSeconds());
////        Image image1 = new Image("cache/images/Gelbooru/1.jpg", "A");
////        Image image2 = new Image("cache/images/Gelbooru/2.jpg", "B");
////        float a = cbirService.minkowskiDist(image1.getHistogramInfo(), image2.getHistogramInfo());
////        float b = cbirService.histogramIntersectionDist(image1.getHistogramInfo(), image2.getHistogramInfo());
////        float c = cbirService.relativeDeviationDist(image1.getHistogramInfo(), image2.getHistogramInfo());
////
////        System.out.println("闵可夫斯基距离: " + a);
////        System.out.println("直方图交点距离: " + b);
////        System.out.println("相对偏差距离: " + c);
//    }
//
//    @Test
//    public void multiTaskTest() throws InterruptedException, IOException, NoSuchAlgorithmException {
//        File file = new File("cache/images/compress/");
//
//        List taskList = new ArrayList();
//        testFunc2(file, taskList);
//        log.info("共有: " + taskList.size() + " 个任务");
//        // 设定要启动的工作线程数为 4 个
//        int threadCount = 12;
//        List[] taskListPerThread = TaskDistributor.distributeTasks(taskList, threadCount);
//        System.out.println("实际要启动的工作线程数：" + taskListPerThread.length);
//        for (int j = 0; j < taskListPerThread.length; j++) {
//            Thread workThread = new WorkThread(taskListPerThread[j], j);
//            workThread.start();
//        }
//        while(CalcImageTask.NewMap.size() != 395) {
//            log.info("当前处理数: " + CalcImageTask.NewMap.size());
//            Thread.sleep(500);
//        }
//        log.info("wc");
//    }
//
//    @Test
//    public void singleThreadTest() throws IOException, NoSuchAlgorithmException {
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//
//        File file = new File("cache/images/Lpip/");
//        Map<String, ProcessImage> NewMap = Collections.synchronizedMap(new HashMap<String, ProcessImage>());
//        int noOfFiles = file.listFiles().length;
//
//        System.out.println("Total Files Discovered : " + noOfFiles);
//
//        testFunc(file);
//        stopWatch.stop();
//        System.out.println("执行时长: " + stopWatch.getTotalTimeSeconds());
//
//    }
//
//    public void testFunc(File file) throws IOException, NoSuchAlgorithmException {
//        for (File image : file.listFiles()) {
//            if (image.isDirectory()) {
//                testFunc(image);
//                continue;
//            }
//            String md5 = EncryptUtils.checkSumMD5(image.getAbsolutePath());
//            try {
//                ProcessImage ig = new ProcessImage(image.getPath(), image.getName());
//            } catch (IOException e) {
//                System.out.println("读取图片 " + image.getName() + "失败");
//            }
//            //NewMap.put(md5, ig);
//        }
//    }
//
//    public void testFunc2(File file, List taskList) {
//        int i = 0;
//
//        for (File image : file.listFiles()) {
//            if (image.isDirectory()) {
//                testFunc2(image, taskList);
//                continue;
//            }
//            CalcImageTask task = new CalcImageTask(i, "task: " + i,  image);
//            if (task.getStatus() != 9)
//                taskList.add(task);
//            i++;
//        }
//    }
//
//    @Test
//    public void checkRepeat() throws IOException, NoSuchAlgorithmException {
//        List<String> list = new ArrayList();
//        for( File check : Objects.requireNonNull(new File("cache/images/compress").listFiles())) {
//            String mad5 = EncryptUtils.checkSumMD5(check.getAbsolutePath());
//            log.info(check.getAbsolutePath() + " : " + mad5);
//            list.add(mad5);
//        }
//        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
//        for (String string : list) {
//            if (hashMap.get(string) != null) {
//                Integer value = hashMap.get(string);
//                hashMap.put(string, value+1);
//                System.out.println("the element:"+string+" is repeat");
//            } else {
//                hashMap.put(string, 1);
//            }
//        }
//    }
//
//    @Test
//    public void testImageStreamCompress() throws IOException, NoSuchAlgorithmException, InterruptedException {
//        File testFile = new File("cache/temp/75377838.jpg");
//        Image src = ImageIO.read(testFile);
//        BufferedImage bufferedImage = ImageCompress.resizeImage(src, ((BufferedImage) src).getWidth() / 2, ((BufferedImage) src).getHeight() / 2);
//        ImageCompress.compress(bufferedImage, "cache/ready_compare/", "ready.jpg");
//        //multiTaskTest("cache/ready_compare/ready.jpg");
//    }
//
//    @Test
//    public void testNewImage() throws IOException {
//
//        File testFile = new File("cache/temp/75377838.jpg");
//        BufferedImage testImage = ImageIO.read(testFile);
//        Map<Integer, Double> temp = transverseImage(testImage);
//        System.out.println("rua");
//    }
//
//    public Map<Integer, Double> transverseImage(BufferedImage testImage) {
//
//        int width = testImage.getWidth();
//        int height = testImage.getHeight();
//        Map<Integer, Double> map = new HashMap<>();
//        for(int i = 0; i < 4096; i++) {
//            map.put(i, 0.0);
//        }
//        for(int i = 0; i < width; i++) {
//            for(int j = 0; j < height; j++) {
//                int rgb = testImage.getRGB(i, j);
//                int R = (rgb >> 16) & 0xff;
//                int G = (rgb >> 8) & 0xff;
//                int B = rgb & 0xff;
//                int rgbs = transverseRGBToInt(R, G, B);
//                map.put(rgbs, map.get(rgbs) + 1);
//            }
//        }
//        int totalPix = height * width;
//        for(int i = 0; i < 4096; i++) {
//            double value = map.get(i) / totalPix;
//            map.put(i, value);
//        }
//        return map;
//    }
//
//    private int transverseRGBToInt(int R, int G, int B) {
//        int value = (R / 16) << 8 | (G / 16) << 4 | (B / 16);
//        return value;
//    }
}
