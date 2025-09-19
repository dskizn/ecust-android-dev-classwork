package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private ListView msgListView;
    private EditText inputText;
    private Button sendButton;
    private MsgAdapter adapter;
    private List<Msg> msgList = new ArrayList<>();

    private String currentUser;
    private String chatWithUser;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        databaseHelper = new DatabaseHelper(this);

        // 获取传递的用户信息
        Intent intent = getIntent();
        currentUser = intent.getStringExtra("currentUser");
        chatWithUser = intent.getStringExtra("chatWithUser");

        // 设置工具栏
        CustomToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("与 " + chatWithUser + " 聊天");
        toolbar.showBackButton(true);
        toolbar.setBackButtonClickListener(() -> {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        // 初始化视图
        msgListView = findViewById(R.id.messages_list);
        inputText = findViewById(R.id.message_edit_text);
        sendButton = findViewById(R.id.send_button);

        // 从数据库加载聊天记录
        loadChatMessages();

        // 设置适配器
        adapter = new MsgAdapter(this, R.layout.msg_item, msgList);
        msgListView.setAdapter(adapter);

        // 设置发送按钮点击事件
        sendButton.setOnClickListener(v -> {
            String content = inputText.getText().toString().trim();
            if (!content.isEmpty()) {
                sendMessage(content);
            }
        });

        // 标记对方发来的消息为已读
        databaseHelper.markMessagesAsRead(chatWithUser, currentUser);
    }

    private void loadChatMessages() {
        // 从数据库获取聊天记录
        List<Message> dbMessages = databaseHelper.getChatMessages(currentUser, chatWithUser);
        msgList.clear();

        for (Message message : dbMessages) {
            int type = message.getSender().equals(currentUser) ? Msg.TYPE_SENT : Msg.TYPE_RECEIVED;
            Msg msg = new Msg(message.getContent(), type);
            msgList.add(msg);
        }
    }

    private void sendMessage(String content) {
        // 保存消息到数据库
        long result = databaseHelper.saveMessage(currentUser, chatWithUser, content);

        if (result != -1) {
            // 添加到本地列表
            Msg msg = new Msg(content, Msg.TYPE_SENT);
            msgList.add(msg);
            adapter.notifyDataSetChanged();
            msgListView.setSelection(msgList.size() - 1);
            inputText.setText("");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 每次回到聊天界面时刷新消息
        loadChatMessages();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}