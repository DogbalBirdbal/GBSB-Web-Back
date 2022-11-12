package com.dogbalbirdbal.controller;


import com.fasterxml.jackson.core.JsonParser;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

@RestController
public class MainController {


    @GetMapping("myinfo/{id}")
    public HashMap<String, String> myInfoController(@PathVariable String id, Model model) {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("ID", id);
        return stringStringHashMap;
    }

    @GetMapping("login/{id}")
    public HashMap<String, String> loginController(@PathVariable String id, Model model) {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("ID", id);
        return stringStringHashMap;
    }

    @GetMapping("register/{name}/{id}/{password}/{email}")
    public HashMap<String, String> registerController(@PathVariable String name,
                                                      @PathVariable String id,
                                                      @PathVariable String password,
                                                      @PathVariable String email,
                                                      Model model) {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        String url = "jdbc:postgresql://127.0.0.1:5432/wheretogo";
        String user = "2";
        String password1 = "1!"; //password 입력
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

        //TODO DB 연동해서
        //TODO  회원가입이 성공하면 어떤 페이지를 보여줌
        //TODO 실패하면 실패하는 페이지를 보여줌 ( 실패 이유와 함께 )

        return stringStringHashMap;
    }

    @GetMapping("/")
    public String mainPage(Model model) {
        return String.format("Main_Page");
    }


}
