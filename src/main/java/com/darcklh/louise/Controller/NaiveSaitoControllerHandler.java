package com.darcklh.louise.Controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Model.Result;
import com.darcklh.louise.Model.SpecificException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author DarckLH
 * @date 2022/4/18 21:33
 * @Description
 */
@ControllerAdvice(annotations = RestController.class)
public class NaiveSaitoControllerHandler {

    Logger logger = LoggerFactory.getLogger(NaiveSaitoControllerHandler.class);


    @ExceptionHandler(value = SpecificException.class)
    @ResponseBody
    public Object logicExceptionHandler(Exception e) throws UnsupportedEncodingException {
        //系统级异常，错误码固定为-1，提示语固定为系统繁忙，请稍后再试
        Result result = new Result();

        //如果是业务逻辑异常，返回具体的错误码与提示信息，即可以自定义多种异常类型
        if (e instanceof ConnectException) {
            ConnectException connectException = (ConnectException) e;
            setData(result,connectException.getMessage(),502, e);
        }else if (e instanceof SpecificException) {
            SpecificException specificException = (SpecificException) e;
            setData(result,specificException.getErrorMsg(),specificException.getCode(),e);
        }else {
            result.setMsg(e.getMessage());
            logger.error("errorMsg={},innerCode={},exception={}" ,e.getMessage(),500, e);
        }
        return JSONObject.toJSON(result);//正式返回给前端信息
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JSONObject handleRuntimeException(Exception e) throws UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        Result result = new Result();
        if (e instanceof java.sql.SQLIntegrityConstraintViolationException) {
            jsonObject.put("reply", "SQLIntegrityConstraintViolationException");
            setData(result, e.getCause().toString(), ((SQLIntegrityConstraintViolationException) e).getErrorCode(), e);
        } else if (e instanceof DuplicateKeyException){
            jsonObject.put("reply", "你已经注册过了哦");
            setData(result, e.getCause().toString(), 500, e);
        }
        jsonObject.put("result", result);
        return jsonObject;
    }

    @ExceptionHandler(value = SQLException.class)
    @ResponseBody
    public JSONObject handleDatabaseException(SQLException e) throws UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
        Result result = new Result();
        if (e instanceof java.sql.SQLIntegrityConstraintViolationException) {
            jsonObject.put("reply", "你已经注册过了哦");
        } else {
            jsonObject.put("reply", "数据库出现异常: " + e.getMessage());
        }
        setData(result, e.getMessage(), e.getErrorCode(), e);
        jsonObject.put("result", result);
        return jsonObject;
    }

    void setData(Result result,String errorMsg, Integer innerCode, Exception e) throws UnsupportedEncodingException {
        result.setMsg(errorMsg);
        result.setCode(innerCode);
        logger.error("errorMsg={},innerCode={},exception={}" ,errorMsg,innerCode,e);
        logger.info("出现异常");
    }
}
