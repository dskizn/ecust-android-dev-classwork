package com.example.myapplication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private ListView userListView;
    private UserManagementAdapter adapter;
    private TextView userCountText;
    private TextView adminCountText;
    private TextView normalCountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_user_management);

        // 初始化数据库
        databaseHelper = new DatabaseHelper(this);

        // 设置自定义工具栏
        CustomToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("用户管理");
        toolbar.showBackButton(true);
        toolbar.setBackButtonClickListener(() -> {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        // 初始化统计信息文本视图
        userCountText = findViewById(R.id.user_count_text);
        adminCountText = findViewById(R.id.admin_count_text);
        normalCountText = findViewById(R.id.normal_count_text);

        // 初始化用户列表
        userListView = findViewById(R.id.user_list_view);
        loadUsers();

        // 设置列表项点击事件
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) parent.getItemAtPosition(position);
                showUserOptions(user);
            }
        });
    }

    private void loadUsers() {
        List<User> users = databaseHelper.getAllUsers();
        adapter = new UserManagementAdapter(this, users);
        userListView.setAdapter(adapter);
        // 更新统计信息
        updateStatistics();
    }
    private void updateStatistics() {
        int totalUsers = databaseHelper.getTotalUserCount();
        int adminUsers = databaseHelper.getAdminUserCount();
        int normalUsers = totalUsers - adminUsers;

        userCountText.setText("总计: " + totalUsers + " 用户");
        adminCountText.setText("管理员: " + adminUsers);
        normalCountText.setText("普通用户: " + normalUsers);
    }

    private void showUserOptions(User user) {
        String[] options;
        if (user.isAdmin()) {
            options = new String[]{"查看详情", "取消管理员权限"};
        } else {
            options = new String[]{"查看详情", "设为管理员", "删除用户"};
        }

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("用户操作 - " + user.getUsername())
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0: // 查看详情
                            showUserDetails(user);
                            break;
                        case 1: // 权限操作
                            if (user.isAdmin()) {
                                updateUserAdminStatus(user.getUsername(), false);
                            } else {
                                if (options.length > 2) {
                                    updateUserAdminStatus(user.getUsername(), true);
                                } else {
                                    deleteUser(user.getUsername());
                                }
                            }
                            break;
                        case 2: // 删除用户
                            deleteUser(user.getUsername());
                            break;
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void showUserDetails(User user) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("用户详情")
                .setMessage("用户名: " + user.getUsername() + "\n" +
                        "身份: " + (user.isAdmin() ? "管理员" : "普通用户") + "\n" +
                        "头像ID: " + user.getAvatarResId())
                .setPositiveButton("确定", null)
                .show();
    }

    private void updateUserAdminStatus(String username, boolean isAdmin) {
        // 这里需要实现更新用户权限的数据库操作
        Toast.makeText(this, "用户权限更新功能待实现: " + username + " -> " + (isAdmin ? "管理员" : "普通用户"), Toast.LENGTH_SHORT).show();
        // 刷新列表和统计信息
        loadUsers();
    }

    private void deleteUser(String username) {
        if (username.equals("管理员")) {
            Toast.makeText(this, "不能删除默认管理员账号", Toast.LENGTH_SHORT).show();
            return;
        }

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("确认删除")
                .setMessage("确定要删除用户 " + username + " 吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    int result = databaseHelper.deleteUser(username);
                    if (result > 0) {
                        Toast.makeText(this, "用户删除成功", Toast.LENGTH_SHORT).show();
                        // 刷新列表和统计信息
                        loadUsers();
                    } else {
                        Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 当页面重新显示时刷新数据
        loadUsers();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}