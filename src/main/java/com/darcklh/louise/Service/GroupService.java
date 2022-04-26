package com.darcklh.louise.Service;

import com.darcklh.louise.Model.Louise.Group;
import com.darcklh.louise.Model.VO.GroupRole;

import java.util.List;

/**
 * @author DarckLH
 * @date 2021/9/28 19:47
 * @Description
 */
public interface GroupService extends BaseService<Group> {

    public List<GroupRole> findGroupRoleBy();
    public List<Group> findBy();
    public String delByGroup_ID(String group_id);
    public Group selectById(String group_id);
    public String editBy(Group group);
    public String add(Group group);
    public String switchStatus(String group_id);
    public boolean isGroupExist(String group_id);
    public boolean isGroupEnabled(String group_id);
}
