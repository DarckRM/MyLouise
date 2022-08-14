package com.darcklh.louise.Controller;
import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Model.R;
import com.darcklh.louise.Model.ReplyException;
import com.darcklh.louise.Model.Result;
import com.darcklh.louise.Model.SpecificException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author DarckLH
 * @date 2022/4/18 21:33
 * @Description
 */
@Slf4j
@ControllerAdvice(annotations = RestController.class)
public class NaiveSaitoControllerHandler {

    @Autowired
    R r;

    @ExceptionHandler(value = SpecificException.class)
    @ResponseBody
    public JSONObject specificExceptionHandler(SpecificException sE) {
        JSONObject jsonObject;
        Result<String> result = new Result<>();
        result.setMsg(sE.getMessage());
        log.info("errorMsg={},innerCode={},exception={}", sE.getErrorMsg(), sE.getInnerCode(), sE.getOriginErrorMessage());
        jsonObject = sE.getJsonObject();
        jsonObject.put("result", result);
        return jsonObject;//正式返回给前端信息
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

    @ExceptionHandler(value = ReplyException.class)
    @ResponseBody
    public JSONObject handleReplyException(ReplyException e) throws UnsupportedEncodingException {
        if (e.getType() == 0)
            return e.getReply();
        r.sendMessage(e.getOutMessage());
        return null;
    }

    void setData(Result result,String errorMsg, Integer innerCode, Exception e) throws UnsupportedEncodingException {
        result.setMsg(errorMsg);
        result.setCode(innerCode);
        log.error("errorMsg={},innerCode={},exception={}" ,errorMsg,innerCode,e);
        log.info("出现异常");
    }
}
