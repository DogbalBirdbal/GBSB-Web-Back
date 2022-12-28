package com.dogbalbirdbal.coreserver;

import com.dogbalbirdbal.database.manager.DataBaseServiceManager;
import com.dogbalbirdbal.database.vo.WishBox;
import com.dogbalbirdbal.database.vo.WishContainer;
import com.dogbalbirdbal.database.vo.WishList;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.sql.ResultSet;

@SpringBootApplication
@ComponentScan ( basePackages = {"com.dogbalbirdbal.controller"})
public class CoreServerApplication {

    public static void main(String[] args) {
        DataBaseServiceManager.getInstance().loadDataSource("", "",
                "127.0.0.1", 5432, "");
        SpringApplication.run(CoreServerApplication.class, args);
        System.out.println("테스트!");
        System.out.println("------------------------------------------------------");

        System.out.println("json test");
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

                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            });
        }



}
