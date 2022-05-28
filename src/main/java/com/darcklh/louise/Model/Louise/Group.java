package com.darcklh.louise.Model.Louise;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author DarckLH
 * @date 2021/9/20 0:48
 * @Description QQ群组
 */
@Data
public class Group {

    @TableId
    private String group_id;
    private Integer role_id;
    private String member_count;
    private String group_memo;
    private String group_name;
    private Integer group_level;
    private Integer is_enabled;
    private String avatar;
}
