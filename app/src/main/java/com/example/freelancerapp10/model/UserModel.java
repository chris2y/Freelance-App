package com.example.freelancerapp10.model;


import com.google.firebase.database.ServerValue;

public class UserModel {
    private String fullName;

    private Object createdTimestamp;

    private String userId;
    private String dataProfileImage;
    private String jobId;
    private String title;


    public UserModel() {
    }

    public UserModel(String fullName, String userId) {

        this.fullName = fullName;
        this.createdTimestamp = ServerValue.TIMESTAMP; // Store the current time as a long value
        this.userId = userId;
    }

    public UserModel(String fullName, String userId,String title) {

        this.fullName = fullName;
        this.createdTimestamp = ServerValue.TIMESTAMP; // Store the current time as a long value
        this.userId = userId;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
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

    public Object getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Object createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

