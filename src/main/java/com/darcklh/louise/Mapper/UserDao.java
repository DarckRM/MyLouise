package com.darcklh.louise.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.darcklh.louise.Model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * @author DarckLH
 * @date 2021/8/7 19:12
 * @Description
 */
@Mapper
public interface UserDao extends BaseMapper<User> {

    //重写一下这个 涉及到获取创建时间丢到SQL里简单一些
    @Override
    @Insert("INSERT INTO t_user (user_id, group_id, nickname, create_time, count_setu, count_upload) VALUES" +
            "(#{user_id}, #{group_id}, #{nickname}, NOW(), #{count_setu}, #{count_upload})")
    public int insert(User user);

    @Select("SELECT COUNT(user_id) FROM t_user WHERE user_id = #{user_id}")
    public Integer selectById(String user_id);
}
