package com.darcklh.louise.Service;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface CBIRService {

    /**
     *
     */
    public int reCalculateImageLib() throws InterruptedException, IOException, NoSuchAlgorithmException;
    public JSONObject startAllCBIR(boolean isFinding) throws InterruptedException, IOException, NoSuchAlgorithmException;
    public void startAllCompress() throws InterruptedException;
    public int startCompressAndCalc(String file_path) throws IOException, NoSuchAlgorithmException;
    public JSONObject compareImageCompress(String compare_image) throws IOException, NoSuchAlgorithmException, InterruptedException;

}
