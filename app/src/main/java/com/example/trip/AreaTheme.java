package com.example.trip;

public class AreaTheme {
    private String themeName;
    private int resId;//테마사진

    public AreaTheme(String themeName,int resId){
        this.themeName=themeName;
        this.resId=resId;
    }


    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
