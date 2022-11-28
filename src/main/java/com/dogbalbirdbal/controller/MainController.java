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
import java.util.function.Consumer;

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

    @GetMapping("myinfo")
    public HashMap<String, String> myInfoController(HttpServletRequest request, Model model) {

        //
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        Object o = request.getSession().getAttribute("login_data");//세션을 통한 데이터 유무 판별
        
        if ( o != null && o instanceof UserInfo userInfo ) {
            // TODO 세션에 데이터가 있을경우

            stringStringHashMap.put("result", "success");
            stringStringHashMap.put("data", userInfo.toString());

            try {
                Connection connection = dataBaseServiceManager.getConnection();
                displayResult(connection, "select * from travelrecord where uid = ?"
                        , (resultSet) -> {
                            try{
                                while (resultSet.next()) {
                                    stringStringHashMap.put("user id", resultSet.getString(1));
                                    stringStringHashMap.put("위도", resultSet.getString(2));
                                    stringStringHashMap.put("경도", resultSet.getString(3));
                                    stringStringHashMap.put("주소", resultSet.getString(4));
                                    stringStringHashMap.put("image", resultSet.getString(5));
                                    stringStringHashMap.put("placename", resultSet.getString(6));
                                    stringStringHashMap.put("travel date", resultSet.getString(7));

                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }, userInfo.getId());

            } catch ( SQLException e ) {
                e.printStackTrace();
            }

            try {
                Connection connection = dataBaseServiceManager.getConnection();
                displayResult(connection, "select * from wishlist where uid = ?"
                        , (resultSet) -> {
                            try{

                                while (resultSet.next()) {
                                    stringStringHashMap.put("user id", resultSet.getString(1));
                                    stringStringHashMap.put("위도", resultSet.getString(2));
                                    stringStringHashMap.put("경도", resultSet.getString(3));
                                    stringStringHashMap.put("주소", resultSet.getString(4));
                                    stringStringHashMap.put("image", resultSet.getString(5));
                                    stringStringHashMap.put("placename", resultSet.getString(6));
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }, userInfo.getId());

            } catch ( SQLException e ) {
                e.printStackTrace();
            }

        }
        else {
            // TODO 세션에 데이터가 없을경우
            stringStringHashMap.put("Result", "you`re not in our page");
        }
        
        return stringStringHashMap;
    }

    private static void displayResult(Connection conn, String query, Consumer<ResultSet> resultSetConsumer, String... queryValue) {

        try{
            Connection con = conn;
            PreparedStatement pre = con.prepareStatement(query);
            if (queryValue != null) {
                int count = 1;
                for (String v : queryValue) {
                    pre.setString(count, v);
                    count++;
                }
            }
            ResultSet rs = pre.executeQuery();
            resultSetConsumer.accept(rs);
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.close();
            } catch ( SQLException e1 ) {
                e1.printStackTrace();
            }
        }

    }

    @GetMapping("login/{id}/{password}")
    public HashMap<String, String> loginController(@PathVariable String id, @PathVariable String password, HttpServletRequest request) {

        HashMap<String, String> stringStringHashMap = new HashMap<>();
        
        UserInfo userInfo = dataBaseServiceManager.getUserInfo("uid",id);
        
        if ( userInfo != null ) {
            String resultID = userInfo.getId();
            String resultPassword = userInfo.getPassword();
            if ( resultID.equalsIgnoreCase(id) ) {
                if (resultPassword.equalsIgnoreCase(password)) {
                    // TODO 로그인 성공 데이터
                    request.getSession().setAttribute("login_data", userInfo);
                    stringStringHashMap.put("Result", "login success!");
                    stringStringHashMap.put("data", userInfo.toString());
                    System.out.println(request.getLocalAddr() + " << SERVER INFO [LOGIN]");
                } else {
                    // TODO 로그인 실패
                    stringStringHashMap.put("Result", "invalid password");
                }
            } else {
                //TODO 로그인 실패
                stringStringHashMap.put("Result", "login fail ID not match");
            }
        } else {
            // TODO 데이터 없음
            stringStringHashMap.put("Result", "you`re not in our page!");
        }

        return stringStringHashMap;
    }

    @GetMapping("register/{name}/{id}/{password}/{email}")
    public HashMap<String, String> registerController(@PathVariable String name,
                                                      @PathVariable String id,
                                                      @PathVariable String password,
                                                      @PathVariable String email) throws SQLException {
        HashMap<String, String> stringStringHashMap = new HashMap<>();

        UserInfo nameUserInfo = dataBaseServiceManager.getUserInfo("name", name);
        UserInfo idUserInfo = dataBaseServiceManager.getUserInfo("uid", id);
        UserInfo emailUserInfo = dataBaseServiceManager.getUserInfo("email" , email);

        if ( nameUserInfo != null &&  idUserInfo != null && emailUserInfo != null) {
            stringStringHashMap.put("Result", "you`re already existed.");
            return stringStringHashMap;
        }

        if ( idUserInfo != null ) {
            stringStringHashMap.put("Result", "ID is existed");
            return stringStringHashMap;
        }

        if ( emailUserInfo != null ) {
            stringStringHashMap.put("Result", "email is existed");
            return stringStringHashMap;
        }

        boolean success = dataBaseServiceManager.taskTransaction( connection -> {
            String sql1 = "insert into MyUser(name, uid, password, email) values(?, ?, ?, ?)";
            PreparedStatement p1 = connection.prepareStatement(sql1);
            p1.setString(1, name);
            p1.setString(2, id);
            p1.setString(3, password);
            p1.setString(4, email);
            p1.executeUpdate();

            String sql2 = "insert into wishlist(uid) values(?)";
            PreparedStatement p2 = connection.prepareStatement(sql2);
            p2.setString(1, id);
            p2.executeUpdate();

            String sql3 = "insert into travelrecord(uid) values(?)";
            PreparedStatement p3 = connection.prepareStatement(sql3);
            p3.setString(1, id);
            p3.executeUpdate();

        });

        if ( success ) {
            stringStringHashMap.put("Result", "register success");
        } else {
            stringStringHashMap.put("Result", "SQL Exception");
        }

        return stringStringHashMap;
    }

    @GetMapping("/")
    public String mainPage(Model model) {
        return String.format("Main_Page");
    }

    @GetMapping("/select/place")
    public String selectplacePage(Model model) {
        return String.format("Select Place_Page");
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
