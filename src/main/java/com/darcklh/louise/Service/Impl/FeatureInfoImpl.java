package com.darcklh.louise.Service.Impl;

import com.darcklh.louise.Mapper.FeatureInfoDao;
import com.darcklh.louise.Model.Saito.FeatureInfo;
import com.darcklh.louise.Model.VO.FeatureInfoMin;
import com.darcklh.louise.Service.FeatureInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author DarckLH
 * @date 2021/9/28 15:43
 * @Description
 */
@Service
public class FeatureInfoImpl implements FeatureInfoService {

    @Autowired
    FeatureInfoDao featureInfoDao;

    @Override
    public List<FeatureInfo> findBy() {
        return featureInfoDao.selectList(null);
    }

    @Override
    public String delBy(Integer feature_id) {
        return null;
    }

    @Override
    public String editBy(FeatureInfo featureInfo) {

        String reply = "功能<" + featureInfo.getFeature_name() +">修改失败了！";
        if(featureInfoDao.updateById(featureInfo) > 0)
            reply = "功能<" + featureInfo.getFeature_name() +">修改成功！";
        return reply;
    }

    @Override
    public String add(FeatureInfo featureInfo) {

        featureInfo.setIs_enabled(-1);
        String reply = "功能<" + featureInfo.getFeature_name() +">添加失败了！";
        if (featureInfoDao.insert(featureInfo) > 0)
            reply = "功能<" + featureInfo.getFeature_name() +">添加成功！";
        return reply;
    }

    public Integer isEnabled(Integer feature_id) {
        return featureInfoDao.isEnabled(feature_id);
    }

    public String switchStatus(Integer feature_id, String feature_name) {
        String reply = "变更状态失败";
        if (featureInfoDao.switchStatus(feature_id) == 1) {
            reply = isEnabled(feature_id) == 1 ? "功能<"+feature_name+">已启用" : "功能<"+feature_name+">已禁用";
        }
        return reply;
    }

    @Override
    public FeatureInfo findWithFeatureURL(String feature_url) {
        return featureInfoDao.findWithFeatureURL(feature_url);
    }

    @Override
    public List<FeatureInfoMin> findWithRoleId(Integer role_id) {
        return featureInfoDao.findWithRoleId(role_id);
    }
}
