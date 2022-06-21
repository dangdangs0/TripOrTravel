package com.example.trip;

import android.graphics.Bitmap;

public class Tourist{
    public String area;
    public String sigungu;
    public String tour_spot;
    public String area_pic;
    public int likes;
    public int likeb;


    public Tourist(String area, String sigungu, String tour_spot,int likes,String area_pic,int likeb){
        this.area=area;
        this.sigungu=sigungu;
        this.tour_spot=tour_spot;
        this.area_pic=area_pic;
        this.likes=likes;
        this.likeb=likeb;
    }

    public String getArea(){
        return area;
    }
    public String getSigungu(){
        return sigungu;
    }
    public String getTour_spot(){
        return tour_spot;
    }

    public String getArea_pic() {
        return area_pic;
    }

    public int getLikes() {
        return likes;
    }

    public int getLikeb(){return likeb;}
}