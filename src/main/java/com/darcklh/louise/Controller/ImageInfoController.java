package com.darcklh.louise.Controller;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Api.FileControlApi;
import com.darcklh.louise.Model.Result;
import com.darcklh.louise.Service.CBIRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * @author DarckLH
 * @date 2022/4/22 0:45
 * @Description
 */
@RestController
@RequestMapping("/saito/image-info/")
public class ImageInfoController {

    @Autowired
    private FileControlApi fileControlApi;

    @Autowired
    private CBIRService cbirService;

    /**
     * 对所有的图片库进行特征计算，
     * @return JSONObject
     */
    @RequestMapping("init")
    public JSONObject initImageLib() {
        JSONObject jsonObject = new JSONObject();
        return jsonObject;
    }

    @RequestMapping("start_cbir")
    public JSONObject startCBIR(String compare_image) throws InterruptedException, NoSuchAlgorithmException, IOException {
        JSONObject jsonObject = new JSONObject();
        Result<JSONObject> result = new Result<>();
        result.setData(cbirService.compareImageCompress(compare_image));
        result.setMsg("查询完成");
        jsonObject.put("result", result);
        return jsonObject;
    }

    @RequestMapping("save")
    public JSONObject save(MultipartFile file) throws IOException {
        return fileControlApi.uploadImage(file);
    }

}
