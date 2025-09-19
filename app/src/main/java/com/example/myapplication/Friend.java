package com.example.myapplication;

public class Friend {
    private String name;
    private String status;
    private int avatarResId;
    private boolean isOnline;

    public Friend(String name, String status, int avatarResId, boolean isOnline) {
        this.name = name;
        this.status = status;
        this.avatarResId = avatarResId;
        this.isOnline = isOnline;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public int getAvatarResId() {
        return avatarResId;
    }

    public boolean isOnline() {
        return isOnline;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAvatarResId(int avatarResId) {
        this.avatarResId = avatarResId;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }


}