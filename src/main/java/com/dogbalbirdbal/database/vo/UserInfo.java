package com.dogbalbirdbal.database.vo;

import jdk.jfr.DataAmount;
import lombok.Data;

import java.util.Objects;



public class UserInfo {

    private String id;
    private String password;
    private String email;
    private String name;


    public UserInfo(String id, String password, String email, String name) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.name = name;
    }


    @Override
    public String toString() {
        return "UserInfo{" +
                "id='" + id + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserInfo userInfo = (UserInfo) o;
        return Objects.equals(id, userInfo.id) && Objects.equals(password, userInfo.password) && Objects.equals(email, userInfo.email) && Objects.equals(name, userInfo.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, password, email, name);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public  String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public  String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public  String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
