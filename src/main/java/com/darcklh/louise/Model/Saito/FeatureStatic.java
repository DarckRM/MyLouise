package com.darcklh.louise.Model.Saito;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @author DarckLH
 * @date 2021/9/20 16:59
 * @Description 功能调用统计
 */
@Data
public class FeatureStatic {

    @TableId
    private Integer invoke_id;
    private Integer feature_id;
    private Timestamp invoke_time;
    private Integer count;

}
