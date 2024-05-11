package com.example.freelancerapp10.model;

public class HirerModel {

    String userId;
    String dataProfileImage;
    String fullName;
    Long createdTimestamp;
    long moneySpent;
    long jobHired;

    public HirerModel() {
    }

    public HirerModel(String userId, String dataProfileImage, String fullName, Long createdTimestamp,
                      long moneySpent, long jobHired) {
        this.userId = userId;
        this.dataProfileImage = dataProfileImage;
        this.fullName = fullName;
        this.createdTimestamp = createdTimestamp;
        this.moneySpent = moneySpent;
        this.jobHired = jobHired;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDataProfileImage() {
        return dataProfileImage;
    }

    public void setDataProfileImage(String dataProfileImage) {
        this.dataProfileImage = dataProfileImage;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public long getMoneySpent() {
        return moneySpent;
    }

    public void setMoneySpent(long moneySpent) {
        this.moneySpent = moneySpent;
    }

    public long getJobHired() {
        return jobHired;
    }

    public void setJobHired(long jobHired) {
        this.jobHired = jobHired;
    }
}
