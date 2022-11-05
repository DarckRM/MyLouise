package com.darcklh.louise.Api;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Controller.CqhttpWSController;
import com.darcklh.louise.Model.Messages.InMessage;
import com.darcklh.louise.Model.Messages.OutMessage;
import com.darcklh.louise.Model.R;
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

    @Autowired
    R r;

    /**
     * 根据图片以及参数调用识图接口
     * @param inMessage
     * @return
     */
    @RequestMapping("louise/find")
    private JSONObject findPicture(@RequestBody InMessage inMessage) {

        OutMessage outMsg = new OutMessage(inMessage);

        new Thread(() -> {
            // 进入监听模式
            CqhttpWSController.startWatch(inMessage.getUser_id());
            InMessage wsMsg;
            int interval = 0;
            outMsg.setMessage("[CQ:at,qq=" + inMessage.getUser_id() + "]请在 15秒 内发送你要搜索的图片吧");
            r.sendMessage(outMsg);
            // 尝试从 WS 获取参数
            while(true) {

                if (interval > 5) {
                    outMsg.setMessage("[CQ:at,qq=" + inMessage.getUser_id() + "]你已经很久没有理 Louise 了，下次再搜索吧");
                    r.sendMessage(outMsg);
                    CqhttpWSController.stopWatch(inMessage.getUser_id());
                    return;
                }

                wsMsg = CqhttpWSController.messageMap.get(inMessage.getUser_id());
                if (wsMsg != null) {
                    inMessage.setMessage(inMessage.getMessage() + wsMsg.getMessage());
                    outMsg.setMessage("[CQ:at,qq=" + inMessage.getUser_id() + "]收到你的图片了");
                    r.sendMessage(outMsg);
                    break;
                }
                interval++;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            CqhttpWSController.stopWatch(inMessage.getUser_id());
            //解析上传的信息 拿到图片URL还有一些相关参数
            String url = inMessage.getMessage();
            url = url.substring(url.indexOf("url=")+4, url.length()-1);

            log.info("上传图片的地址:"+ url);

            String finalUrl = url;
            new Thread(() -> {searchPictureCenter(inMessage, finalUrl); }).start();

            outMsg.setMessage("[CQ:at,qq=" + inMessage.getUser_id() + "]露易丝在搜索了哦！" +
                    "\n目前Ascii2d搜索引擎仍在测试中，受网络影响较大！");
            r.sendMessage(outMsg);

        }, UniqueGenerator.uniqueThreadName("SPC", "Watching message")).start();
        return null;
    }


    /**
     * 搜图入口
     * @param inMessage
     * @return
     */
    private void searchPictureCenter(InMessage inMessage, String url){

        log.info("进入搜图流程, 发起用户为:"+ inMessage.getSender().getNickname()+" QQ:"+ inMessage.getUser_id());
        log.debug(inMessage.toString());

        // TODO 线程名过长

        new Thread(() -> searchPictureService.findWithSourceNAO(inMessage, url), UniqueGenerator.uniqueThreadName("", "NAO")).start();
        new Thread(() -> searchPictureService.findWithAscii2d(inMessage, url), UniqueGenerator.uniqueThreadName("", "A2d")).start();

    }

}
