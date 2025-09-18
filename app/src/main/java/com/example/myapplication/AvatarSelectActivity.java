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
    private int selectedPosition = 0;

    private final int[] avatarResources = {
            R.drawable.avatar1, R.drawable.avatar2, R.drawable.avatar3,
            R.drawable.avatar4, R.drawable.avatar5, R.drawable.avatar6,
            R.drawable.avatar7, R.drawable.avatar8, R.drawable.avatar9
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_select);

        CustomToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("头像选择");
        toolbar.showBackButton(true);
        toolbar.setBackButtonClickListener(() -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        int currentAvatar = getIntent().getIntExtra("currentAvatar", R.drawable.avatar1);
        selectedAvatarResId = currentAvatar;

        avatarGrid = findViewById(R.id.avatar_grid);
        Button confirmButton = findViewById(R.id.confirm_button);
        Button cancelButton = findViewById(R.id.cancel_button);

        List<AvatarItem> avatarList = new ArrayList<>();
        for (int i = 0; i < avatarResources.length; i++) {
            if (avatarResources[i] == selectedAvatarResId) {
                selectedPosition = i;
            }
            avatarList.add(new AvatarItem(avatarResources[i]));
        }

        avatarAdapter = new AvatarAdapter(this, avatarList);
        avatarGrid.setAdapter(avatarAdapter);
        avatarAdapter.setSelectedPosition(selectedPosition);

        avatarGrid.setOnItemClickListener((parent, view, position, id) -> {
            selectedAvatarResId = avatarResources[position];
            selectedPosition = position;
            avatarAdapter.setSelectedPosition(position);
        });

        confirmButton.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selectedAvatar", selectedAvatarResId);
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        cancelButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}