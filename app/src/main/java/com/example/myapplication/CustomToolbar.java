package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;

public class CustomToolbar extends LinearLayout {
    private TextView titleText;
    private ImageView backButton;
    private OnBackButtonClickListener backButtonClickListener;

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
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(v -> {
            if (backButtonClickListener != null) {
                backButtonClickListener.onBackButtonClick();
            }
        });
    }

    public void setTitle(String title) {
        titleText.setText(title);
    }
    public void showBackButton(boolean show) {
        backButton.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void setBackButtonClickListener(OnBackButtonClickListener listener) {
        this.backButtonClickListener = listener;
    }

    public interface OnBackButtonClickListener {
        void onBackButtonClick();
    }
}