package com.darcklh.louise.Utils;

import com.alibaba.fastjson.JSON;
import com.darcklh.louise.Model.Messages.InMessage;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * @author DarckLH
 * @date 2022/11/4 6:30
 * @Description
 */
public class InMessageEncoder implements Encoder.Text<InMessage> {
    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }

    @Override
    public String encode(InMessage inMessage) throws EncodeException {
        return JSON.toJSONString(inMessage);
    }
}
