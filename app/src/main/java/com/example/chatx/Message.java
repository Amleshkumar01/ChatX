package com.example.chatx;

public class Message {
    private String text;
    private String senderId;
    private long timestamp;

    public Message() {
        // Required empty constructor for Firebase
    }

    public Message(String text, String senderId) {
        this.text = text;
        this.senderId = senderId;
        this.timestamp = System.currentTimeMillis();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
