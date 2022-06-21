package com.example.trip;

public class Schedule{
    public String area;
    public String sigungu;
    public String tour_spot;
    public String startdate;
    public String enddate;

    public Schedule(String area, String sigungu, String tour_spot, String startdate, String enddate){
        this.area=area;
        this.sigungu=sigungu;
        this.tour_spot=tour_spot;
        this.startdate=startdate;
        this.enddate=enddate;
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
    public String getStartdate(){return startdate;}
    public String getEnddate(){return enddate;}
}