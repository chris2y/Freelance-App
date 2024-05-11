package com.example.freelancerapp10.model;

import com.google.firebase.database.ServerValue;

public class ReviewModel {
        private String id;
        private float rating;
        private String text;

        private Object timestamp;

        // Default constructor (required for Firebase)
        public ReviewModel() {
        }

        public ReviewModel(String id, float rating, String text) {
            this.id = id;
            this.rating = rating;
            this.text = text;
            this.timestamp = ServerValue.TIMESTAMP;
        }

    public void setId(String id) {
        this.id = id;
    }

    public void setRating(float rating) {
        this.rating = rating;
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

    public String getId() {
            return id;
        }

        public float getRating() {
            return rating;
        }

        public String getText() {
            return text;
        }


}
