package com.darcklh.louise.Api;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Model.Messages.InMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author DarckLH
 * @date 2022/10/15 22:40
 * @Description
 */
@Slf4j
@RestController
public class AiPaintApi {

    public JSONObject aiPaint(@RequestBody InMessage inMessage) {
        return null;
    }

}
