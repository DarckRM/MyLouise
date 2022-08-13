package com.darcklh.louise.Model.Messages;

import com.darcklh.louise.Model.Sender;
import lombok.Data;

/**
 * @author DarckLH
 * @date 2022/8/8 20:57
 * @Description 包含 go-cqhttp 上报的消息体
 */
@Data
public class InMessage {

    // 事件发生的时间戳
    private Integer time;
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
    // 消息内容
    private String message;
    // 原始消息内容
    private String raw_message;
    // 字体
    private Integer font;
    // 发送人信息
    private Sender sender;
    // 临时会话来源
    private String temp_source;

}
