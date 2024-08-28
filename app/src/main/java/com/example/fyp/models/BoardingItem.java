package com.example.fyp.models;

public class BoardingItem {

    String title, description;
    int screenImg;

    public BoardingItem(String title, String description, int screenImg) {
        this.title = title;
        this.description = description;
        this.screenImg = screenImg;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setScreenImg(int screenImg) {
        this.screenImg = screenImg;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getScreenImg() {
        return screenImg;
    }
}
