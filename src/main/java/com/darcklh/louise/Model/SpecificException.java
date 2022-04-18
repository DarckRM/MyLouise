package com.darcklh.louise.Model;

/**
 * @author DarckLH
 * @date 2022/4/18 21:37
 * @Description
 */
public class SpecificException extends RuntimeException {

    /**
     * 异常信息
     */
    private String errorMsg;
    /**
     * 错误码
     */
    private String innerCode;

    private int code;

    public String getInnerCode() {
        return innerCode;
    }

    public int getCode() {
        return code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }


    public SpecificException(String errorMsg) {
        super(errorMsg);
        this.code = 500;
        this.innerCode = "5000";
        this.errorMsg = errorMsg;
        this.errorMsg = "Internal Server Error: "+errorMsg;

    }

    public SpecificException(Integer innerCode, String errorMsg) {
        super(errorMsg);
        this.code = 500;
        this.innerCode = "500"+innerCode.toString();
        this.errorMsg = errorMsg;
        this.errorMsg = "Test Internal Server Error: "+errorMsg;

    }

    /**
     * 抛出逻辑异常的两个静态类，对应上面两种构造方法
     * @param errorMsg
     * @return
     */
    public static SpecificException le(String errorMsg) {
        return new SpecificException(errorMsg);
    }
    public static SpecificException le(Integer innerCode, String errorMsg) {
        return new SpecificException(innerCode,errorMsg);
    }
}
