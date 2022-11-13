package com.dogbalbirdbal.controller;


import com.fasterxml.jackson.core.JsonParser;
import org.jsoup.nodes.Element;
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

@RestController
public class MainController {


    String url = "jdbc:postgresql://127.0.0.1:5432/wheretogo";
    String user = "account"; //
    String password1 = "password"; //

    @GetMapping("myinfo/{id}/{password}")
    public HashMap<String, String> myInfoController(@PathVariable String id,
                                                    @PathVariable String password,Model model) {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("Result", "fail");

        try{
            Connection connect = null;
            connect = DriverManager.getConnection(url, user, password1);
            String sql = "select name, id, email\n" +
                    "from MyUser\n" +
                    "where id = ? and password = ?";
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

    @GetMapping("login/{id}/{password}")
    public HashMap<String, String> loginController(@PathVariable String id,
                                                   @PathVariable String password, Model model) {

        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("Result", "fail");

        try{
            Connection connect = null;
            connect = DriverManager.getConnection(url, user, password1);
            String sql = "select name, id\n" +
                    "from MyUser\n" +
                    "where id = ? and password = ?";
            PreparedStatement p = connect.prepareStatement(sql);
            p.setString(1, id);
            p.setString(2, password);

            ResultSet resultSet = p.executeQuery();

            boolean existData = false;

            while ( resultSet.next() ) {
                stringStringHashMap.put("name", resultSet.getString(1));
                stringStringHashMap.put("id", resultSet.getString(2));
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

    @GetMapping("register/{name}/{id}/{password}/{email}")
    public HashMap<String, String> registerController(@PathVariable String name,
                                                      @PathVariable String id,
                                                      @PathVariable String password,
                                                      @PathVariable String email,
                                                      Model model) {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        try{
            Connection connect = null;
            connect = DriverManager.getConnection(url, user, password1);
            String sql = "insert into MyUser(name, id, password, email) values(?, ?, ?, ?)";
            PreparedStatement p = connect.prepareStatement(sql);
            p.setString(1, name);
            p.setString(2, id);
            p.setString(3, password);
            p.setString(4, email);
            p.executeUpdate();

            stringStringHashMap.put("Result", "Success");

        } catch (SQLException ex) {
            ex.printStackTrace();
            stringStringHashMap.put("Result", "fail");
        }

        stringStringHashMap.put("name", name);
        stringStringHashMap.put("ID", id);
        stringStringHashMap.put("password", password);
        stringStringHashMap.put("email", email);

        return stringStringHashMap;
    }

    @GetMapping("/")
    public String mainPage(Model model) {
        return String.format("Main_Page");
    }

    //crawling with url using jsoup
    @GetMapping("crawling/{url}")
    public String crawlingController(@PathVariable String url, Model model) {
        String url1 = "https://finance.naver.com/item/main.naver?code=" + url;
        Document doc = null;
        try {
            doc = Jsoup.connect(url1).get();
            Elements e1 = doc.select("#middle > div.h_company > div.wrap_company > div > span.code");
            Elements e2 = doc.select("#chart_area > div.rate_info > div > p.no_today > em");
            String str = e2.text().split(" ")[0];
            System.out.print("code: "+ e1.text());
            System.out.println(", gageuk:" +  str);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return String.format("Crawling_Page");
    }
}
