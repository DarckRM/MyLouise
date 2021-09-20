package com.darcklh.louise.Model.Louise;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author DarckLH
 * @date 2021/9/20 16:46
 * @Description 角色信息
 */
@Data
public class Role {

    @TableId
    private Integer role_id;
    private String role_name;
    private String info;

}
