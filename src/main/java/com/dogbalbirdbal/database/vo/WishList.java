package com.dogbalbirdbal.database.vo;

public class WishList {


    String name ;
    String picURL ;
    String info ;


    public WishList(String name, String picURL, String info) {
        this.name = name;
        this.picURL = picURL;
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicURL() {
        return picURL;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
