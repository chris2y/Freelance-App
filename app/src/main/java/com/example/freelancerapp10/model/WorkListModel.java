package com.example.freelancerapp10.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class WorkListModel implements Parcelable {

    private String docId;
    private String title;
    private double average_rating;
    private double total_ratings;
    private String price;
    private String logoUrl;
    private Timestamp timestamp; // Assuming timestamp is a Timestamp object

    public WorkListModel() {
    }

    public WorkListModel(String docId, String title, double average_rating, double total_ratings, String price, String logoUrl, Timestamp timestamp) {
        this.docId = docId;
        this.title = title;
        this.average_rating = average_rating;
        this.total_ratings = total_ratings;
        this.price = price;
        this.logoUrl = logoUrl;
        this.timestamp = timestamp;
    }

    protected WorkListModel(Parcel in) {
        docId = in.readString();
        title = in.readString();
        average_rating = in.readDouble();
        total_ratings = in.readDouble();
        price = in.readString();
        logoUrl = in.readString();
        timestamp = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public static final Creator<WorkListModel> CREATOR = new Creator<WorkListModel>() {
        @Override
        public WorkListModel createFromParcel(Parcel in) {
            return new WorkListModel(in);
        }

        @Override
        public WorkListModel[] newArray(int size) {
            return new WorkListModel[size];
        }
    };

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAverage_rating() {
        return average_rating;
    }

    public void setAverage_rating(double average_rating) {
        this.average_rating = average_rating;
    }

    public double getTotal_ratings() {
        return total_ratings;
    }

    public void setTotal_ratings(double total_ratings) {
        this.total_ratings = total_ratings;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(docId);
        parcel.writeString(title);
        parcel.writeDouble(average_rating);
        parcel.writeDouble(total_ratings);
        parcel.writeString(price);
        parcel.writeString(logoUrl);
        parcel.writeParcelable(timestamp, i);
    }
}
