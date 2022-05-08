package com.darcklh.louise;

import com.alibaba.fastjson.JSONObject;
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

    /**
     * 获取当前计算机 CPU 负载
     * @throws InterruptedException
     */
    @Test
    public void getCpuPayLoad() throws InterruptedException {
//        while(true) {
//            SystemInfo systemInfo = new SystemInfo();
//            CentralProcessor processor = systemInfo.getHardware().getProcessor();
//            long[] prevTicks = processor.getSystemCpuLoadTicks();
//            Thread.sleep(1000);
//            long[] ticks = processor.getSystemCpuLoadTicks();
//            long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
//            long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
//            long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
//            long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
//            long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
//            long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
//            long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
//            long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
//            long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
//            log.info("CPU总数 = {},CPU利用率 ={}",processor.getLogicalProcessorCount(),new DecimalFormat("#.##%").format((1.0-(idle * 1.0 / totalCpu)) * 2));
//            Thread.sleep(5000);
//        }
    }

    @Test
    public void getMemInfo() throws InterruptedException {
        while(true) {
            SystemInfo systemInfo = new SystemInfo();
            GlobalMemory memory = systemInfo.getHardware().getMemory();
            long totalMem = memory.getTotal();
            long restMem = memory.getAvailable();

            log.info("可用内存：{}， 内存占用率：{}", formatByte(restMem), new DecimalFormat("#.##%").format((totalMem-restMem)*1.0/totalMem));
            Thread.sleep(5000);
        }
    }

    public static String formatByte(long byteNumber){
        //换算单位
        double FORMAT = 1024.0;
        double kbNumber = byteNumber/FORMAT;
        if(kbNumber<FORMAT){
            return new DecimalFormat("#.##KB").format(kbNumber);
        }
        double mbNumber = kbNumber/FORMAT;
        if(mbNumber<FORMAT){
            return new DecimalFormat("#.##MB").format(mbNumber);
        }
        double gbNumber = mbNumber/FORMAT;
        if(gbNumber<FORMAT){
            return new DecimalFormat("#.##GB").format(gbNumber);
        }
        double tbNumber = gbNumber/FORMAT;
        return new DecimalFormat("#.##TB").format(tbNumber);
    }
}
