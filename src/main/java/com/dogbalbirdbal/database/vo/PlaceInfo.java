package com.dogbalbirdbal.database.vo;


public class PlaceInfo {

    private String id;
    private String route;
    private int num;



    public PlaceInfo(String id, String route, int num) {
        this.id = id;
        this.route = route;
        this.num = num;
    }


    @Override
    public String toString() {
        return "PlaceInfo{" +
                "id='" + id + '\'' +
                ", route='" + route + '\'' +
                ", num='" + num + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoute() {
        return route;
    }
    public void setRoute(String route) {
        this.route = route;
    }


    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
