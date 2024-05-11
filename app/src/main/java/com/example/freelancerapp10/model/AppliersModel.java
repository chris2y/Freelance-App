package com.example.freelancerapp10.model;

public class AppliersModel {
    private String proposalDescription,bidAmount,deliveryDate,userId,key;
    private long timestamp;

    public AppliersModel() {
    }

    public AppliersModel(String proposalDescription, String bidAmount, String deliveryDate, String userId) {
        this.proposalDescription = proposalDescription;
        this.bidAmount = bidAmount;
        this.deliveryDate = deliveryDate;
        this.userId = userId;
    }

    public String getProposalDescription() {
        return proposalDescription;
    }

    public void setProposalDescription(String proposalDescription) {
        this.proposalDescription = proposalDescription;
    }

    public String getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(String bidAmount) {
        this.bidAmount = bidAmount;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
