package com.darcklh.louise;

import com.alibaba.fastjson.JSONObject;
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
import org.springframework.util.StopWatch;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MyLouiseApplicationTests {


//    @Test
//    public void getMemInfo() throws InterruptedException {
//        while(true) {
//            SystemInfo systemInfo = new SystemInfo();
//            GlobalMemory memory = systemInfo.getHardware().getMemory();
//            long totalMem = memory.getTotal();
//            long restMem = memory.getAvailable();
//
//            log.info("可用内存：{}， 内存占用率：{}", formatByte(restMem), new DecimalFormat("#.##%").format((totalMem-restMem)*1.0/totalMem));
//            Thread.sleep(5000);
//        }
//    }

//    public static String formatByte(long byteNumber){
//        //换算单位
//        double FORMAT = 1024.0;
//        double kbNumber = byteNumber/FORMAT;
//        if(kbNumber<FORMAT){
//            return new DecimalFormat("#.##KB").format(kbNumber);
//        }
//        double mbNumber = kbNumber/FORMAT;
//        if(mbNumber<FORMAT){
//            return new DecimalFormat("#.##MB").format(mbNumber);
//        }
//        double gbNumber = mbNumber/FORMAT;
//        if(gbNumber<FORMAT){
//            return new DecimalFormat("#.##GB").format(gbNumber);
//        }
//        double tbNumber = gbNumber/FORMAT;
//        return new DecimalFormat("#.##TB").format(tbNumber);
//    }
}
