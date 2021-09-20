package com.darcklh.louise.Model.Louise;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author DarckLH
 * @date 2021/9/20 16:46
 * @Description 角色功能对应
 */
@Data
public class RoleFeature {

    @TableId
    private Integer feature_id;
    private Integer role_id;
    private String info;
    private Integer is_enabled;

}
