package com.darcklh.louise.Controller;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Api.FileControlApi;
import com.darcklh.louise.Model.Result;
import com.darcklh.louise.Service.CBIRService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
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
@Slf4j
@RestController
@RequestMapping("/saito/image-info/")
public class ImageInfoController {

    @Autowired
    private FileControlApi fileControlApi;

    @Autowired
    private CBIRService cbirService;


    /**
     * 执行图片索引库初始化操作
     * 将图片缓存目录下的所有图片创建缩略图并放到image_index文件夹中
     * @return
     */
    @RequestMapping("init")
    public JSONObject initImageLib() {
        JSONObject jsonObject = new JSONObject();
        return jsonObject;
    }

    /**
     * 对图片索引库的所有图片进行特征计算，
     * @return JSONObject
     */
    public JSONObject createImageLibThumbnail() {
        JSONObject jsonObject = new JSONObject();
        return jsonObject;
    }

    @RequestMapping("start_cbir")
    public JSONObject startCBIR(@RequestBody String compare_image) throws InterruptedException, NoSuchAlgorithmException, IOException {
        log.info("待检索的图片本地位置: " + compare_image);
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
