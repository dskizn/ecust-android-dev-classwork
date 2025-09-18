package com.example.myapplication;

public class User {
    private String username;
    private String password;
    private int avatarResId;
    private boolean isAdmin;

    public User(String username, int avatarResId, boolean isAdmin) {
        this.username = username;
        this.avatarResId = avatarResId;
        this.isAdmin = isAdmin;
    }

    public User(String username, String password, int avatarResId, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.avatarResId = avatarResId;
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
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

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}