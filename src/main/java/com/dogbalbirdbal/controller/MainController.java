package com.dogbalbirdbal.controller;


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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;

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

    //give location name to input{location}
    @GetMapping("crawlingfood/{location}")
    public String crawlingController(@PathVariable String location, Model model) {

        ArrayList<CrawlingData> foods = new ArrayList<>();
        ArrayList<String> StringName = new ArrayList<>();
        ArrayList<String> PicURL = new ArrayList<>();
        String result;
        String fullurl = "https://www.siksinhot.com/search?keywords=" + location;
        try {
            Document doc = Jsoup.connect(fullurl).get();
            Elements text_contents = doc.select("section ul.localFood_list li a h2 ");
            Elements image_contents = doc.select("ul.localFood_list li a img");

            for(Element f : text_contents)  StringName.add(f.text());
            for(Element p : image_contents) PicURL.add(p.attr("src"));
            for(int a=0; a < 10; a++)
            {
                CrawlingData food = new CrawlingData(StringName.get(a), PicURL.get(a));
                foods.add(food);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        result = foods.toString(); // All data of crwaling
        System.out.println(GetRandomSource(foods)); // 1 data from crawling with Random Function
        return String.format(result);
    }


    public CrawlingData GetRandomSource(ArrayList<CrawlingData> list){
        int a, flag = 0;
        int[] WeightValueArray = {50, 30, 20, 10, 8, 6, 4, 3, 2, 1 };
        for(a=0; a<10; a++){
            WeightValueArray[a] += (Math.random()*10 + 1);
        }
        while(true){
            for(a= 0; a< list.size(); a++) {
                flag += WeightValueArray[a];
                if(flag > 1000)
                {
                    return list.get(a);
                }
            }
        }
    }
}

class CrawlingData {
    private final String name;
    private final String pic_url;

    CrawlingData(String name, String pic_url){
        this.name = name;
        this.pic_url = pic_url;
    }

    public String getName(){
        return this.name;
    }

    public String getPic_url(){
        return this.pic_url;
    }

    public String toString(){
        return "NAME : " + this.name + ", URL : " + this.pic_url;
    }
}


