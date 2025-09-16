package com.example.myapplication;

public class User {
    private String username;
    private int avatarResId;
    private boolean isAdmin;

    public User(String username, int avatarResId, boolean isAdmin) {
        this.username = username;
        this.avatarResId = avatarResId;
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return username;
    }

    public int getAvatarResId() {
        return avatarResId;
    }

    public void setAvatarResId(int avatarResId) {
        this.avatarResId = avatarResId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}