package com.example.chatpro.model;

public class ChatModel {

    public String message, senderuid, receiveruid, stamp, imageurl, type;
    public boolean isseen;
    public String key;

    public ChatModel() {
    }

    public ChatModel(String message, String senderuid, String receiveruid, String stamp, String imageurl, String type, boolean isseen, String key) {
        this.message = message;
        this.senderuid = senderuid;
        this.receiveruid = receiveruid;
        this.stamp = stamp;
        this.imageurl = imageurl;
        this.type = type;
        this.isseen = isseen;
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderuid() {
        return senderuid;
    }

    public void setSenderuid(String senderuid) {
        this.senderuid = senderuid;
    }

    public String getReceiveruid() {
        return receiveruid;
    }

    public void setReceiveruid(String receiveruid) {
        this.receiveruid = receiveruid;
    }

    public String getStamp() {
        return stamp;
    }

    public void setStamp(String stamp) {
        this.stamp = stamp;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}