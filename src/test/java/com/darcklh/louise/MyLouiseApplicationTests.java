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

//    @Autowired
//    R r;
//
//    @Test
//    public void test() {
//        // 向Bot请求群聊数据
//        JSONObject param = new JSONObject();
//        param.put("group_id", 1081885076);
//        // 禁用缓存
//        param.put("no_cache", "true");
//
//        JSONObject botReturn = r.requestAPI("get_group_info", param);
//        System.out.println(botReturn);
//    }
}
