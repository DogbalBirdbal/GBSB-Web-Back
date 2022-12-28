package com.dogbalbirdbal.controller;

import com.dogbalbirdbal.database.manager.DataBaseServiceManager;
import com.dogbalbirdbal.database.vo.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.json.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;


@RestController
public class MainController {

    static int count = 0; // variable for choice path

    @PostMapping("api/login/")
    public HashMap<String, String> logins(@RequestBody UserInfo userInfo) {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("Result", "fail");
        boolean existData = false;
        try{
            Connection connect = DataBaseServiceManager.getInstance().getConnection();
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
    public String signups(@RequestBody UserInfo userInfo) {

        try{
            Connection connect = DataBaseServiceManager.getInstance().getConnection();
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
        return "Duplicate"; // 이 return value를 front에서 받았을 때, 다른 메세지를 출력할 수 있도록 진행해야함. ex) 이미 사용중인 아이디입니다.
    }

    @GetMapping("/api/crawlingfood/{location}")
    public String crawlingController(@PathVariable("location") String location) {
        ArrayList<DataSet_URL> foods = new ArrayList<>();
        ArrayList<String> StringName = new ArrayList<>();
        ArrayList<String> PicURL = new ArrayList<>();
        ArrayList<String> Info = new ArrayList<>();
        String fullURL = "https://www.mangoplate.com/search/" + location;
        try {
            Document doc = Jsoup.connect(fullURL).get();
            Elements contents = doc.select("div[class=thumb] img");
            Elements infos = doc.select("a[class=only-desktop_not]");

            for(Element t : contents){
                String[] temp = t.attr("alt").split(" ");
                StringName.add(temp[0]);

                String temp2 = t.attr("data-original");
                int parsingindex = temp2.indexOf("?");
                PicURL.add(temp2.substring(0, parsingindex));
            }

            for(Element k : infos){
                String temp = k.attr("href");
                System.out.println(temp);
                Info.add("https://www.mangoplate.com/"+ temp);
            }

            for (int a = 0; a < 14; a++) {
                if(!(PicURL.get(a).equals("/"))) {
                    DataSet_URL food = new DataSet_URL(StringName.get(a), PicURL.get(a),Info.get(a));
                    foods.add(food);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println(foods.toString());
        //System.out.println(foods.get(count++ % foods.size()));
        String result = "[";
        for(int a=0; a<3; a++){
            int random = (int) (Math.random() * foods.size());
            if(result.contains(foods.get(random).toString())) a--;
            else {
                result += foods.get(random).toString();
                if(a<2) result += ",";
            }
        }
        result += "]";
        return result;
    }

    @GetMapping("/api/crawlinghotel/{data}")
    public String crawlingController2(@PathVariable("data") String data) {
        //장소_2022-12-09_2022-12-10 방식으로 data 작성

        ArrayList<DataSet_URL> Hotels = new ArrayList<>();
        ArrayList<String> StringName = new ArrayList<>();
        ArrayList<String> PicURL = new ArrayList<>();
        ArrayList<String> Info = new ArrayList<>();
        String[] urlSplit = data.split("_");

        String fullURL = "https://www.goodchoice.kr/product/result?sel_date=" + urlSplit[1] + "&sel_date2=" + urlSplit[2] + "&keyword=" + urlSplit[0];
        try {
            Document doc = Jsoup.connect(fullURL).timeout(0).get();
            Elements text_contents = doc.select("div.name strong");
            Elements image_contents = doc.select("p[class=pic] img");
            Elements info_contents = doc.select("div[id=poduct_list_area] ul a");

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

            for (Element k : info_contents) {
                Info.add(k.attr("href"));
            }

            for (int a = 0; a < 10; a++) {
                DataSet_URL hotel = new DataSet_URL(StringName.get(a), PicURL.get(a), Info.get(a));
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

        ArrayList<DataSet_URL>[][] FoodLocation = new ArrayList[3][];
        //  FoodLocation[n][0] = 힐링, FoodLocation[n][1] = 식도락, FoodLocation[n][2] = 예술
        FoodLocation[0] = new ArrayList[6]; // 부산
        FoodLocation[0][0] = new ArrayList<>(); // 부산 힐링
        FoodLocation[0][0].add(new DataSet_URL("감천문화마을", "https://a.cdn-hotels.com/gdcs/production132/d545/0870f01b-96ec-4854-98b6-72dfc747fa92.jpg?impolicy=fcrop&w=1600&h=1066&q=medium", "https://map.naver.com/v5/entry/place/21884707?c=14360910.6217881,4177294.2364293,15,0,0,0,dh"));
        FoodLocation[0][0].add(new DataSet_URL("동백섬", "https://media.triple.guide/triple-cms/c_limit,f_auto,h_1024,w_1024/7d64cf8e-4adc-4b6c-b53d-e01ea70d55b9.jpeg","https://map.naver.com/v5/search/%EB%8F%99%EB%B0%B1%EC%84%AC?c=14376689.6809337,4184817.9801856,15,0,0,0,dh"));
        FoodLocation[0][0].add(new DataSet_URL("범어사", "https://www.visitbusan.net/uploadImgs/files/cntnts/20191230190106794_oen","https://map.naver.com/v5/search/%EB%B2%94%EC%96%B4%EC%82%AC?c=14367824.9804448,4202515.9354916,18.01,0,0,0,dh"));
        FoodLocation[0][0].add(new DataSet_URL("태종대", "https://www.visitbusan.net/uploadImgs/files/cntnts/20221215173025725_oen", "https://map.naver.com/v5/search/%ED%83%9C%EC%A2%85%EB%8C%80?c=14369925.0845300,4171371.8330982,15.72,0,0,0,dh"));
        FoodLocation[0][0].add(new DataSet_URL("송정해수욕장", "https://a.cdn-hotels.com/gdcs/production81/d1958/660649a2-7183-4376-ba6d-fa9c72847c83.jpg","https://map.naver.com/v5/search/%EC%86%A1%EC%A0%95%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5?c=14382300.7414847,4188100.6353477,15.91,0,0,0,dh"));
        FoodLocation[0][0].add(new DataSet_URL("해동용궁사", "https://a.cdn-hotels.com/gdcs/production194/d969/ce3eef2e-2e5d-44c3-b0ee-99c3d3bb991e.jpg?impolicy=fcrop&w=800&h=533&q=medium", "https://map.naver.com/v5/search/%ED%95%B4%EB%8F%99%EC%9A%A9%EA%B6%81%EC%82%AC?c=14385022.9645746,4189538.2833944,17.88,0,0,0,dh"));
        FoodLocation[1] = new ArrayList[3];
        FoodLocation[1][2] = new ArrayList<>();
//        FoodLocation[1][2].add(new DataSet_URL("제주동문시장", "https://t1.daumcdn.net/cfile/tistory/99491D335E4F77741C"));
//        FoodLocation[1][2].add(new DataSet_URL("수목원길야시장", "https://www.jejutwn.com/data/photos/20220623/art_16545968632197_5fd5d7.jpg"));
//        FoodLocation[1][2].add(new DataSet_URL("", ""));

        //FoodLocation[0][0] = new ArrayList<>(Arrays.asList("감천문화마을", "씨라이프부산아쿠아리움", "송도해상케이블카", "동백섬", "범어사", "이기대수변공원"));
        //FoodLocation[0][1] = new ArrayList<>(Arrays.asList("자갈치시장", "부전시장", "부평깡통시장", "부산밀락회센터", "부산구포시장", "부사영도포장마차거리"));
        //FoodLocation[0][2] = new ArrayList<>(Arrays.asList("부산뮤지엄원", "부산영화체험박물관", "부산커피박물관", "광복로문화패션거리", "트릭아이뮤지엄부산", "부산영화의전당"));

        if(urlSplit[0].equals("부산")){
            switch (urlSplit[1]) {
                case "힐링":
                    result += "[";
                    result += FoodLocation[0][0].get(count++ %6).toString() +",";
                    result += FoodLocation[0][0].get(count++ %6).toString() +",";
                    result += FoodLocation[0][0].get(count++ %6).toString() + "]";
                    break;
                case "식도락":
                    result = FoodLocation[0][1].get(count % 6).toString();
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

    @PostMapping("api/myinfo/wishlist/")
    public String routesender(@RequestBody PlaceInfo placeInfo){

        System.out.println("test123");
        System.out.println(placeInfo.toString());

        try{
            Connection connect = null;
            connect = DataBaseServiceManager.getInstance().getConnection();
            String sql = "insert into wishlist(uid, route) values(?, ?)";
            PreparedStatement p = connect.prepareStatement(sql);
            p.setString(1, placeInfo.getId());
            p.setString(2, placeInfo.getRoute());
            p.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "id : " + placeInfo.getId() + ", route " + placeInfo.getRoute();
    }


    @GetMapping("api/myinfo/{id}")
    public HashMap<String, String> myInfoControllers(@PathVariable String id) {
        LinkedHashMap<String, String> stringStringLinkedHashMap = new LinkedHashMap<>();
        System.out.println("test111");
        try{
            Connection connect = null;
            connect = DataBaseServiceManager.getInstance().getConnection();
            String sql1 = "select uid, name, email\n" +
                    "from MyUser\n" +
                    "where uid = ? ";
            PreparedStatement p1 = connect.prepareStatement(sql1);
            p1.setString(1, id);
            ResultSet resultSet1 = p1.executeQuery();
            while ( resultSet1.next() ) {
                System.out.println(resultSet1.getString(1));
                stringStringLinkedHashMap.put("id", resultSet1.getString(1));
                stringStringLinkedHashMap.put("name", resultSet1.getString(2));
                stringStringLinkedHashMap.put("email", resultSet1.getString(3));
            }

            String sql2 = "select string_to_array(route, ',') from wishlist";

            System.out.println(sql2);
            PreparedStatement p2 = connect.prepareStatement(sql2);
            ResultSet resultSet2 = p2.executeQuery();
            while ( resultSet2.next() ) {
                stringStringLinkedHashMap.put("route", resultSet2.getString(1));
            }

            DataBaseServiceManager.getInstance().taskTransaction(connection -> {
                ResultSet resultSet = connection.prepareStatement("select route from wishlist").executeQuery();

                WishContainer wishContainer = new WishContainer();

                while ( resultSet.next() ) {

                    WishBox wishBox = new WishBox();

                    String route = resultSet.getString(1);

                    org.json.simple.parser.JSONParser jsonParser = new org.json.simple.parser.JSONParser();

                    try {

                        JSONArray jsonArray = (JSONArray) jsonParser.parse(route);

                        jsonArray.forEach(o -> {
                            JSONObject jsonObject = (JSONObject) o;

                            String name = jsonObject.get("name").toString();
                            String picURL = jsonObject.get("pic_url").toString();
                            String info = jsonObject.get("info").toString();
                            wishBox.addWishList(new WishList(name,picURL,info));

                        });

                    } catch ( Exception e ) {
                        e.printStackTrace();
                    }
                    wishContainer.addWishBox(wishBox);
                }

                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonData = objectMapper.writeValueAsString(wishContainer);

                    System.out.printf("[JSON MAPPER] : %s\n", jsonData);

                    stringStringLinkedHashMap.put("result", jsonData);

                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            });


        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return stringStringLinkedHashMap;
    }




    class DataSet {
        final String name;
        final String pic_url;

        DataSet(String name, String pic_url) {
            this.name = name;
            this.pic_url = pic_url;
        }
        public String toString() {
            return "{\"name\":\"" + name + "\", \"pic_url\":\"" + pic_url + "\"}";
        }
    }

    class DataSet_URL extends DataSet {
        private final String info;

        DataSet_URL(String name, String pic_url, String info) {
            super(name, pic_url);
            this.info = info;
        }

        public String toString() {
            return "{\"name\":\"" + super.name + "\", \"pic_url\":\"" + super.pic_url + "\", \"info\":\"" + this.info + "\"}";
        }

    }


}