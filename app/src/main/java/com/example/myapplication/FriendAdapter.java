package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

public class FriendAdapter extends ArrayAdapter<Friend> {
    private int resourceId;
    private OnFriendClickListener onFriendClickListener; // 添加监听器字段

    public FriendAdapter(Context context, int resource, List<Friend> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    // 添加设置监听器的方法
    public void setOnFriendClickListener(OnFriendClickListener listener) {
        this.onFriendClickListener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Friend friend = getItem(position);
        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.avatar = view.findViewById(R.id.friend_avatar);
            viewHolder.name = view.findViewById(R.id.friend_name);
            viewHolder.status = view.findViewById(R.id.friend_status);
            viewHolder.onlineStatus = view.findViewById(R.id.online_status);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        if (friend != null) {
            int avatarResId = friend.getAvatarResId();
            if (avatarResId <= 0) {
                avatarResId = R.drawable.avatar1;
            }

            viewHolder.avatar.setImageResource(avatarResId);
            viewHolder.name.setText(friend.getName());
            viewHolder.status.setText(friend.getStatus());

            // 设置在线状态 - 所有用户都显示为离线
            viewHolder.onlineStatus.setVisibility(View.VISIBLE);
            if (friend.isOnline()) {
                viewHolder.onlineStatus.setBackgroundResource(R.drawable.online_status_bg);
            } else {
                // 离线状态使用灰色背景
                viewHolder.onlineStatus.setBackgroundResource(R.drawable.offline_status_bg);
            }
            // 设置点击事件
            view.setOnClickListener(v -> {
                if (onFriendClickListener != null) {
                    onFriendClickListener.onFriendClick(friend);
                }
            });
        }

        return view;
    }

    static class ViewHolder {
        ImageView avatar;
        TextView name;
        TextView status;
        View onlineStatus;
    }

    public interface OnFriendClickListener {
        void onFriendClick(Friend friend);
    }
}