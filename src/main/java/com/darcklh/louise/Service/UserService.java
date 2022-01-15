package com.darcklh.louise.Service;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Model.Louise.User;
import com.darcklh.louise.Model.VO.UserRole;

import java.util.List;

public interface UserService {

    public List<UserRole> findAll();
    public JSONObject joinLouise(String user_id, String group_id);
    public JSONObject myInfo(String user_id);
    public void updateCount(String user_id, int option);
    public String banUser(String user_id);
    public int isUserAvaliable(String user_id);
    public User selectById(String user_id);
    public int minusCredit( String user_id, int credit);



}
