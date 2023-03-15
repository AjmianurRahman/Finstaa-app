package com.example.myapplication.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class NotificatonModel {
    String id, notification, imageUrl;

    @ServerTimestamp
    Date time;

    public NotificatonModel(String id, String notification, String imageUrl, Date time) {
        this.id = id;
        this.notification = notification;
        this.imageUrl = imageUrl;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
