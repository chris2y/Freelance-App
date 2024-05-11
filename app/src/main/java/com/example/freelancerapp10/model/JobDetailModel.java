package com.example.freelancerapp10.model;
public class JobDetailModel extends HomeModel {
    private int seenCount;
    private String userId;
    private String itemId;

    private String timeline;

    public JobDetailModel() {
    }

    public JobDetailModel(String title, String description, String price, int seenCount,String userId,String timeleine) {
        super(title, description, price);
        this.seenCount = seenCount;
        this.userId = userId;
        this.timeline =timeleine;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getSeenCount() {
        return seenCount;
    }

    public void setSeenCount(int seenCount) {
        this.seenCount = seenCount;
    }
}