package com.darcklh.louise.Model.Louise;

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
    private String member_count;
    private String group_name;
    private Integer group_level;
}
