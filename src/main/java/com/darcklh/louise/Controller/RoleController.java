package com.darcklh.louise.Controller;

import com.darcklh.louise.Model.Louise.Role;
import com.darcklh.louise.Model.Result;
import com.darcklh.louise.Model.VO.FeatureInfoMin;
import com.darcklh.louise.Model.VO.RoleFeatureId;
import com.darcklh.louise.Service.RoleService;
import com.darcklh.louise.Utils.isEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DarckLH
 * @date 2021/9/29 0:24
 * @Description
 */
@RestController
@RequestMapping("saito/role/")
public class RoleController {

    @Autowired
    RoleService roleService;

    @RequestMapping("findBy")
    public Result<Role> findBy() {
        Result<Role> result = new Result<>();
        List<Role> roles = roleService.findBy();
        if (isEmpty.isEmpty(roles)) {
            result.setCode(202);
            return result;
        }
        result.setCode(200);
        result.setMsg("请求成功");
        result.setDatas(roles);
        return result;
    }

    @RequestMapping("edit")
    public Result edit(@RequestBody RoleFeatureId roleFeatureId) {
        Result result = new Result();

        //先清除原本的role-feature信息
        if (roleService.delRoleFeature(roleFeatureId.getRole_id()) >= 0) {

            for (Integer feature_id: roleFeatureId.getFeatureInfoList()) {
                roleService.addRoleFeature(roleFeatureId.getRole_id(), feature_id + 1);
            }
            result.setMsg(roleService.edit(roleFeatureId));
        }
        result.setCode(200);
        return result;
    }

    @RequestMapping("switchStatus")
    public Result test(@RequestBody Role role) {
        Result result = new Result();
        System.out.println(role);
        result.setMsg(roleService.switchStatus(role.getRole_id(), role.getRole_name()));
        result.setCode(200);

        return result;
    }

    //TODO 实体类逻辑有点混乱 可以改进
    @RequestMapping("save")
    public Result save(@RequestBody RoleFeatureId roleFeatureId){
        Role role = new Role();
        FeatureInfoMin infoMin = new FeatureInfoMin();
        role.setFeatureInfoList(new ArrayList<>());
        role.setInfo(roleFeatureId.getInfo());
        role.setRole_name(roleFeatureId.getRole_name());
        for (Integer feature_id: roleFeatureId.getFeatureInfoList()) {
            infoMin.setFeature_id(feature_id);
            role.getFeatureInfoList().add(infoMin);
        }

        String msg = roleService.add(role);
        return null;
    }

}
