package com.dogbalbirdbal.controller;


import com.fasterxml.jackson.core.JsonParser;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

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

    @GetMapping("register/{id}/{password}")
    public HashMap<String, String> registerController(@PathVariable String id, @PathVariable String password, Model model) {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("ID", id);
        stringStringHashMap.put("password", password);

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
