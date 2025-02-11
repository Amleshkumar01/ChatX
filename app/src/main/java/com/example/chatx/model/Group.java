package com.example.chatx.model;

import java.util.HashMap;
import java.util.Map;

public class Group {
    private String groupId;
    private String name;
    private String description;
    private String groupImage;
    private String creatorId;
    private long createdAt;
    private Map<String, Boolean> members;
    private Map<String, Boolean> admins;

    public Group() {
        // Required empty constructor for Firebase
        this.members = new HashMap<>();
        this.admins = new HashMap<>();
    }

    public Group(String groupId, String name, String description, String creatorId) {
        this.groupId = groupId;
        this.name = name;
        this.description = description;
        this.creatorId = creatorId;
        this.createdAt = System.currentTimeMillis();
        this.members = new HashMap<>();
        this.admins = new HashMap<>();
        
        // Add creator as member and admin
        this.members.put(creatorId, true);
        this.admins.put(creatorId, true);
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public Map<String, Boolean> getMembers() {
        return members;
    }

    public void setMembers(Map<String, Boolean> members) {
        this.members = members;
    }

    public Map<String, Boolean> getAdmins() {
        return admins;
    }

    public void setAdmins(Map<String, Boolean> admins) {
        this.admins = admins;
    }

    public void addMember(String userId) {
        if (members == null) {
            members = new HashMap<>();
        }
        members.put(userId, true);
    }

    public void removeMember(String userId) {
        if (members != null) {
            members.remove(userId);
        }
    }

    public void addAdmin(String userId) {
        if (admins == null) {
            admins = new HashMap<>();
        }
        admins.put(userId, true);
    }

    public void removeAdmin(String userId) {
        if (admins != null) {
            admins.remove(userId);
        }
    }

    public boolean isMember(String userId) {
        return members != null && members.containsKey(userId);
    }

    public boolean isAdmin(String userId) {
        return admins != null && admins.containsKey(userId);
    }
}
