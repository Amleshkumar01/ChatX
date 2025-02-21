package com.example.chatx.models;

public class Message {
    private String messageId;
    private String senderId;
    private String message;
    private long timestamp;

    public Message() {
        // Required empty constructor for Firebase
    }

    public Message(String messageId, String senderId, String message) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
