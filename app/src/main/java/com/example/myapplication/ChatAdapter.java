package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

public class ChatAdapter extends ArrayAdapter<ChatMessage> {
    private int resourceId;
    private String currentUser;

    public ChatAdapter(Context context, List<ChatMessage> messages, String currentUser) {
        super(context, R.layout.chat_message_item, messages);
        this.resourceId = R.layout.chat_message_item;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ChatMessage message = getItem(position);
        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.messageText = view.findViewById(R.id.message_text);
            viewHolder.timeText = view.findViewById(R.id.time_text);
            viewHolder.senderText = view.findViewById(R.id.sender_text);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        if (message != null) {
            viewHolder.messageText.setText(message.getMessage());
            viewHolder.timeText.setText(message.getFormattedTime());

            // 根据发送者显示不同的布局
            if (message.isSentByMe(currentUser)) {
                // 自己发送的消息
                viewHolder.senderText.setText("我");
                viewHolder.senderText.setVisibility(View.VISIBLE);
                view.setBackgroundResource(R.drawable.chat_bubble_sent);
            } else {
                // 对方发送的消息
                viewHolder.senderText.setText(message.getSender());
                viewHolder.senderText.setVisibility(View.VISIBLE);
                view.setBackgroundResource(R.drawable.chat_bubble_received);
            }
        }

        return view;
    }

    static class ViewHolder {
        TextView messageText;
        TextView timeText;
        TextView senderText;
    }
}