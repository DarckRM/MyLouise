package com.darcklh.louise;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Controller.ImageInfoController;
import com.darcklh.louise.Mapper.ProcessImageDao;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MyLouiseApplicationTests {


//    @Test
//    public void test() {
//        RestTemplate restTemplate = new RestTemplate();
//        // 待下载的文件地址
//        String url = "https://files.yande.re/sample/3bace2fc8e6dcfc7503301552a68c59d/yande.re%201007564%20sample%20angel%20animal_ears%20blue_archive%20naked%20nekomimi%20nipples%20pussy%20sunaookami_shiroko%20uncensored%20wet%20yokami.jpg";
//        // 文件保存的本地路径
//        String targetPath = LouiseConfig.LOUISE_CACHE_IMAGE_LOCATION + "/Yande/random.jpg";
//        //定义请求头的接收类型
//        RequestCallback requestCallback = request -> request.getHeaders()
//                .setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
//        //对响应进行流式处理而不是将其全部加载到内存中
//        restTemplate.setRequestFactory(new HttpProxy().getFactory());
//        restTemplate.execute(url, HttpMethod.GET, requestCallback, clientHttpResponse -> {
//            Files.copy(clientHttpResponse.getBody(), Paths.get(targetPath));
//            return null;
//        });
//    }
}
