package com.example.chatx.models;

public class User {
    private String uid;
    private String name;
    private String email;
    private String status;
    private String profileImage;

    public User() {
        // Required empty constructor for Firebase
    }

    public User(String uid, String name, String email, String status, String profileImage) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.status = status;
        this.profileImage = profileImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
