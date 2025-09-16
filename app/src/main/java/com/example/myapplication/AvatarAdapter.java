package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

public class AvatarAdapter extends ArrayAdapter<AvatarItem> {
    private int resourceId;
    private int selectedPosition = -1;

    public AvatarAdapter(Context context, int resource, List<AvatarItem> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    public AvatarAdapter(Context context, List<AvatarItem> objects) {
        this(context, R.layout.avatar_grid_item, objects);
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        for (int i = 0; i < getCount(); i++) {
            AvatarItem item = getItem(i);
            if (item != null) {
                item.setSelected(i == position);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AvatarItem avatarItem = getItem(position);
        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.avatarImage = view.findViewById(R.id.avatar_image);
            viewHolder.selectionBorder = view.findViewById(R.id.selection_border);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        if (avatarItem != null) {
            viewHolder.avatarImage.setImageResource(avatarItem.getAvatarResId());

            // 设置不透明度和边框
            if (avatarItem.isSelected()) {
                // 选中状态：完全显示 + 边框
                viewHolder.avatarImage.setAlpha(1.0f);
                viewHolder.selectionBorder.setVisibility(View.VISIBLE);
            } else {
                // 未选中状态：50%透明度 + 无边框
                viewHolder.avatarImage.setAlpha(0.5f);
                viewHolder.selectionBorder.setVisibility(View.GONE);
            }
        }

        return view;
    }

    static class ViewHolder {
        ImageView avatarImage;
        View selectionBorder;
    }
}