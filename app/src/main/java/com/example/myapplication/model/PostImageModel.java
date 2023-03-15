package com.example.myapplication.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class PostImageModel {
    private String imageUrl, id, description, uid;
    @ServerTimestamp
    private Date timeStamp;

    public PostImageModel() {
    }

    public PostImageModel(String imageUrl, String id, Date timeStamp, String description, String uid) {
        this.imageUrl = imageUrl;
        this.id = id;
        this.description = description;
        this.timeStamp = timeStamp;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}

