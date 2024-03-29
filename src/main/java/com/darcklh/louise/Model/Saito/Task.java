package com.darcklh.louise.Model.Saito;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @author DarckLH
 * @date 2021/9/20 16:50
 * @Description 定时任务信息
 */
@Data
public class Task {

    @TableId
    private Integer task_id;
    private String task_name;
    private String cron;
    private Integer type;
    private String url;
    private String target;
    private Integer is_return;
    private Integer is_parameter;
    private String sender_type;
    private String number;
    private String parameter;
    private String info;
    private Integer is_enabled;

}
