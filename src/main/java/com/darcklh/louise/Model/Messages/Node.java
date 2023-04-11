package com.darcklh.louise.Model.Messages;

import com.darcklh.louise.Config.LouiseConfig;
import lombok.Data;

/**
 * @author DarckLH
 * @date 2022/8/13 2:24
 * @Description
 */
@Data
public class Node {
    String type;
    DataType data = new DataType();

    public Node(String message, Long self_id) {
        this.type = "node";
        this.data.content = message;
        this.data.uin = self_id;
    }

    public Node() {
        this.type = "node";
        this.data.content = "";
    }

    public static Node build() {
        Node node = new Node();
        node.data.uin = Long.parseLong(LouiseConfig.BOT_ACCOUNT);
        return node;
    }

    public Node text(String text) {
        this.data.content += text;
        return this;
    }

    public Node image(String image) {
        this.data.content += "[CQ:image,file=" + image + "]";
        return this;
    }

    public Node reply() {
        return this;
    }
}
