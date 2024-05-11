package com.example.freelancerapp10.model;

import com.google.firebase.database.ServerValue;

public class PostModel {

    private String title;
    private String skill;
    private String description;
    private String price;
    private String timeline;
    private String userId;
    private Object timestamp;
    private String itemId;
    private String priceType;

    public PostModel() {
        // Default constructor required for Firebase
    }

    public PostModel(String title, String skill, String description, String price, String priceType,
                     String timeline, String userId, String itemId) {
        this.title = title;
        this.skill = skill;
        this.description = description;
        this.price = price;
        this.timeline = timeline;
        this.userId = userId;
        this.timestamp = ServerValue.TIMESTAMP;
        this.itemId = itemId;
        this.priceType = priceType;

    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
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

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }
}
