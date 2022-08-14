package com.darcklh.louise.Api;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Model.Messages.InMessage;
import com.darcklh.louise.Model.ReplyException;
import com.darcklh.louise.Service.SearchPictureService;
import com.darcklh.louise.Utils.UniqueGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 识别发送的图片的Api
 */
@RestController
@Slf4j
public class SearchPictureApi{

    @Autowired
    SearchPictureService searchPictureService;

    /**
     * 根据图片以及参数调用识图接口
     * @param inMessage
     * @return
     */
    @RequestMapping("louise/find")
    private JSONObject findPicture(@RequestBody InMessage inMessage) {

        //返回值
        JSONObject returnJson = new JSONObject();
        // 提前判断是否具有参数避免无用请求
        if (inMessage.getMessage().length() <= 5)
            throw new ReplyException(LouiseConfig.LOUISE_ERROR_UPLOAD_IMAGE_FAILED);

        //解析上传的信息 拿到图片URL还有一些相关参数
        String url = inMessage.getMessage();
        url = url.substring(url.indexOf("url=")+4, url.length()-1);

        log.info("上传图片的地址:"+ url);

        String finalUrl = url;
        new Thread(() -> searchPictureCenter(inMessage, finalUrl)).start();

        returnJson.put("reply", inMessage.getSender().getNickname()+"! 露易丝在搜索了哦！" +
                "\n目前Ascii2d搜索引擎仍在测试中，受网络影响较大！");
        return returnJson;

    }


    /**
     * 搜图入口
     * @param inMessage
     * @return
     */
    private void searchPictureCenter(InMessage inMessage, String url) {

        log.info("进入搜图流程, 发起用户为:"+ inMessage.getSender().getNickname()+" QQ:"+ inMessage.getUser_id());
        log.debug(inMessage.toString());

        // TODO 线程名过长
        // new Thread(() -> searchPictureService.findWithAscii2d(inMessage, url), UniqueGenerator.uniqueThreadName("", "A2d")).start();
        new Thread(() -> searchPictureService.findWithSourceNAO(inMessage, url), UniqueGenerator.uniqueThreadName("", "NAO")).start();
    }

}
