package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;

public class CustomToolbar extends LinearLayout {
    private TextView titleText;

    public CustomToolbar(Context context) {
        super(context);
        init(context);
    }

    public CustomToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.custom_toolbar, this);
        titleText = findViewById(R.id.toolbar_title);
    }

    public void setTitle(String title) {
        titleText.setText(title);
    }
}