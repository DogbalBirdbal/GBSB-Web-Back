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

 @GetMapping("crawlingfood/{location}")
    public String crawlingController(@PathVariable String location, Model model) {
        ArrayList<CrawlingData> foods = new ArrayList<>();
        ArrayList<String> StringName = new ArrayList<>();
        ArrayList<String> PicURL = new ArrayList<>();
        String result;
        String fullURL = "https://www.siksinhot.com/search?keywords=" + location;
            try {
                Document doc = Jsoup.connect(fullURL).get();
                Elements text_contents = doc.select("section ul.localFood_list li a h2 ");
                Elements image_contents = doc.select("ul.localFood_list li a img");
                for(Element f : text_contents){
                    StringName.add(f.text());
               }
                for(Element p : image_contents){
                    PicURL.add(p.attr("src"));
                }
                for(int a=0; a < 10; a++)
                {
                    CrawlingData food = new CrawlingData(StringName.get(a), PicURL.get(a));
                    foods.add(food);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        result = foods.toString();
        System.out.println(GetRandomSource(foods));
        return String.format(result);
    }

    @GetMapping("crawlinghotel/{data}")
    public String crawlingController2(@PathVariable String data, Model model) {
        //장소_2022-12-09_2022-12-10 방식으로 data 작성
        ArrayList<CrawlingData> Hotels = new ArrayList<>();
        ArrayList<String> StringName = new ArrayList<>();
        ArrayList<String> PicURL = new ArrayList<>();
        String result;
        String[] urlSplit = data.split("_");

        String fullURL = "https://www.goodchoice.kr/product/result?sel_date=" + urlSplit[1] +"&sel_date2=" + urlSplit[2] +"&keyword=" + urlSplit[0];
        try {
            Document doc = Jsoup.connect(fullURL).timeout(0).get();
            Elements text_contents = doc.select("div.name strong");
            Elements image_contents = doc.select("p[class=pic] img");

            for(Element t : text_contents){
                StringName.add(t.text());
            }

            for(Element i : image_contents){
                PicURL.add("https:" + i.attr("data-original"));
            }

            for(int a=0; a<10; a++) {
                CrawlingData hotel = new CrawlingData(StringName.get(a), PicURL.get(a));
                Hotels.add(hotel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        result = Hotels.toString();
        System.out.println(GetRandomSource(Hotels));
        return result;
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