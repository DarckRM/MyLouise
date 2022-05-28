package com.darcklh.louise.Model.VO;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author DarckLH
 * @date 2021/9/29 0:08
 * @Description
 */
@Data
public class GroupRole {
    @TableId
    private String group_id;
    private String member_count;
    private String group_memo;
    private String group_name;
    private Integer group_level;
    private Integer is_enabled;
    private String avatar;
    private Integer role_id;
    private String role_name;
    private String info;
}
