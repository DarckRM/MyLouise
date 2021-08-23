package com.darcklh.louise;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.darcklh.louise.Mapper.UploadInfoDao;
import com.darcklh.louise.Mapper.UserDao;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class MyLouiseApplicationTests {

//    @Test
//    void contextLoads() throws IOException{
//        //构造Rest请求模板
//
//        //利用复杂构造器可以实现超时设置，内部实际实现为 HttpClient
//        RestTemplate restTemplate = new RestTemplate();
//
//        JSONObject jsonObject = new JSONObject();
//        String url = "https://ascii2d.net/search/color/b16dd972b78d60eee454ef10a776b8ed";
//        //构造请求SourceNAO的请求体
//        Map<String, String> map = new HashMap<>();
//        map.put("utf8","✓");
//        map.put("uri","https://c2cpicdw.qpic.cn/offpic_new/412543224//412543224-2695970684-20B609213970A220C1E90EF3923EE464/0?term=3");
//        System.out.println("https://ascii2d.net/search/uri/?utf8=✓&uri=https://c2cpicdw.qpic.cn/offpic_new/412543224//412543224-2695970684-20B609213970A220C1E90EF3923EE464/0?term=3");
//        //Document colorDocument = Jsoup.connect("https://ascii2d.net/search/uri/?utf8=✓&uri=https://c2cpicdw.qpic.cn/offpic_new/412543224//412543224-2695970684-20B609213970A220C1E90EF3923EE464/0?term=3").post();
//        String s = restTemplate.postForObject("https://ascii2d.net/search/uri", map, String.class);
//        //System.out.println("查询到的结果: " + colorDocument.toString().substring(35,99));
//        System.out.println("查询到的结果: " + s.substring(35,99));
//    }
//    @Test
//    public void test() {
//        RestTemplate restTemplate = new RestTemplate();
//        //构造请求SourceNAO的请求体
//        Map<String, String> map = new HashMap<>();
//        map.put("url", "https://c2cpicdw.qpic.cn/offpic_new/412543224//412543224-2695970684-20B609213970A220C1E90EF3923EE464/0?term=3");
//        map.put("api_key", "1b1b6103a6503e94cc5dcef861f041aadd72ec49");
//        map.put("db", "999");
//        map.put("output_type", "2");
//        map.put("numres", "1");
//
//        JSONObject result = JSON.parseObject(restTemplate.getForObject("https://saucenao.com/search.php"+"?url={url}&db={db}&api_key={api_key}&output_type={output_type}&numres={numres}", String.class, map));
//        System.out.println("查询到的结果: "+result);
//    }
//
//    @Test
//    public void testPostHeader() {
//        String url = "https://ascii2d.net/search/uri";
//        String utf8 = "✓";
//        String uri = "https://c2cpicdw.qpic.cn/offpic_new/412543224//412543224-2695970684-20B609213970A220C1E90EF3923EE464/0?term=3";
//
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("utf8", utf8);
//        params.add("uri", uri);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
//                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
//
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
//        System.out.println(response.getStatusCode() + " | " + response.getBody());
//    }

}
