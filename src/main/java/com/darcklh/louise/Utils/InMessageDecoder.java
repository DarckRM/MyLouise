package com.darcklh.louise.Utils;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Model.Messages.InMessage;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 * @author DarckLH
 * @date 2022/11/4 6:29
 * @Description
 */
public class InMessageDecoder implements Decoder.Text<InMessage> {
    @Override
    public void init(EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy() {

    }


    @Override
    public InMessage decode(String msg) throws DecodeException {
        return JSONObject.parseObject(msg, InMessage.class);
    }

    @Override
    public boolean willDecode(String s) {
        return true;
    }

}
