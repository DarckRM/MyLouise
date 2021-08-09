package com.darcklh.louise.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.darcklh.louise.Model.UploadInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author DarckLH
 * @date 2021/8/7 17:57
 * @Description
 */
@Mapper
public interface UploadInfoDao extends BaseMapper<UploadInfo> {

    public UploadInfo findMyUploadInfo();

}
