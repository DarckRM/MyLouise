package com.darcklh.louise.Model;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Model.Messages.InMessage;
import lombok.Data;

import java.io.IOException;

/**
 * @author DarckLH
 * @date 2022/4/18 21:37
 * @Description
 */
@Data
public class SpecificException extends RuntimeException {

    /**
     * 异常信息
     */
    private String errorMsg;

    /**
     * 错误码
     */
    private String innerCode;

    /**
     * 返回给QQ端的消息
     */
    private JSONObject jsonObject;

    /**
     * 原始异常信息
     */
    private String originErrorMessage;

    /**
     *
     * @param innerCode
     * @param errorMsg
     * @param originErrorMessage
     */
    public SpecificException(String innerCode, String errorMsg, String originErrorMessage) {
        super(errorMsg);
        this.innerCode = "SRT" + innerCode;
        this.errorMsg = "RuntimeException: "+errorMsg;
        this.originErrorMessage = "OriginErrorMsg: " + originErrorMessage;

    }

    public SpecificException(String innerCode, String errorMsg, JSONObject reply, String originErrorMessage) {
        super(errorMsg);
        this.innerCode = "SRT" + innerCode;
        this.errorMsg = "RuntimeException: "+errorMsg;
        this.originErrorMessage = "OriginErrorMsg: " + originErrorMessage;
        this.jsonObject = reply;
    }

    /**
     * 带返回体的异常
     * @param innerCode
     * @param errorMsg
     * @param reply
     * @param originErrorMessage
     */
    public SpecificException(String innerCode, String errorMsg, String reply, String originErrorMessage) {
        super(errorMsg);
        this.innerCode = "SRT" + innerCode;
        this.errorMsg = "RuntimeException: "+errorMsg;
        this.originErrorMessage = "OriginErrorMsg: " + originErrorMessage;
        this.jsonObject = (JSONObject) new JSONObject().put("reply", reply);
    }

    public SpecificException(String innerCode, String errorMsg, InMessage inMessage, String originErrorMessage) {

    }

}
