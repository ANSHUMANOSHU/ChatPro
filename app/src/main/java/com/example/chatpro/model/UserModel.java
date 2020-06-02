package com.example.chatpro.model;

import java.io.Serializable;

public class UserModel implements Serializable {

    String phone,uid,name,email,profileimage,coverimage,date,onlinestatus;
    String status;

    public UserModel() {
    }

    public UserModel(String phone, String uid, String name, String email, String profileimage, String coverimage, String date, String onlinestatus, String status) {
        this.phone = phone;
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.profileimage = profileimage;
        this.coverimage = coverimage;
        this.date = date;
        this.onlinestatus = onlinestatus;
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getCoverimage() {
        return coverimage;
    }

    public void setCoverimage(String coverimage) {
        this.coverimage = coverimage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOnlinestatus() {
        return onlinestatus;
    }

    public void setOnlinestatus(String onlinestatus) {
        this.onlinestatus = onlinestatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
