package com.darcklh.louise.Api;

import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Config.LouiseConfig;
import com.darcklh.louise.Model.Result;
import com.darcklh.louise.Utils.UniqueGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author DarckLH
 * @date 2021/8/7 18:48
 * @Description 处理文件操作相关的接口
 */
@Slf4j
@RestController
public class FileControlApi {

    @Autowired
    LouiseConfig louiseConfig;

    /**
     * 上传图片
     * @param file
     * @return
     */
    @RequestMapping(value = "/saito/upload/image", produces = "application/json")
    public JSONObject uploadImage(MultipartFile file) throws IOException {
        JSONObject jsonObject = new JSONObject();
        Result<String> result = new Result<>();
        log.info("上传图片: " + file.getOriginalFilename());

        if (!file.isEmpty()) {

            String fileName = file.getOriginalFilename(); //获取上传的文件名
            String suffixName = fileName.substring(fileName.lastIndexOf("." )+ 1); //获取后缀名
            fileName = "Image_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "_" + UniqueGenerator.generateShortUuid() + "." + suffixName;
            result.setData(fileName);
            File dest = new File(new File(louiseConfig.getLOUISE_CACHE_IMAGE_LOCATION()).getAbsolutePath() + "/upload/" + fileName);
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }

            file.transferTo(dest);
            String realUrl = "/saito/image/" + fileName;
            result.setMsg("上传成功");
            result.setData(realUrl);
            jsonObject.put("file_name", fileName);
            jsonObject.put("result", result);
            return jsonObject;

        } else {
            result.setMsg("上传失败");
            jsonObject.put("result", result);
            return jsonObject;
        }
    }

    /**
     * images图片下载
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/saito/image/**")
    @ResponseBody
    public JSONObject imagesDownLoad(HttpServletRequest request, HttpServletResponse response){
        String url = request.getRequestURI();
        JSONObject jsonObject = new JSONObject();
        Result<String> result = new Result<>();
        String fileName = url.replace("/saito/image/","");

        File file = new File(louiseConfig.getLOUISE_CACHE_LOCATION() + "/images/" + fileName);
        if(!file.exists()){
            jsonObject.put("success",2);
            jsonObject.put("result","下载文件不存在");
            return jsonObject;
        }
        response.reset();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName );
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));) {
            byte[] buff = new byte[1024];
            OutputStream os  = response.getOutputStream();
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            jsonObject.put("success",2);
            jsonObject.put("result","下载文件不存在");
            return jsonObject;
        }
        jsonObject.put("success",0);
        jsonObject.put("result","下载文件成功");
        return jsonObject;
    }

    /**
     * 根据传入的URL下载图片到本地
     * @param urlList
     */
    public boolean downloadPictureURL_Single(String urlList, String fileName, String fileOrigin) {
        URL url = null;
        String filePath = louiseConfig.getLOUISE_CACHE_IMAGE_LOCATION() + fileOrigin + "/";
        String imageName = filePath + fileName + ".jpg";
        try {

            log.info("开始下载" + imageName + " 图片地址: " + urlList);
            url = new URL(urlList);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
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
            output.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            log.info("下载图片" + imageName + "失败");
            return false;
        }
        return true;
    }

    public boolean downloadPictureURL(String urlList, String fileName, String fileOrigin) {
        long start = System.currentTimeMillis();
        //判断目录是否存在
        String filePath = louiseConfig.getLOUISE_CACHE_IMAGE_LOCATION() + fileOrigin + "/";
        File folder = new File(filePath);

        if (!folder.exists() && !folder.isDirectory()) {
            log.info("创建了图片缓存文件夹" + fileOrigin);
            folder.mkdirs();
        }

        String imageName = filePath + fileName + ".jpg";

        File file = new File(imageName);
        if (file.exists()) {
            log.info("已经存在图片 " + fileName);
            return true;
        }
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        //第一次尝试连接 获取文件大小 以及是否支持断点续传
        try {
            CloseableHttpResponse response = getResponse(urlList, null, null);
            //状态码
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 404) {
                log.info("图片未找到");
                return false;
            }
            else if (statusCode != 206) {
                return downloadPictureURL_Single(urlList, fileName, fileOrigin);
            }

            //文件总大小 这里直接转换为int比较方便计算 因为我们的目的是下载小文件 int足够使用
            int contentLength = Math.toIntExact(response.getEntity().getContentLength());
            //字节数组 用来存储下载到的数据 下载完成后写入到文件
            byte[] bytesFile = new byte[contentLength];

            //状态码 = 206 时表示支持断点续传
            if (statusCode == HttpStatus.SC_PARTIAL_CONTENT) {
                //创建线程池
                ThreadPoolTaskExecutor downloadExecutor = getExecutor("d", 10);
                int k = 1024;
                //分块大小 这里选择80k
                int step = 80 * k;
                //用来分配任务的数组下标
                int index = 0;
                while (index < contentLength) {
                    int finalIndex = index;
                    //提交任务
                    downloadExecutor.execute(() -> {
                        //循环到成功
                        while (true) {
                            try {
                                //请求一个分块的数据
                                CloseableHttpResponse res = getResponse(urlList, finalIndex, finalIndex + step - 1);
                                HttpEntity entity = res.getEntity();
                                InputStream inputStream = entity.getContent();
                                //缓冲字节数组 大小4k
                                byte[] buffer = new byte[40 * k];
                                //读取到的字节数组长度
                                int readLength;
                                //分块内已读取到的位置下标
                                int totalRead = 0;
                                while ((readLength = inputStream.read(buffer)) > 0) {
                                    //把读取到的字节数组复制到总的字节数组的对应位置
                                    System.arraycopy(buffer, 0, bytesFile, finalIndex + totalRead, readLength);
                                    //下标移动
                                    totalRead += readLength;
                                }
                                EntityUtils.consume(entity);
                                //分段下载成功 结束任务
                                return;
                            } catch (IOException e) {
                                //分段下载失败 重新开始
                                log.warn(e.getMessage());
                            }
                        }

                    });
                index += step;
            }

            //等待任务结束 这里用了一个比较粗糙的方法
            log.info("等待任务结束");
            do {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (downloadExecutor.getActiveCount() > 0);
                downloadExecutor.shutdown();

            //把总字节数组写入到文件;
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytesFile, 0, bytesFile.length);
            fos.flush();
            fos.close();
            long end = System.currentTimeMillis();
            log.info("{} 下载完毕 用时 {}毫秒 总速度:{}KB/s", filePath.substring(filePath.lastIndexOf("/") + 1), (end - start), contentLength * 1000 / 1024 / (end - start));
        }
        } catch (IOException e) {
            log.info("连接出现异常");
            return false;
        }
        return true;
    }

    public static CloseableHttpResponse getResponse(String url, Integer start, Integer end) throws IOException {
        CloseableHttpClient client = getCloseableHttpClient();
        HttpGet get = new HttpGet(url);
        int endIndex = url.indexOf("/", url.indexOf("//") + 2);
        get.addHeader("Referer", url.substring(0, endIndex));
        get.addHeader("Range", "bytes=" + (start != null ? start : 0) + "-" + (end != null ? end : ""));
        CloseableHttpResponse execute = client.execute(get);
        return execute;
    }

    /**
     * 生成http客户端
     *
     * @return http客户端
     */
    private static CloseableHttpClient getCloseableHttpClient() {
        int connectionRequestTimeout = 30 * 1000;
        RequestConfig config = RequestConfig.custom()
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .setConnectTimeout(connectionRequestTimeout)
                .setSocketTimeout(connectionRequestTimeout).build();

        return HttpClients.custom()
                .setDefaultRequestConfig(config).build();
    }

    /**
     * 创建线程池
     *
     * @param name     线程池名称
     * @param coreSize 核心线程池大小
     * @return 线程池
     */
    public static ThreadPoolTaskExecutor getExecutor(String name, Integer coreSize) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程池大小
        executor.setCorePoolSize(coreSize);
        //最大线程数
        executor.setMaxPoolSize(coreSize);
        //队列容量
        executor.setQueueCapacity(1000);
        //活跃时间
        executor.setKeepAliveSeconds(300);
        //线程名字前缀
        executor.setThreadNamePrefix(name);

        // setRejectedExecutionHandler：当pool已经达到max size的时候，如何处理新任务
        // CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    private void downloadTo() {

    };

}
