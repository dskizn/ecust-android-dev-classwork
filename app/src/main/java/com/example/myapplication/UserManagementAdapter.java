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

public class UserManagementAdapter extends ArrayAdapter<User> {
    private int resourceId;

    public UserManagementAdapter(Context context, List<User> objects) {
        super(context, R.layout.user_management_item, objects);
        resourceId = R.layout.user_management_item;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        User user = getItem(position);
        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.avatar = view.findViewById(R.id.user_avatar);
            viewHolder.username = view.findViewById(R.id.user_name);
            viewHolder.role = view.findViewById(R.id.user_role);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        if (user != null) {
            viewHolder.avatar.setImageResource(user.getAvatarResId());
            viewHolder.username.setText(user.getUsername());
            viewHolder.role.setText(user.isAdmin() ? "管理员" : "普通用户");

            // 设置角色文字颜色
            if (user.isAdmin()) {
                viewHolder.role.setTextColor(getContext().getResources().getColor(android.R.color.holo_red_dark));
            } else {
                viewHolder.role.setTextColor(getContext().getResources().getColor(android.R.color.darker_gray));
            }
        }

        return view;
    }

    static class ViewHolder {
        ImageView avatar;
        TextView username;
        TextView role;
    }
}