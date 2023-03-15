package com.example.myapplication.model;

public class Users {

    private String email, name, ProfileImage, Uid;

    public Users() {
    }

    public Users(String email, String name, String profileImage, String uid) {
        email = email;
        name = name;
        ProfileImage = profileImage;
        Uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public void setProfileImage(String profileImage) {
        ProfileImage = profileImage;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }
}
