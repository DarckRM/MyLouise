package com.darcklh.louise.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author DarckLH
 * @date 2021/8/7 18:48
 * @Description 处理文件操作相关的接口
 */
@Service
public class FileControlApi {

    //Louise存放图片的路径
    @Value("${LOUISE_CACHE_IMAGE_LOCATION}")
    private String LOUISE_IMAGE_CACHE;

    Logger logger = LoggerFactory.getLogger(FileControlApi.class);

    /**
     * 根据传入的URL下载图片到本地
     * @param urlList
     */
    public void downloadPicture(String urlList, String fileName) {
        URL url = null;
        //判断目录是否存在
        File folder = new File(LOUISE_IMAGE_CACHE);
        if (!folder.exists() && !folder.isDirectory()) {

            logger.info("创建了图片缓存文件夹");
            folder.mkdirs();

        }

        try {
            url = new URL(urlList);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());

            String imageName = LOUISE_IMAGE_CACHE + fileName + ".jpg";

            //判断文件是否存在
            if (new File(imageName).exists()) {
                logger.info("文件" + imageName + "已存在");
                return;
            }

            FileOutputStream fileOutputStream = new FileOutputStream(new File(imageName));
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            logger.info("下载图片失败");
        }
    }

}
