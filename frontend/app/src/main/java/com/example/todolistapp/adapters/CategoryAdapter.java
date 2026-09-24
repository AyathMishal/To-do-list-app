package com.example.todolistapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    public static class CategoryItem {
        public String name;
        public int taskCount;
        public int iconResId;

        public CategoryItem(String name, int taskCount, int iconResId) {
            this.name = name;
            this.taskCount = taskCount;
            this.iconResId = iconResId;
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(CategoryItem category);
    }

    private final List<CategoryItem> categories;
    private final OnCategoryClickListener listener;

    public CategoryAdapter(List<CategoryItem> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryItem item = categories.get(position);
        holder.tvCategoryName.setText(item.name);
        holder.tvCategoryTaskCount.setText(item.taskCount + " Tasks");
        holder.ivCategoryIcon.setImageResource(item.iconResId);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategoryIcon;
        TextView tvCategoryName, tvCategoryTaskCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvCategoryTaskCount = itemView.findViewById(R.id.tvCategoryTaskCount);
        }
    }
}
