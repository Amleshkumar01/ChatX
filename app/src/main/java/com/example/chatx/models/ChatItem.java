package com.example.chatx.models;

import com.example.chatx.models.User;

public class ChatItem {
    private static final int TYPE_USER = 1;
    private final int type;
    private final Object item;

    public ChatItem(User user) {
        this.type = TYPE_USER;
        this.item = user;
    }

    public int getType() {
        return type;
    }

    public String getId() {
        return ((User) item).getUid();
    }

    public String getName() {
        return ((User) item).getName();
    }

    public String getImage() {
        return ((User) item).getProfileImage();
    }

    public String getLastMessage() {
        return ((User) item).getStatus();
    }

    public boolean isOnline() {
        User user = (User) item;
        return user.getStatus() != null && user.getStatus().equals("online");
    }

    public long getLastSeen() {
        return 0; // Implement if needed
    }

    public boolean isGroup() {
        return false;
    }
}
