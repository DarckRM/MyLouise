package com.darcklh.louise;

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

import java.util.HashMap;

@SpringBootTest
class MyLouiseApplicationTests {

    @Autowired
    UserDao userDao;

    @Test
    void contextLoads() {
        String testString = "!setu 萝莉,少女 白丝,黑丝".substring(1);
        String command = testString.split(" ")[0];
            switch (command) {
                case "setu":
                    testFunc(testString.substring(5),"tag");
            }

    }

    void testFunc(String testString, String commandType) {

        JSONObject loliBody = new JSONObject();
        loliBody.put("tag","");
        JSONArray loliParams = new JSONArray();
        for (String params : testString.split(" ")) {
            JSONArray loliParam = new JSONArray();
            for (String param : params.split(",")) {
                loliParam.add(param);
            }
            loliParams.add(loliParam);
        }
        loliBody.put("tag", loliParams);

        System.out.println(loliBody);
    }

    @Test
    void testMapper() throws Exception{
        Document document = Jsoup.parse("" +
                "<div class=\"col-xs-12 col-sm-12 col-md-8 col-xl-8 info-box\">\n" +
"                <div class=\"hash\">\n" +
"                    3b64f9d0ac5d96c071204dce855cb18a\n" +
"                </div>\n" +
"                <small class=\"text-muted\">1423x1958 JPEG 1554.4KB</small>\n" +
"                <div class=\"pull-xs-right\"></div>\n" +
"                <div class=\"detail-box gray-link\">\n" +
"                    <h6> <img class=\"to-link-icon\"\n" +
"                            src=\"/assets/pixiv-628a47348a82153ebc34acba4e5b287777a11631bb382dbb00fd4b88083bed95.ico\"\n" +
"                            alt=\"Pixiv\" width=\"14\" height=\"14\"> <a target=\"_blank\" rel=\"noopener\"\n" +
"                            href=\"https://www.pixiv.net/artworks/73793338\">お嬢かわいいよお嬢</a> <a target=\"_blank\"\n" +
"                            rel=\"noopener\" href=\"https://www.pixiv.net/users/136424\">たまやん</a> <small> pixiv </small>\n" +
"                    </h6>\n" +
"                </div>\n" +
"            </div>");
        Element elements = document.getElementsByClass("info-box").get(0).select(".detail-box h6").get(0);
        System.out.println(elements.child(0).attr("src")+"\n"+elements.child(1).text()+"\n"+elements.child(2));
    }

}
