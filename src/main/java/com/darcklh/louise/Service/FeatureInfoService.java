package com.darcklh.louise.Service;

import com.darcklh.louise.Model.Saito.FeatureInfo;


/**
 * @author DarckLH
 * @date 2021/9/28 15:40
 * @Description
 */
public interface FeatureInfoService extends BaseService<FeatureInfo>{
    public String switchStatus(Integer feature_id, String feature_name);
}
