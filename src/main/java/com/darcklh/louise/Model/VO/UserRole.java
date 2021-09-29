package com.darcklh.louise.Model.VO;

import com.baomidou.mybatisplus.annotation.TableId;
import com.darcklh.louise.Model.Louise.Role;
import com.darcklh.louise.Model.Louise.User;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author DarckLH
 * @date 2021/9/28 23:43
 * @Description
 */
@Data
public class UserRole {

    @TableId
    private String user_id;
    private String group_id;
    private String avatar;
    private String nickname;
    private Timestamp join_time;
    private Timestamp create_time;
    private Integer count_setu;
    private Integer count_upload;
    private Integer isEnabled;
    private Integer credit;
    private Integer credit_buff;
    private Integer role_id;
    private String role_name;
    private String info;
}
