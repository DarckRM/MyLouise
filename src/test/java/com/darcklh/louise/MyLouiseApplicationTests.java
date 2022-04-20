package com.darcklh.louise;

import com.darcklh.louise.Model.Louise.Image;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Service.CBIRService;
import com.darcklh.louise.Service.Impl.CalcImageTask;
import com.darcklh.louise.Utils.EncryptUtils;
import com.darcklh.louise.Utils.TaskDistributor;
import com.darcklh.louise.Utils.WorkThread;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MyLouiseApplicationTests {

    @Autowired
    CBIRService cbirService;

    @Test
    public void imageReadTest() throws IOException, NoSuchAlgorithmException {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        File file = new File("cache/images/Lpip/");
        Map<String, Image> NewMap = Collections.synchronizedMap(new HashMap<String, Image>());
        int noOfFiles = file.listFiles().length;
        List threads1 = new ArrayList();

        System.out.println("Total Files Discovered : " + noOfFiles);

        for (File image : file.listFiles()) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String md5 = EncryptUtils.checkSumMD5(image.getAbsolutePath());
                        Image ig = new Image(image.getPath(), image.getName());
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
        System.out.println("rua");
    }

    @Test
    public void multiTaskTest() throws InterruptedException {
        File file = new File("cache/images/Lpip/");
        int i = 0;

        List taskList = new ArrayList();
        for (File image : Objects.requireNonNull(file.listFiles())) {
            taskList.add(new CalcImageTask(i, "task: " + i, image));
            i++;
        }

        // 设定要启动的工作线程数为 4 个
        int threadCount = 16;
        List[] taskListPerThread = TaskDistributor.distributeTasks(taskList, threadCount);
        System.out.println("实际要启动的工作线程数：" + taskListPerThread.length);
        for (int j = 0; j < taskListPerThread.length; j++) {
            Thread workThread = new WorkThread(taskListPerThread[j], j);
            workThread.start();
        }
        Thread.sleep(10000);
        System.out.println(CalcImageTask.NewMap);
    }

    @Test
    public void singleThreadTest() throws IOException, NoSuchAlgorithmException {

        File file = new File("cache/images/Lpip/");
        Map<String, Image> NewMap = Collections.synchronizedMap(new HashMap<String, Image>());
        int noOfFiles = file.listFiles().length;

        System.out.println("Total Files Discovered : " + noOfFiles);

        for (File image : file.listFiles()) {
            String md5 = EncryptUtils.checkSumMD5(image.getAbsolutePath());
            Image ig = new Image(image.getPath(), image.getName());
            //Values for ImageAttr[]
            NewMap.put(md5, ig);
        }

    }
}
