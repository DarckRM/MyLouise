package com.darcklh.louise.Service;

import com.darcklh.louise.Model.Louise.Group;
import com.darcklh.louise.Model.VO.GroupRole;

import java.util.List;

/**
 * @author DarckLH
 * @date 2021/9/28 19:47
 * @Description
 */
public interface GroupService {

    public List<GroupRole> findBy();
    public String delBy();
    public String editBy();
    public String add(Group group);
    public String switchStatus(String group_id);
    public boolean isGroupExist(String group_id);
    public boolean isGroupEnabled(String group_id);
}
