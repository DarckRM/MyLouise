package com.darcklh.louise.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.darcklh.louise.Model.Plugin;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface PluginDao extends BaseMapper<Plugin> {
}
