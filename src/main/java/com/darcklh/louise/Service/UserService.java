package com.darcklh.louise.Service;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Model.Louise.User;

import java.util.List;

public interface UserService {

    public List<User> findAll();
    public JSONObject joinLouise(String user_id, String group_id);
    public JSONObject myInfo(String user_id);
    public void updateCount(String user_id, int option);
    public String banUser(String user_id);
    public boolean isUserExist(String user_id);
    public boolean isUserEnabled(String user_id);



}