package com.darcklh.louise.Model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author DarckLH
 * @date 2021/9/13 23:42
 * @Description
 */
@Data
public class Result<T> {

    public List<T> datas;
    public T data;
    public Integer code;
    public String msg;

}
