package com.dogbalbirdbal.controller;


import com.dogbalbirdbal.database.vo.PlaceInfo;
import com.dogbalbirdbal.database.vo.UserInfo;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedHashMap;

//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.select.Elements;

@RestController
public class MainController {


    String url = "jdbc:postgresql://127.0.0.1:5432/GBSB-back";
    String user = "kimjuyoung"; //
    String password1 = "rhdwn1004!"; //

    @GetMapping("api/myinfo/{id}/")
    public HashMap<String, String> myInfoController(@PathVariable String id) {
        LinkedHashMap<String, String> stringStringLinkedHashMap = new LinkedHashMap<>();
        int count = 1;

        try{
            Connection connect = null;
            connect = DriverManager.getConnection(url, user, password1);
            String sql1 = "select uid, name, email\n" +
                    "from MyUser\n" +
                    "where uid = ? ";
            PreparedStatement p1 = connect.prepareStatement(sql1);
            p1.setString(1, id);
            ResultSet resultSet1 = p1.executeQuery();
            while ( resultSet1.next() ) {
                stringStringLinkedHashMap.put("id", resultSet1.getString(1));
                stringStringLinkedHashMap.put("name", resultSet1.getString(2));
                stringStringLinkedHashMap.put("email", resultSet1.getString(3));
            }

            String sql2 = "select latitude, longitude, address, image, placename\n" +
                    "from travelrecord\n" +
                    "where uid = ? " +
                    "order by id";
            PreparedStatement p2 = connect.prepareStatement(sql2);
            p2.setString(1, id);
            ResultSet resultSet2 = p2.executeQuery();
            while ( resultSet2.next() ) {
                stringStringLinkedHashMap.put("LATITUDE" + count, resultSet2.getString(1));
                stringStringLinkedHashMap.put("LONGITUDE" + count, resultSet2.getString(2));
                stringStringLinkedHashMap.put("ADDRESS" + count, resultSet2.getString(3));
                stringStringLinkedHashMap.put("IMAGE" + count, resultSet2.getString(4));
                stringStringLinkedHashMap.put("PLACENAME" + count, resultSet2.getString(5));
                count++;

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return stringStringLinkedHashMap;
    }

    @PostMapping("api/routesender/")
    public String routesender(@RequestBody PlaceInfo placeInfo){

        System.out.println("test");
        try{
            Connection connect = null;
            connect = DriverManager.getConnection(url, user, password1);
            String sql = "insert into travelrecord(uid, latitude, longitude, address," +
                    "image, placename) values(?, ?, ?, ?, ?, ?)";
            PreparedStatement p = connect.prepareStatement(sql);
            p.setString(1, placeInfo.getId());
            p.setString(2, placeInfo.getLatitude());
            p.setString(3, placeInfo.getLongitude());
            p.setString(4, placeInfo.getAddress());
            p.setString(5, placeInfo.getImage());
            p.setString(6, placeInfo.getPlacename());
            p.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "id : " + placeInfo.getId() + ", latitude : " + placeInfo.getLatitude()
                + ", longitude : " + placeInfo.getLongitude() + ", address " + placeInfo.getAddress()
                + ", image " + placeInfo.getImage() + ", placename " + placeInfo.getPlacename();
    }


    @PostMapping("api/login/")
    public HashMap<String, String> login(@RequestBody UserInfo userInfo)
    {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("Result", "fail");
        int count = 1;
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
                count++;
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


    //이메일 인증
//    @GetMapping("/mailCheck")
//    @ResponseBody
//    public String mailCheck(String email) {
//        System.out.println("이메일 인증 요청이 들어옴!");
//        System.out.println("이메일 인증 이메일 : " + email);
//        return email;
//    }


}
