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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText usernameEdit, passwordEdit;
    private Button loginButton;
    private LinearLayout progressLayout;
    private ProgressBar progressBarCircular;
    private DatabaseHelper databaseHelper;
    private CountDownTimer countDownTimer;
    private static final int LOGIN_DURATION = 2000;
    private static final int REGISTER_AVATAR_SELECT_REQUEST_CODE = 1002;
    private ImageView registerAvatarImageView;
    private int registerSelectedAvatarResId = R.drawable.avatar1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        CustomToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("用户登录");
        databaseHelper = new DatabaseHelper(this);

        usernameEdit = findViewById(R.id.username_edit);
        passwordEdit = findViewById(R.id.password_edit);
        loginButton = findViewById(R.id.login_button);
        progressLayout = findViewById(R.id.progress_layout);
        progressBarCircular = findViewById(R.id.login_progress_circular);

        loginButton.setOnClickListener(v -> {
            String username = usernameEdit.getText().toString();
            String password = passwordEdit.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                return;
            }

            if (databaseHelper.checkUser(username, password)) {
                boolean isAdmin = databaseHelper.isAdmin(username);
                int avatarResId = databaseHelper.getUserAvatar(username);

                loginButton.setEnabled(false);
                loginButton.setAlpha(0.5f);
                progressLayout.setVisibility(View.VISIBLE);
                startLoginProgress(username, avatarResId, isAdmin);
            } else {
                Toast.makeText(MainActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
            }
        });

        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(v -> showRegisterDialog());
    }

    private void showRegisterDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_register, null);
        EditText registerUsername = dialogView.findViewById(R.id.register_username);
        EditText registerPassword = dialogView.findViewById(R.id.register_password);
        registerAvatarImageView = dialogView.findViewById(R.id.register_avatar);

        registerAvatarImageView.setImageResource(registerSelectedAvatarResId);
        registerAvatarImageView.setOnClickListener(v -> openAvatarSelection(REGISTER_AVATAR_SELECT_REQUEST_CODE));

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
                            registerSelectedAvatarResId = R.drawable.avatar1;
                        } else {
                            Toast.makeText(this, "用户名已存在", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    registerSelectedAvatarResId = R.drawable.avatar1;
                })
                .create();

        registerDialog.show();
    }

    private void openAvatarSelection(int requestCode) {
        Intent intent = new Intent(MainActivity.this, AvatarSelectActivity.class);
        intent.putExtra("currentAvatar", registerSelectedAvatarResId);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            int selectedAvatar = data.getIntExtra("selectedAvatar", R.drawable.avatar1);
            if (requestCode == REGISTER_AVATAR_SELECT_REQUEST_CODE) {
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
            public void onTick(long millisUntilFinished) {}
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