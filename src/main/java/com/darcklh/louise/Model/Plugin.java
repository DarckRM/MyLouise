package com.darcklh.louise.Model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.darcklh.louise.Service.PluginService;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

/**
 * 插件封装类
 */
@Data
@Component
public class Plugin {

    @TableField(exist = false)
    PluginService pluginServices;

    @TableId
    private Integer id;
    private String author;
    private String name;
    private String path;
    private String class_name;
    private Timestamp create_time;
    private Integer isEnabled;
    private String info;

}
