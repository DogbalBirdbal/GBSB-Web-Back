package com.dogbalbirdbal.coreserver;

import com.dogbalbirdbal.database.manager.DataBaseServiceManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan ( basePackages = {"com.dogbalbirdbal.controller"})
public class CoreServerApplication {

//    String url = "jdbc:postgresql://127.0.0.1:5432/wheretogo";
//    String user = "account"; //
//    String password1 = "password"; //

    public static void main(String[] args) {
        DataBaseServiceManager.getInstance().loadDataSource("kimjuyoung", "rhdwn1004!", "127.0.0.1", 5432, "wheretogo");
        // Database 먼저 로딩
        SpringApplication.run(CoreServerApplication.class, args);
    }

}
