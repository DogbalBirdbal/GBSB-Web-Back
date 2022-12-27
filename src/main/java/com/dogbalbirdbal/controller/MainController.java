package com.dogbalbirdbal.controller;

import com.dogbalbirdbal.database.vo.UserInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;


@RestController
public class MainController {

    String url = "jdbc:postgresql://localhost:5432/GBSB_JUN";
    String user = "postgres"; //
    String password1 = null; // have to set pwd
    static int count = 0; // variable for choice path

    @PostMapping("api/login/")
    public HashMap<String, String> login(@RequestBody UserInfo userInfo)
    {
        System.out.println(userInfo.toString());
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("Result", "fail");
        boolean existData = false;
        try{
            Connection connect = DriverManager.getConnection(url, user, password1);
            String sql = "select uid, name\n" +
                    "from myuser\n" +                            // table 선택
                    "where uid = ? and password = ?";            // 조건문 uid랑 password 입력받은 값이 일치하는지
            PreparedStatement p = connect.prepareStatement(sql); // 질의문을 작성할 것을 만든다.2
            p.setString(1, userInfo.getId());       // 이게 첫번째 물음표로 이동한다.
            p.setString(2, userInfo.getPassword()); // 이게 두번째 물음표로 이동한다.

            ResultSet resultSet = p.executeQuery();
                while (resultSet.next()) {
                    existData = true;
                }
                if (existData) {
                    stringStringHashMap.put("Result", "success");
                }

            if ( existData ) {
                stringStringHashMap.put("Result", "Success");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        //return "id : " + userInfo.getId() + ", password " + userInfo.getPassword();
        return stringStringHashMap;
    }

    @PostMapping("api/signup/")
    public String signup(@RequestBody UserInfo userInfo){

        try{
            Connection connect = DriverManager.getConnection(url, user, password1);
            String sql = "insert into MyUser(uid, name, password, email) values(?, ?, ?, ?)";
            PreparedStatement p = connect.prepareStatement(sql);
            p.setString(1, userInfo.getId());
            p.setString(2, userInfo.getName());
            p.setString(3, userInfo.getPassword());
            p.setString(4, userInfo.getEmail());
            p.executeUpdate();

            System.out.println("ID: " + userInfo.getId() + " NAME: " + userInfo.getName() + "PW: " + userInfo.getPassword() + "EMAIL: " + userInfo.getEmail());
            return "id : " + userInfo.getId() + ", name : " + userInfo.getName() + ", email : " + userInfo.getEmail() + ", password " + userInfo.getPassword();

        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("ID 중복");
        }
        return null; // 이 return value를 front에서 받았을 때, 다른 메세지를 출력할 수 있도록 진행해야함. ex) 이미 사용중인 아이디입니다.
    }


    @GetMapping("/api/crawlingfood/{location}")
    public String crawlingController(@PathVariable("location") String location) {
        ArrayList<DataSet> foods = new ArrayList<>();
        ArrayList<String> StringName = new ArrayList<>();
        ArrayList<String> PicURL = new ArrayList<>();
        String fullURL = "https://www.mangoplate.com/search/" + location;
        try {
            Document doc = Jsoup.connect(fullURL).get();
            Elements contents = doc.select("div[class=thumb] img");

            for(Element t : contents){
                String[] temp = t.attr("alt").split(" ");
                StringName.add(temp[0]);

                String temp2 = t.attr("data-original");
                int parsingindex = temp2.indexOf("?");
                PicURL.add(temp2.substring(0, parsingindex));
            }

            for (int a = 0; a < 14; a++) {
                DataSet food = new DataSet(StringName.get(a), PicURL.get(a));
                foods.add(food);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(foods.toString());
        //System.out.println(foods.get(count++ % foods.size()));
        String result = "";
        for(int a=0; a<3; a++){
            int random = (int) (Math.random() * 14);
            if(result.contains(foods.get(random).toString())) a--;
            else result += foods.get(random).toString() + " ";
        }
        return result;
    }

    @GetMapping("/api/crawlinghotel/{data}")
    public String crawlingController2(@PathVariable("data") String data) {
        //장소_2022-12-09_2022-12-10 방식으로 data 작성
       
        ArrayList<DataSet> Hotels = new ArrayList<>();
        ArrayList<String> StringName = new ArrayList<>();
        ArrayList<String> PicURL = new ArrayList<>();
        ArrayList<String> latitudes = new ArrayList<>();
        ArrayList<String> longitudes = new ArrayList<>();
        String[] urlSplit = data.split("_");

        String fullURL = "https://www.goodchoice.kr/product/result?sel_date=" + urlSplit[1] + "&sel_date2=" + urlSplit[2] + "&keyword=" + urlSplit[0];
        try {
            Document doc = Jsoup.connect(fullURL).timeout(0).get();
            Elements text_contents = doc.select("div.name strong");
            Elements image_contents = doc.select("p[class=pic] img");
            Elements location_contents = doc.select("div[id=poduct_list_area] ul a");

            for (Element t : text_contents) {
                String temp = t.text();
                if(temp.contains("특급")) temp = temp.replace("특급", "");
                if(temp.contains("가족호텔")) temp = temp.replace("가족호텔", "");
                if(temp.contains("비지니스")) temp = temp.replace("비지니스","");
                if (temp.contains("★당일특가★")) temp = temp.replace("★당일특가★", "");
                if (temp.contains("[반짝특가]")) temp = temp.replace("[반짝특가]", "");
                if(temp.contains("[특가]")) temp = temp.replace("[특가]", "");

                StringName.add(temp);
            }

            for (Element i : image_contents) {
                PicURL.add("https:" + i.attr("data-original"));
            }

            for (Element k : location_contents) {
                latitudes.add(k.attr("data-alat"));
                longitudes.add(k.attr("data-alng"));
            }

            for (int a = 0; a < 10; a++) {
                DataSet hotel = new HotelSet(StringName.get(a), PicURL.get(a), latitudes.get(a), longitudes.get(a));
                Hotels.add(hotel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String result = Hotels.get(count++ % Hotels.size()).toString();
        //System.out.println(result);
        return result;
    }

    @GetMapping("api/choicepath/{destination}")
    public String choicepathController(@PathVariable("destination") String destination) {
        // 입력 예시는 "부산 힐링", "부산 식도락", "부산 오락".
        String result = "";

        if(destination == null)  return "empty input";
        String[] urlSplit = destination.split(" ");
        if(urlSplit.length != 2) return "error";

        ArrayList<DataSet>[][] FoodLocation = new ArrayList[3][];
        //  FoodLocation[n][0] = 힐링, FoodLocation[n][1] = 식도락, FoodLocation[n][2] = 예술
        FoodLocation[0] = new ArrayList[3]; // 부산
        FoodLocation[0][0] = new ArrayList<>(); // 부산 힐링
        FoodLocation[0][0].add(new DataSet("감천문화마을", "https://a.cdn-hotels.com/gdcs/production132/d545/0870f01b-96ec-4854-98b6-72dfc747fa92.jpg?impolicy=fcrop&w=1600&h=1066&q=medium"));
        FoodLocation[0][0].add(new DataSet("씨라이프부산아쿠아리움", "https://www.visitsealife.com/busan/media/pfml3jrp/seaatnight.jpg"));
        FoodLocation[0][0].add(new DataSet("송도해상케이블카", "https://blog.kakaocdn.net/dn/bdlVIx/btq49awBY6d/nakSwgXXAIzXLhUnNHTL2k/img.jpg"));

        //FoodLocation[0][0] = new ArrayList<>(Arrays.asList("감천문화마을", "씨라이프부산아쿠아리움", "송도해상케이블카", "동백섬", "범어사", "이기대수변공원"));
        //FoodLocation[0][1] = new ArrayList<>(Arrays.asList("자갈치시장", "부전시장", "부평깡통시장", "부산밀락회센터", "부산구포시장", "부사영도포장마차거리"));
        //FoodLocation[0][2] = new ArrayList<>(Arrays.asList("부산뮤지엄원", "부산영화체험박물관", "부산커피박물관", "광복로문화패션거리", "트릭아이뮤지엄부산", "부산영화의전당"));

        if(urlSplit[0].equals("부산")){
            switch (urlSplit[1]) {
                case "힐링":
                    result += "[";
                    result += FoodLocation[0][0].get(count++ % 6).toString() +",";
                    result += FoodLocation[0][0].get(count++ % 6).toString() +",";
                    result += FoodLocation[0][0].get(count++ % 6).toString() + "]";
                    break;
                case "식도락":
                    result = FoodLocation[0][1].get(count++ % 6).toString();
                    break;
                case "예술":
                    result = FoodLocation[0][2].get(count++ % 6).toString();
                    break;
            }
        } else if(urlSplit[0].equals("서울")){
            switch (urlSplit[1]) {
                case "힐링":
                    break;
                case "식도락":
                    break;
                case "예술":
                    break;
            }
        }
        return result;
    }

}

class DataSet {
    private final String name;
    private final String pic_url;

    DataSet(String name, String pic_url) {
        this.name = name;
        this.pic_url = pic_url;
    }
    public String toString() {
        return "{\"name\":\"" + name + "\", \"pic_url\":\"" + pic_url + "\"}";
    }
}

class HotelSet extends DataSet {
    private final String latitudes;
    private final String longitudes;

    HotelSet(String name, String pic_url, String latitudes, String longitudes) {
        super(name, pic_url);
        this.latitudes = latitudes;
        this.longitudes = longitudes;
    }
    public String toString() {
        return super.toString(); // + "\"latitudes\":\"" + latitudes + "\", \"longitudes\":\"" + longitudes + "\"}";
    }
}
