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

    public FriendAdapter(Context context, int resource, List<Friend> objects) {
        super(context, resource, objects);
        resourceId = resource;
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
            viewHolder.avatar.setImageResource(friend.getAvatarResId());
            viewHolder.name.setText(friend.getName());
            viewHolder.status.setText(friend.getStatus());

            // 设置在线状态
            if (friend.isOnline()) {
                viewHolder.onlineStatus.setVisibility(View.VISIBLE);
                viewHolder.onlineStatus.setBackgroundResource(R.drawable.online_status_bg);
            } else {
                viewHolder.onlineStatus.setVisibility(View.INVISIBLE);
            }
        }

        return view;
    }

    class ViewHolder {
        ImageView avatar;
        TextView name;
        TextView status;
        View onlineStatus;
    }
}