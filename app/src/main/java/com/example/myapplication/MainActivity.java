package com.example.myapplication;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private ImageView avatarImage;
    private EditText usernameEdit, passwordEdit;
    private RadioGroup avatarGroup;
    private Button loginButton;
    private LinearLayout progressLayout;
    private ProgressBar progressBarHorizontal;
    private ProgressBar progressBarCircular;
    private TextView progressPercentText;

    private int[] avatarResources = {R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3};
    private CountDownTimer countDownTimer;
    private static final int LOGIN_DURATION = 5000; // 5秒
    private static final int UPDATE_INTERVAL = 50;  // 50毫秒更新一次

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化自定义工具栏
        CustomToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("用户登录");

        // 初始化UI组件
        // 初始化UI组件
        avatarImage = findViewById(R.id.avatar_image);
        usernameEdit = findViewById(R.id.username_edit);
        passwordEdit = findViewById(R.id.password_edit);
        avatarGroup = findViewById(R.id.avatar_group);
        loginButton = findViewById(R.id.login_button);
        progressLayout = findViewById(R.id.progress_layout);
        progressBarHorizontal = findViewById(R.id.login_progress_horizontal);
        progressBarCircular = findViewById(R.id.login_progress_circular);
        progressPercentText = findViewById(R.id.progress_percent);

        // 设置进度条最大值
        progressBarHorizontal.setMax(100);

        // 设置头像选择监听
        avatarGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int selectedIndex = 0;
            if (checkedId == R.id.avatar2) {
                selectedIndex = 1;
            } else if (checkedId == R.id.avatar3) {
                selectedIndex = 2;
            }
            avatarImage.setImageResource(avatarResources[selectedIndex]);
        });

        // 设置登录按钮点击事件
        loginButton.setOnClickListener(v -> {
            String username = usernameEdit.getText().toString();
            String password = passwordEdit.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                // 简单的验证
                return;
            }

            // 禁用登录按钮，防止重复点击
            loginButton.setEnabled(false);

            // 显示进度条
            progressLayout.setVisibility(View.VISIBLE);

            // 启动进度条动画
            startLoginProgress();
        });
    }

    private void startLoginProgress() {
        // 重置进度条
        progressBarHorizontal.setProgress(0);
        progressPercentText.setText("0%");

        // 创建进度更新计时器
        countDownTimer = new CountDownTimer(LOGIN_DURATION, UPDATE_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                // 计算进度百分比
                long elapsedTime = LOGIN_DURATION - millisUntilFinished;
                int progress = (int) ((elapsedTime * 100) / LOGIN_DURATION);

                // 更新进度条
                progressBarHorizontal.setProgress(progress);
                progressPercentText.setText(progress + "%");

                // 添加一些视觉反馈
                if (progress % 20 == 0) {
                    // 每20%进度时轻微改变圆形进度条颜色
                    progressBarCircular.setRotation(progress * 3.6f);
                }
            }

            @Override
            public void onFinish() {
                // 确保进度条显示100%
                progressBarHorizontal.setProgress(100);
                progressPercentText.setText("100%");

                // 添加完成动画效果
                progressBarCircular.animate().rotation(360).setDuration(300).start();

                // 延迟一下再跳转，让用户看到完成状态
                progressBarCircular.postDelayed(() -> {
                    performLogin();
                }, 300);
            }
        }.start();
    }

    private void performLogin() {
        // 获取选中的头像索引
        int selectedAvatarIndex = 0;
        int checkedId = avatarGroup.getCheckedRadioButtonId();
        if (checkedId == R.id.avatar2) {
            selectedAvatarIndex = 1;
        } else if (checkedId == R.id.avatar3) {
            selectedAvatarIndex = 2;
        }

        String username = usernameEdit.getText().toString();

        // 启动UserActivity并传递数据
        Intent intent = new Intent(MainActivity.this, UserActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("avatarResId", avatarResources[selectedAvatarIndex]);
        startActivity(intent);

        // 重置UI状态
        resetLoginUI();
    }

    private void resetLoginUI() {
        // 隐藏进度条
        progressLayout.setVisibility(View.GONE);
        // 重新启用登录按钮
        loginButton.setEnabled(true);
        loginButton.setAlpha(1.0f);
        // 清空输入框
        usernameEdit.setText("");
        passwordEdit.setText("");
        // 重置进度条
        progressBarHorizontal.setProgress(0);
        progressPercentText.setText("0%");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 防止内存泄漏，取消倒计时
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}