package com.example.freelancerapp10.model;

import com.google.firebase.database.ServerValue;

public class ReportModel {
        private String userId;
        private String otherUserId;
        private String jobId;
        private String text;

        private Object timestamp;

        // Default constructor (required for Firebase)
        public ReportModel() {
        }

    public ReportModel(String userId, String otherUserId, String jobId, String text) {
        this.userId = userId;
        this.otherUserId = otherUserId;
        this.jobId = jobId;
        this.text = text;
        this.timestamp = ServerValue.TIMESTAMP;
    }

    public String getOtherUserId() {
        return otherUserId;
    }

    public void setOtherUserId(String otherUserId) {
        this.otherUserId = otherUserId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }



    public void setText(String text) {
        this.text = text;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
            return userId;
        }


        public String getText() {
            return text;
        }


}
