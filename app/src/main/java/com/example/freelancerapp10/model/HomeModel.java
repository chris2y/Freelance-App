package com.example.freelancerapp10.model;

import com.google.firebase.Timestamp;
import com.google.firebase.database.ServerValue;

public class HomeModel {

    private String title,description,userId,key;
    private long Timestamp;
    private String price;

    public HomeModel() {
    }

    public HomeModel(String title, String description,String price) {
        this.title = title;
        this.description = description;
        this.price = price;

    }


    public long getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(long Timestamp) {
        this.Timestamp = Timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String itemId) {
        this.userId = itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
