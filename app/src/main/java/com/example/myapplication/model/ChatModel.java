package com.example.myapplication.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ChatModel {
    String id, message, senderId;

    @ServerTimestamp
    Date time;

    public ChatModel(String id, String message, String senderId, Date time) {
        this.id = id;
        this.message = message;
        this.senderId = senderId;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
