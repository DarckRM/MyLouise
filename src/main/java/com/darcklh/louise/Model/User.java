package com.darcklh.louise.Model;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @author DarckLH
 * @date 2021/8/7 18:57
 * @Description 使用MyLouise的用户信息
 */
@Data
public class User {

    private String user_id;
    private String group_id;
    private String nickname;
    private Timestamp join_time;
    private Timestamp create_time;
    private Integer count_setu;
    private Integer count_upload;

}
