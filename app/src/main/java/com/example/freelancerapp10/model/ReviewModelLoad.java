package com.example.freelancerapp10.model;

public class ReviewModelLoad {
        private String id;
        private float rating;
        private String text,key;

        private long timestamp;

        // Default constructor (required for Firebase)
        public ReviewModelLoad() {
        }

        public ReviewModelLoad(String id, float rating, String text) {
            this.id = id;
            this.rating = rating;
            this.text = text;
        }

    public void setId(String id) {
        this.id = id;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
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
