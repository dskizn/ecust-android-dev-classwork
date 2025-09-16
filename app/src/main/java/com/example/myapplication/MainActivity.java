package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText usernameEdit, passwordEdit;
    private Button loginButton;
    private LinearLayout progressLayout;
    private ProgressBar progressBarCircular;
    private DatabaseHelper databaseHelper;

    private CountDownTimer countDownTimer;
    private static final int LOGIN_DURATION = 2000; // 缩短为2秒

    // 注册相关的请求码
    private static final int REGISTER_AVATAR_SELECT_REQUEST_CODE = 1002;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        // 初始化自定义工具栏
        CustomToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("用户登录");

        // 初始化数据库
        databaseHelper = new DatabaseHelper(this);

        // 初始化UI组件
        usernameEdit = findViewById(R.id.username_edit);
        passwordEdit = findViewById(R.id.password_edit);
        loginButton = findViewById(R.id.login_button);
        progressLayout = findViewById(R.id.progress_layout);
        progressBarCircular = findViewById(R.id.login_progress_circular);

        // 设置登录按钮点击事件
        loginButton.setOnClickListener(v -> {
            String username = usernameEdit.getText().toString();
            String password = passwordEdit.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                return;
            }

            // 数据库验证用户
            if (databaseHelper.checkUser(username, password)) {
                // 检查是否是管理员
                boolean isAdmin = databaseHelper.isAdmin(username);
                // 获取用户头像
                int avatarResId = databaseHelper.getUserAvatar(username);

                // 禁用登录按钮，防止重复点击
                loginButton.setEnabled(false);
                loginButton.setAlpha(0.5f);

                // 显示进度条
                progressLayout.setVisibility(View.VISIBLE);

                // 启动进度条动画
                startLoginProgress(username, avatarResId, isAdmin);
            } else {
                Toast.makeText(MainActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
            }
        });

        // 添加注册按钮
        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(v -> {
            showRegisterDialog();
        });
    }

    // 注册相关的成员变量
    private ImageView registerAvatarImageView;
    private int registerSelectedAvatarResId = R.drawable.avatar1;

    private void showRegisterDialog() {
        // 创建注册对话框视图
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_register, null);
        EditText registerUsername = dialogView.findViewById(R.id.register_username);
        EditText registerPassword = dialogView.findViewById(R.id.register_password);
        registerAvatarImageView = dialogView.findViewById(R.id.register_avatar);

        // 设置默认头像
        registerAvatarImageView.setImageResource(registerSelectedAvatarResId);

        // 设置头像点击事件
        registerAvatarImageView.setOnClickListener(v -> {
            openAvatarSelection(REGISTER_AVATAR_SELECT_REQUEST_CODE);
        });

        AlertDialog registerDialog = new AlertDialog.Builder(this)
                .setTitle("用户注册")
                .setView(dialogView)
                .setPositiveButton("注册", (dialog, which) -> {
                    String username = registerUsername.getText().toString();
                    String password = registerPassword.getText().toString();
                    if (!username.isEmpty() && !password.isEmpty()) {
                        long result = databaseHelper.insertUser(username, password, registerSelectedAvatarResId);
                        if (result != -1) {
                            Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
                            // 重置注册头像选择
                            registerSelectedAvatarResId = R.drawable.avatar1;
                        } else {
                            Toast.makeText(this, "用户名已存在", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    // 取消时重置注册头像选择
                    registerSelectedAvatarResId = R.drawable.avatar1;
                })
                .create();

        registerDialog.show();
    }

    private void openAvatarSelection(int requestCode) {
        Intent intent = new Intent(MainActivity.this, AvatarSelectActivity.class);
        intent.putExtra("currentAvatar", registerSelectedAvatarResId);
        startActivityForResult(intent, requestCode);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            int selectedAvatar = data.getIntExtra("selectedAvatar", R.drawable.avatar1);

            if (requestCode == REGISTER_AVATAR_SELECT_REQUEST_CODE) {
                // 处理注册对话框中的头像选择
                if (registerAvatarImageView != null) {
                    registerAvatarImageView.setImageResource(selectedAvatar);
                    registerSelectedAvatarResId = selectedAvatar;
                }
            }
        }
    }

    private void startLoginProgress(String username, int avatarResId, boolean isAdmin) {
        countDownTimer = new CountDownTimer(LOGIN_DURATION, LOGIN_DURATION) {
            @Override
            public void onTick(long millisUntilFinished) {
                // 让圆形进度条自动旋转
            }

            @Override
            public void onFinish() {
                performLogin(username, avatarResId, isAdmin);
            }
        }.start();
    }

    private void performLogin(String username, int avatarResId, boolean isAdmin) {
        Intent intent = new Intent(MainActivity.this, UserActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("avatarResId", avatarResId);
        intent.putExtra("isAdmin", isAdmin);
        startActivity(intent);

        resetLoginUI();
    }

    private void resetLoginUI() {
        progressLayout.setVisibility(View.GONE);
        loginButton.setEnabled(true);
        loginButton.setAlpha(1.0f);
        usernameEdit.setText("");
        passwordEdit.setText("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}