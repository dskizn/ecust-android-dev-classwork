package com.example.myapplication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class UserActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private ListView friendsList;
    private FriendAdapter adapter;
    private boolean isAdmin = false;
    private LinearLayout adminSection;
    private ImageView userAvatar;
    private String currentUsername;
    private int currentAvatarResId;

    private static final int AVATAR_UPDATE_REQUEST_CODE = 2001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_user);

        // 初始化数据库
        databaseHelper = new DatabaseHelper(this);

        // 获取用户信息
        currentUsername = getIntent().getStringExtra("username");
        currentAvatarResId = getIntent().getIntExtra("avatarResId", R.drawable.avatar1);
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        // 设置自定义工具栏
        CustomToolbar toolbar = findViewById(R.id.toolbar);
        String title = isAdmin ? "管理员面板" : "用户信息";
        toolbar.setTitle(title);
        toolbar.showBackButton(true);
        toolbar.setBackButtonClickListener(() -> {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        // 显示用户信息
        userAvatar = findViewById(R.id.user_avatar);
        TextView userName = findViewById(R.id.user_name);
        userAvatar.setImageResource(currentAvatarResId);

        String welcomeText = isAdmin ? "管理员您好！" : "欢迎, " + currentUsername;
        userName.setText(welcomeText);

        // 设置头像点击事件
        userAvatar.setOnClickListener(v -> {
            openAvatarSelectionForUpdate();
        });

        // 初始化管理员区域
        adminSection = findViewById(R.id.admin_section);

        // 设置游戏按钮
        Button gameButton = findViewById(R.id.game_button);
        gameButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, NumberPuzzleActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // 设置好友列表
        friendsList = findViewById(R.id.friends_list);
        loadFriendsFromDatabase();

        // 如果是管理员，显示管理员功能区
        if (isAdmin) {
            showAdminFeatures();
        }
    }

    private void showAdminFeatures() {
        // 显示管理员功能区
        adminSection.setVisibility(View.VISIBLE);

        // 设置用户管理按钮
        Button userManageButton = findViewById(R.id.user_manage_button);
        userManageButton.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, UserManagementActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // 设置刷新按钮
        Button refreshButton = findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(v -> {
            refreshData();
            Toast.makeText(this, "数据已刷新", Toast.LENGTH_SHORT).show();
        });

        // 设置设置按钮（预留功能）
        Button settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(v -> {
            Toast.makeText(this, "系统设置功能开发中", Toast.LENGTH_SHORT).show();
        });
    }

    private void openAvatarSelectionForUpdate() {
        Intent intent = new Intent(UserActivity.this, AvatarSelectActivity.class);
        intent.putExtra("currentAvatar", currentAvatarResId);
        startActivityForResult(intent, AVATAR_UPDATE_REQUEST_CODE);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AVATAR_UPDATE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            int selectedAvatar = data.getIntExtra("selectedAvatar", R.drawable.avatar1);

            // 更新本地头像显示
            currentAvatarResId = selectedAvatar;
            userAvatar.setImageResource(selectedAvatar);

            // 更新数据库中的头像信息
            boolean updateSuccess = databaseHelper.updateUserAvatar(currentUsername, selectedAvatar);
            if (updateSuccess) {
                Toast.makeText(this, "头像更新成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "头像更新失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadFriendsFromDatabase() {
        List<Friend> friends = databaseHelper.getAllFriends();
        adapter = new FriendAdapter(this, R.layout.friend_item, friends);
        friendsList.setAdapter(adapter);
    }

    private void refreshData() {
        loadFriendsFromDatabase();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}