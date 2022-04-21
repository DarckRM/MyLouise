package com.darcklh.louise.Model;

import com.alibaba.fastjson.JSONObject;
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

    /**
     *
     * @param innerCode
     * @param errorMsg
     * @param jsonObject
     * @param originErrorMessage
     */
    public SpecificException(String innerCode, String errorMsg, JSONObject jsonObject, String originErrorMessage) {
        super(errorMsg);
        this.innerCode = "SRT" + innerCode;
        this.errorMsg = "RuntimeException: "+errorMsg;
        this.originErrorMessage = "OriginErrorMsg: " + originErrorMessage;
        this.jsonObject = jsonObject;
    }

}
