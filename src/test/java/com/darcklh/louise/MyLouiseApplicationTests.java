package com.darcklh.louise;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Model.Louise.Image;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Model.SpecificException;
import com.darcklh.louise.Service.CBIRService;
import com.darcklh.louise.Service.Impl.CalcImageTask;
import com.darcklh.louise.Service.Impl.UserImpl;
import com.darcklh.louise.Service.MultiTaskService;
import com.darcklh.louise.Utils.EncryptUtils;
import com.darcklh.louise.Utils.TaskDistributor;
import com.darcklh.louise.Utils.WorkThread;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MyLouiseApplicationTests {
//
//    @Autowired
//    CBIRService cbirService;
//
//    @Autowired
//    R r;
//
//    @Test
//    public void imageReadTest() throws IOException, NoSuchAlgorithmException {
//
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//
//        File file = new File("cache/images/Lpip/");
//        Map<String, Image> NewMap = Collections.synchronizedMap(new HashMap<String, Image>());
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
//                        Image ig = new Image(image.getPath(), image.getName());
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
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//        File file = new File("cache/images/Lpip/");
//        int i = 0;
//
//        List taskList = new ArrayList();
//        testFunc2(file, taskList);
//
//        // 设定要启动的工作线程数为 4 个
//        int threadCount = 12;
//        List[] taskListPerThread = TaskDistributor.distributeTasks(taskList, threadCount);
//        System.out.println("实际要启动的工作线程数：" + taskListPerThread.length);
//        for (int j = 0; j < taskListPerThread.length; j++) {
//            Thread workThread = new WorkThread(taskListPerThread[j], j);
//            workThread.start();
//        }
//        System.out.println(CalcImageTask.NewMap);
//        Thread.sleep(15000);
//        stopWatch.stop();
//        System.out.println("执行时长: " + stopWatch.getTotalTimeSeconds());
//    }
//
//    @Test
//    public void singleThreadTest() throws IOException, NoSuchAlgorithmException {
//        StopWatch stopWatch = new StopWatch();
//        stopWatch.start();
//
//        File file = new File("cache/images/Lpip/");
//        Map<String, Image> NewMap = Collections.synchronizedMap(new HashMap<String, Image>());
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
//                Image ig = new Image(image.getPath(), image.getName());
//            } catch (IOException e) {
//                System.out.println("读取图片 " + image.getName() + "失败");
//            }
//            //NewMap.put(md5, ig);
//        }
//    }
//
//    public void testFunc2(File file, List taskList) throws IOException, NoSuchAlgorithmException {
//        int i = 0;
//        for (File image : file.listFiles()) {
//            if (image.isDirectory()) {
//                testFunc2(image, taskList);
//                continue;
//            }
//            taskList.add(new CalcImageTask(i, "task: " + i, image));
//            i++;
//        }
//    }
//
//    @Test
//    public void testBotSendPicture() {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("user_id", "412543224");
//        jsonObject.put("message", "[CQ:image,file=file://../../../../1.jpg]");
//        r.sendMessage(jsonObject);
//    }
}
