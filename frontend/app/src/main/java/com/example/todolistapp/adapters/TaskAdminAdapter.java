package com.example.todolistapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.R;
import com.example.todolistapp.models.Task;

import java.util.List;

public class TaskAdminAdapter extends RecyclerView.Adapter<TaskAdminAdapter.ViewHolder> {

    private List<Task> tasks;

    public TaskAdminAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.tvAdminTaskTitle.setText(task.getTitle() != null ? task.getTitle() : task.getCategory());
        String info = "Category: " + task.getCategory() + " | User: " + (task.getUserId() != null ? task.getUserId() : "N/A");
        holder.tvAdminTaskCategoryUser.setText(info);
    }

    @Override
    public int getItemCount() {
        return tasks != null ? tasks.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAdminTaskTitle, tvAdminTaskCategoryUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAdminTaskTitle = itemView.findViewById(R.id.tvAdminTaskTitle);
            tvAdminTaskCategoryUser = itemView.findViewById(R.id.tvAdminTaskCategoryUser);
        }
    }
}
