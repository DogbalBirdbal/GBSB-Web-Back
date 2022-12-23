package com.dogbalbirdbal.controller;


import com.dogbalbirdbal.database.vo.UserInfo;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.HashMap;

//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.select.Elements;

@RestController
public class MainController {


    String url = "jdbc:postgresql://127.0.0.1:5432/wheretogo";
    String user = ""; //
    String password1 = ""; //

    @GetMapping("myinfo/{id}/{password}")
    public HashMap<String, String> myInfoController(@PathVariable String id,
                                                    @PathVariable String password,Model model) {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("Result", "fail");

        try{
            Connection connect = null;
            connect = DriverManager.getConnection(url, user, password1);
            String sql = "select name, uid, email\n" +
                    "from MyUser\n" +
                    "where uid = ? and password = ?";
            PreparedStatement p = connect.prepareStatement(sql);
            p.setString(1, id);
            p.setString(2, password);

            ResultSet resultSet = p.executeQuery();
            boolean existData = false;

            while ( resultSet.next() ) {
                stringStringHashMap.put("name", resultSet.getString(1));
                stringStringHashMap.put("id", resultSet.getString(2));
                stringStringHashMap.put("email", resultSet.getString(3));
                existData = true;
            }

            if ( existData ) {
                stringStringHashMap.put("Result", "Success");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return stringStringHashMap;
    }

    @PostMapping("api/login/")
    public String login(@RequestBody UserInfo userInfo)
    {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("Result", "fail");

        try{
            Connection connect = null;
            connect = DriverManager.getConnection(url, user, password1);
            String sql = "select uid, name\n" +
                    "from MyUser\n" +
                    "where uid = ? and password = ?";
            PreparedStatement p = connect.prepareStatement(sql);
            p.setString(1, userInfo.getId());
            p.setString(2, userInfo.getPassword());

            ResultSet resultSet = p.executeQuery();

            boolean existData = false;

            while ( resultSet.next() ) {
                stringStringHashMap.put("id", resultSet.getString(1));
                stringStringHashMap.put("name", resultSet.getString(2));
                existData = true;
            }

            if ( existData ) {
                stringStringHashMap.put("Result", "Success");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "id : " + userInfo.getId() + ", password " + userInfo.getPassword();
    }


    @PostMapping("api/signup/")
    public String signup(@RequestBody UserInfo userInfo){

        System.out.println("test");
        try{
            Connection connect = null;
            connect = DriverManager.getConnection(url, user, password1);
            String sql = "insert into MyUser(uid, name, password, email) values(?, ?, ?, ?)";
            PreparedStatement p = connect.prepareStatement(sql);
            p.setString(2, userInfo.getName());
            p.setString(1, userInfo.getId());
            p.setString(3, userInfo.getPassword());
            p.setString(4, userInfo.getEmail());
            p.executeUpdate();


        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "id : " + userInfo.getId() + ", name : " + userInfo.getName()
                + ", email : " + userInfo.getEmail() + ", password " + userInfo.getPassword();
    }


    @GetMapping("/")
    public String mainPage(Model model) {
        return String.format("Main_Page");
    }

    @GetMapping("/result/")
    public String resultPage(Model model) {
        return String.format("result page");
    }

    //crawling with url using jsoup
//    @GetMapping("crawling/{url}")
//        public String crawlingController(@PathVariable String url, Model model) {
//        String url1 = "https://www.siksinhot.com/search?keywords=" + url;
//        Document doc = null;
//        String result = "";
//        try {
//            doc = Jsoup.connect(url1).get();
//            Elements e1 = doc.select("#main_search > div > article:nth-child(1) > section > div > div > ul > li:nth-child(1) > figcaption > a > h2");
//            Elements e2 = doc.select("#main_search > div > article:nth-child(1) > section > div > div > ul > li:nth-child(2) > figcaption > a > h2");
//            Elements e3 = doc.select("#main_search > div > article:nth-child(1) > section > div > div > ul > li:nth-child(3) > figcaption > a > h2");
//
//            System.out.println("1등 맛집: "+ e1.text());
//            System.out.println("2등 맛집: "+ e2.text());
//            System.out.println("3등 맛집: "+ e3.text());
//            result += "1등 맛집: " +e1.text() + "2등 맛집: " + e2.text() +"3등 맛집: "+ e3.text();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        return String.format(result);
//    }
}
