package com.darcklh.louise.Model;

import org.springframework.beans.factory.annotation.Value;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
public class R {

    //各种信息
    @Value("${LOUISE.unknown_command}")
    public String UNKNOWN_COMMAND;
    @Value("${LOUISE.thirdApi_request_failed}")
    public String THIRDAPI_REQUEST_FAILED;

}
