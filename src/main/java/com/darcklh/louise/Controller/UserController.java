package com.darcklh.louise.Controller;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Model.Louise.User;
import com.darcklh.louise.Model.Result;
import com.darcklh.louise.Model.Saito.SysUser;
import com.darcklh.louise.Model.SpecificException;
import com.darcklh.louise.Model.VO.UserRole;
import com.darcklh.louise.Service.SysUserService;
import com.darcklh.louise.Service.UserService;
import com.darcklh.louise.Utils.isEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

@RestController
@RequestMapping("saito/user")
public class UserController {


    @Autowired
    UserService userService;

    @RequestMapping("findAll")
    public Result<UserRole> findAll() {
        Result<UserRole> result = new Result<>();
        List<UserRole> userList = userService.findAll();
        if (isEmpty.isEmpty(userList)) {
            result.setCode(202);
            return result;
        }
        result.setCode(200);
        result.setMsg("请求成功");
        result.setDatas(userList);
        return result;
    }

    @RequestMapping("switchStatus")
    public Result test(@RequestBody JSONObject jsonObject) {
        Result result = new Result();
        result.setMsg(userService.banUser(jsonObject.getString("user_id")));
        result.setCode(200);
        return result;
    }

    @RequestMapping("save")
    public Result edit(@RequestBody UserRole userRole) {
        Result result = new Result();
        result.setMsg(userService.joinLouise(userRole.getUser_id(), userRole.getGroup_id()).toString());
        result.setCode(200);
        return result;
    }

}
