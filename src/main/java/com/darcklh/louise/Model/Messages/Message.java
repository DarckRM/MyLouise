package com.darcklh.louise.Model.Messages;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Model.Sender;
import com.darcklh.louise.Utils.OkHttpUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;

import java.util.ArrayList;

@Data
public class Message {
    // 事件发生的时间戳
    private Long time;
    // 收到事件的机器人 QQ 号
    private Long self_id;
    // 上报类型 message: 消息; request: 请求; notice: 通知; meta_event: 元事件
    private String post_type;
    // 消息类型
    private String message_type;
    // 消息子类型, 如果是好友则是 friend, 如果是群临时会话则是 group, 如果是在群中自身发送则是 group_self
    private String sub_type;
    // 消息 ID
    private Integer message_id;
    // 发送者 QQ 号
    private Long user_id;
    // 发送群 QQ 号
    private Long group_id = (long)-1;
    // 数组类型消息
    private ArrayList<JSONObject> message = new ArrayList<>();
    // 合并转发时的消息结构体
    private ArrayList<Node> messages = new ArrayList<>();
    // 原始消息内容
    private String raw_message;
    // 字体
    private Integer font;
    // 发送人信息
    private Sender sender;
    // 临时会话来源
    private String temp_source;
    public Message (InMessage inMessage) {
        this.setMessage_type(inMessage.getMessage_type());
        this.setGroup_id(inMessage.getGroup_id());
        this.setUser_id(inMessage.getUser_id());
        this.setSender(inMessage.getSender());
        this.setMessage_id(inMessage.getMessage_id());
    }

    public Message() {

    }

    public static Message build() {
        return new Message();
    }

    public static Message build(InMessage inMessage) {
        return new Message(inMessage);
    }

    public Message clear() {
        this.message.clear();
        this.messages.clear();
        return this;
    }

    public Message at(Long user_id) {
        JSONObject obj = new JSONObject();
        obj.put("type", "at");
        JSONObject data = new JSONObject();
        data.put("qq", user_id);
        obj.put("data", data);
        this.message.add(obj);
        return this;
    }

    public Message at() {
        JSONObject obj = new JSONObject();
        obj.put("type", "at");
        JSONObject data = new JSONObject();
        data.put("qq", this.user_id);
        obj.put("data", data);
        this.message.add(obj);
        return this;
    }

    public Message text(String text) {
        JSONObject obj = new JSONObject();
        obj.put("type", "text");
        JSONObject data = new JSONObject();
        data.put("text", text);
        obj.put("data", data);
        this.message.add(obj);
        return this;
    }

    public Message image(String image) {
        JSONObject obj = new JSONObject();
        obj.put("type", "image");
        JSONObject data = new JSONObject();
        data.put("file", image);
        obj.put("data", data);
        this.message.add(obj);
        return this;
    }

    public Message reply() {
        JSONObject obj = new JSONObject();
        obj.put("type", "reply");
        JSONObject data = new JSONObject();
        data.put("id", this.message_id);
        obj.put("data", data);
        this.message.add(obj);
        return this;
    }

    public Message reply(Integer message_id) {
        JSONObject obj = new JSONObject();
        obj.put("type", "reply");
        JSONObject data = new JSONObject();
        data.put("id", message_id);
        obj.put("data", data);
        this.message.add(obj);
        return this;
    }

    public Message node(Node node) {
        this.messages.add(node);
        return this;
    }

    public Message node(Node node, int index) {
        this.messages.add(index, node);
        return this;
    }

    public void send() {
        R r = new R();
        r.send(this);
        this.clear();
    }

    public void send(MessageCallBack func) {
        R r = new R();
        func.call(r.send(this));
        this.clear();
    }

    public void fall() {
        R r = new R();
        r.fall(this);
        this.clear();
    }

    public interface MessageCallBack {
        void call(JSONObject result);
    }

}
