package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class AvatarSelectActivity extends AppCompatActivity {
    private GridView avatarGrid;
    private AvatarAdapter avatarAdapter;
    private int selectedAvatarResId = R.drawable.avatar1;

    // 头像资源数组
    private final int[] avatarResources = {
            R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3,
            R.drawable.avatar4, R.drawable.avatar5, R.drawable.avatar6,
            R.drawable.avatar7, R.drawable.avatar8, R.drawable.avatar9,
            //R.drawable.avatar10
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_select);

        // 设置自定义工具栏
        CustomToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("头像选择");
        toolbar.showBackButton(true);
        toolbar.setBackButtonClickListener(() -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        // 获取传递过来的当前头像
        int currentAvatar = getIntent().getIntExtra("currentAvatar", R.drawable.avatar1);
        selectedAvatarResId = currentAvatar;

        avatarGrid = findViewById(R.id.avatar_grid);
        Button confirmButton = findViewById(R.id.confirm_button);
        Button cancelButton = findViewById(R.id.cancel_button);

        // 初始化头像适配器
        List<AvatarItem> avatarList = new ArrayList<>();
        for (int i = 0; i < avatarResources.length; i++) {
            boolean isSelected = (avatarResources[i] == selectedAvatarResId);
            avatarList.add(new AvatarItem(avatarResources[i], isSelected));
            if (isSelected) {
                int selectedPosition = i; // 记录选中位置
            }
        }

        avatarAdapter = new AvatarAdapter(this, avatarList);
        avatarGrid.setAdapter(avatarAdapter);

        // 头像选择监听
        avatarGrid.setOnItemClickListener((parent, view, position, id) -> {
            selectedAvatarResId = avatarResources[position];
            avatarAdapter.setSelectedPosition(position);
        });

        // 确认按钮
        confirmButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selectedAvatar", selectedAvatarResId);
            setResult(RESULT_OK, resultIntent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // 取消按钮
        cancelButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 添加返回动画
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}