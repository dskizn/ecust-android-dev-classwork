package com.example.myapplication;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
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
        toolbar.showBackButton(true); // 显示返回按钮

        // 设置返回按钮点击事件
        toolbar.setBackButtonClickListener(() -> {
            finish(); // 关闭当前Activity，返回上一个页面
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

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
        List<Friend> friends = getFriendsData();

        FriendAdapter adapter = new FriendAdapter(this, R.layout.friend_item, friends);
        friendsList.setAdapter(adapter);
    }
    private List<Friend> getFriendsData() {
        List<Friend> friends = new ArrayList<>();

        // 添加10个好友的基础信息
        friends.add(new Friend("用户1", "休息一下", R.drawable.avatar4, true));
        friends.add(new Friend("用户2", "学习中...", R.drawable.avatar5, true));
        friends.add(new Friend("用户3", "忙碌中，请勿打扰", R.drawable.avatar6, false));
        friends.add(new Friend("用户4", "在线", R.drawable.avatar7, true));
        friends.add(new Friend("用户5", "刚刚更新了动态", R.drawable.avatar8, false));
        return friends;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 添加返回动画
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}