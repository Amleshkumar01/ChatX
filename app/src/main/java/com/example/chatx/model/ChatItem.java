package com.example.chatx.model;

import com.example.chatx.model.User;
import com.example.chatx.model.Group;

public class ChatItem {
    private static final int TYPE_USER = 1;
    private static final int TYPE_GROUP = 2;

    private final int type;
    private final Object item;

    public ChatItem(User user) {
        this.type = TYPE_USER;
        this.item = user;
    }

    public ChatItem(Group group) {
        this.type = TYPE_GROUP;
        this.item = group;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        if (type == TYPE_USER) {
            return ((User) item).getName();
        } else {
            return ((Group) item).getName();
        }
    }

    public String getDescription() {
        if (type == TYPE_USER) {
            return ((User) item).getEmail();
        } else {
            return ((Group) item).getDescription();
        }
    }

    public String getImage() {
        if (type == TYPE_USER) {
            String profileImage = ((User) item).getName();
            return (profileImage != null && !profileImage.isEmpty()) ? profileImage : "default_image_url";
        } else {
            return ((Group) item).getGroupImage();
        }
    }

    public String getId() {
        if (type == TYPE_USER) {
            return ((User) item).getUid();
        } else {
            return ((Group) item).getGroupId();
        }
    }

    public boolean isGroup() {
        return type == TYPE_GROUP;
    }

    public User getUser() {
        return type == TYPE_USER ? (User) item : null;
    }

    public Group getGroup() {
        return type == TYPE_GROUP ? (Group) item : null;
    }
}
