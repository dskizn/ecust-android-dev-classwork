package com.example.myapplication;

public class AvatarItem {
    private int avatarResId;
    private boolean isSelected;

    public AvatarItem(int avatarResId, boolean isSelected) {
        this.avatarResId = avatarResId;
        this.isSelected = isSelected;
    }

    public int getAvatarResId() {
        return avatarResId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}