package com.dogbalbirdbal.controller;


import com.dogbalbirdbal.database.manager.DataBaseServiceManager;
import com.dogbalbirdbal.database.vo.UserInfo;
import com.fasterxml.jackson.core.JsonParser;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.*;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.servlet.http.HttpServletRequest;

@RestController
public class MainController {

    DataBaseServiceManager dataBaseServiceManager;

    public MainController() {
        dataBaseServiceManager = DataBaseServiceManager.getInstance();
    }

    @GetMapping("myinfo/{id}")
    public HashMap<String, String> myInfoController(@PathVariable String id, HttpServletRequest request, Model model) {

        //
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        Object o = request.getSession().getAttribute(id);
        
        if ( o != null && o instanceof UserInfo userInfo ) {
            // TODO 세션에 데이터가 있을경우 
            stringStringHashMap.put("result", "Data있음");
            stringStringHashMap.put("data", userInfo.toString());
        } else {
            // TODO 세션에 데이터가 없을경우
            stringStringHashMap.put("Result", "세션에 데이터가 없음");
        }
        
        return stringStringHashMap;
    }

    @GetMapping("login/{id}/{password}")
    public HashMap<String, String> loginController(@PathVariable String id, @PathVariable String password) {

        HashMap<String, String> stringStringHashMap = new HashMap<>();
        
        UserInfo userInfo = dataBaseServiceManager.getUserInfo("id",id);
        
        if ( userInfo != null ) {
            String resultID = userInfo.getId();
            String resultPassword = userInfo.getPassword();
            if ( resultID.equalsIgnoreCase(id) ) {
                if (resultPassword.equalsIgnoreCase(password)) {
                    // TODO 로그인 성공 데이터
                    stringStringHashMap.put("Result", "로그인 성공!");
                    stringStringHashMap.put("data", userInfo.toString());
                } else {
                    // TODO 로그인 실패
                    stringStringHashMap.put("Result", "비밀번호 다름");
                }
            } else {
                //TODO 로그인 실패
                stringStringHashMap.put("Result", "로그인 실패 ID 불일치");
            }
        } else {
            // TODO 데이터 없음
            stringStringHashMap.put("Result", "로그인 실패 ID에 따른 데이터 없음!");
        }

        return stringStringHashMap;
    }

    @GetMapping("register/{name}/{id}/{password}/{email}")
    public HashMap<String, String> registerController(@PathVariable String name,
                                                      @PathVariable String id,
                                                      @PathVariable String password,
                                                      @PathVariable String email) {
        HashMap<String, String> stringStringHashMap = new HashMap<>();

        UserInfo emailUserInfo = dataBaseServiceManager.getUserInfo("email" , email);
        
        if ( emailUserInfo != null ) {
            stringStringHashMap.put("Result", "이메일 중복");
            return stringStringHashMap;
        }
        
        UserInfo idUserInfo = dataBaseServiceManager.getUserInfo("id", id);
        if ( idUserInfo != null ) {
            stringStringHashMap.put("Result", "ID 중복");
            return stringStringHashMap;
        }

        boolean success = dataBaseServiceManager.taskTransaction( connection -> {
            String sql = "insert into MyUser(name, id, password, email) values(?, ?, ?, ?)";
            PreparedStatement p = connection.prepareStatement(sql);
            p.setString(1, name);
            p.setString(2, id);
            p.setString(3, password);
            p.setString(4, email);
            p.executeUpdate();
        });

        if ( success ) {
            stringStringHashMap.put("Result", "가입성공");
        } else {
            stringStringHashMap.put("Result", "SQL Exception");
        }

        return stringStringHashMap;
    }

    @GetMapping("/")
    public String mainPage(Model model) {
        return String.format("Main_Page");
    }

    //crawling with url using jsoup
    @GetMapping("crawling/{url}")
        public String crawlingController(@PathVariable String url, Model model) {
        String url1 = "https://www.siksinhot.com/search?keywords=" + url;
        Document doc = null;
        String result = "";
        try {
            doc = Jsoup.connect(url1).get();
            Elements e1 = doc.select("#main_search > div > article:nth-child(1) > section > div > div > ul > li:nth-child(1) > figcaption > a > h2");
            Elements e2 = doc.select("#main_search > div > article:nth-child(1) > section > div > div > ul > li:nth-child(2) > figcaption > a > h2");
            Elements e3 = doc.select("#main_search > div > article:nth-child(1) > section > div > div > ul > li:nth-child(3) > figcaption > a > h2");

            System.out.println("1등 맛집: "+ e1.text());
            System.out.println("2등 맛집: "+ e2.text());
            System.out.println("3등 맛집: "+ e3.text());
            result += "1등 맛집: " +e1.text() + "2등 맛집: " + e2.text() +"3등 맛집: "+ e3.text();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return String.format(result);
    }
}
