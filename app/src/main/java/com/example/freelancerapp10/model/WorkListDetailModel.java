package com.example.freelancerapp10.model;

public class WorkListDetailModel {

    Double seenCount;
    String userId;
    String description;
    String timeline;

    public WorkListDetailModel() {
    }

    public WorkListDetailModel(Double seenCount, String userId, String description, String timeline) {
        this.seenCount = seenCount;
        this.userId = userId;
        this.description = description;
        this.timeline = timeline;
    }

    public Double getSeenCount() {
        return seenCount;
    }

    public void setSeenCount(Double seenCount) {
        this.seenCount = seenCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String timeline) {
        this.timeline = timeline;
    }
}
