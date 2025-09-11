package com.example.myapplication;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        // 设置自定义工具栏
        CustomToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("用户信息");

        // 获取传递过来的数据
        String username = getIntent().getStringExtra("username");
        int avatarResId = getIntent().getIntExtra("avatarResId", R.drawable.avatar1);

        // 显示用户信息
        ImageView userAvatar = findViewById(R.id.user_avatar);
        TextView userName = findViewById(R.id.user_name);

        userAvatar.setImageResource(avatarResId);
        userName.setText("欢迎, " + username);

        // 设置好友列表
        ListView friendsList = findViewById(R.id.friends_list);
    }
}