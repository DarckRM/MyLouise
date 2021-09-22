package com.darcklh.louise.Model.Saito;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author DarckLH
 * @date 2021/9/20 16:54
 * @Description 功能注册信息
 */
@Data
public class FeatureInfo {

    @TableId
    private Integer feature_id;
    private Integer type;
    private String feature_name;
    private String feature_cmd;
    private Integer credit_cost;
    private String invoke_limit;
    private String description;
    private String info;
    private Integer is_original;
    private Integer is_enabled;

}