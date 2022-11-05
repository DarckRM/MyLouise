package com.darcklh.louise.Controller;

import com.darcklh.louise.Model.Messages.InMessage;
import com.darcklh.louise.Model.WebSocketClient;
import com.darcklh.louise.Utils.InMessageDecoder;
import com.darcklh.louise.Utils.InMessageEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DarckLH
 * @date 2022/11/4 6:10
 * @Description
 */
@ServerEndpoint(value="/go-cqhttp", decoders = { InMessageDecoder.class }, encoders = { InMessageEncoder.class })
@Component
@Slf4j
public class CqhttpWSController {

    // 和 CQHTTP Reverse WS 的连接状态
    private boolean isConnect = false;

    // 监听者计数器，当计数器为 0 时停止接收 WS 的消息
    public static int listenerCounts = 0;

    // 被监听的 QQ 账号链表
    public static ArrayList<Long> accounts = new ArrayList<>();

    // 用于存放在监听状态下 WS 接收到的消息体
    public static ConcurrentHashMap<Long, InMessage> messageMap = new ConcurrentHashMap<>();

    // 存放唯一的和 CQHTTP 的会话
    private Session session;

    public void onOpen(Session session) {
        this.session = session;
        this.isConnect = true;

        log.info("成功和 go-cqhttp 建立 WebSocket 连接");
    }

    @OnClose
    public void onClose() {
        log.info("go-cqhttp 断开了连接");
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("go-cqhttp 错误 ,原因:"+error.getMessage());
        error.printStackTrace();
    }

    @OnMessage
    public void onMessage(InMessage inMessage, Session session) {
        log.debug("收到 go-cqhttp 报文:" + inMessage);
        // TODO 暂时先跳过所有心跳反应，后续可以实现 BOT 状态监听

        // 如果当前不除于监听状态则不接受任何消息
        if (listenerCounts == 0)
            return;

        log.info("正在监听来自 " + Arrays.toString(accounts.toArray()) + " 的消息");

        // 排除所有不是 message 类型且不属于监听对象的消息上报
        if (!inMessage.getPost_type().equals("message") || !accounts.contains(inMessage.getUser_id()))
            return;

        log.debug(inMessage.toString());

        // 向 messageMap 中写入消息体
        messageMap.put(inMessage.getUser_id(), inMessage);
    }

    public static void startWatch(Long user_id) {
        // 进入监听模式
        accounts.add(user_id);
        listenerCounts++;
    }

    public static void stopWatch(Long user_id) {
        // 监听计数器减少，移除多余消息
        listenerCounts--;
        messageMap.remove(user_id);
        accounts.remove(user_id);
    }

}
