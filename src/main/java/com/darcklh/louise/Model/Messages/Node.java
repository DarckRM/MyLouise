package com.darcklh.louise.Model.Messages;

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
}
