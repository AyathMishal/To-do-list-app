package com.example.todolistapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.R;
import com.example.todolistapp.models.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    public interface OnTaskClickListener {
        void onTaskStatusChanged(Task task, boolean isCompleted);
        void onTaskDeleteClicked(Task task);
        void onTaskLongClicked(Task task);
    }

    private List<Task> tasks;
    private final OnTaskClickListener listener;

    public TaskAdapter(List<Task> tasks, OnTaskClickListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.tvTaskTime.setText(task.getTimeRange() != null ? task.getTimeRange() : "");
        holder.tvTaskTitle.setText(task.getTitle() != null ? task.getTitle() : task.getCategory());
        holder.tvTaskDescription.setText(task.getDescription() != null ? task.getDescription() : "");

        holder.cbTaskCompleted.setOnCheckedChangeListener(null);
        holder.cbTaskCompleted.setChecked(task.isCompleted());

        holder.cbTaskCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            if (listener != null) {
                listener.onTaskStatusChanged(task, isChecked);
            }
        });

        if (holder.btnDeleteTask != null) {
            holder.btnDeleteTask.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskDeleteClicked(task);
                }
            });
        }

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onTaskLongClicked(task);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return tasks != null ? tasks.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskTime, tvTaskTitle, tvTaskDescription;
        CheckBox cbTaskCompleted;
        ImageView btnDeleteTask;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTime = itemView.findViewById(R.id.tvTaskTime);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvTaskDescription = itemView.findViewById(R.id.tvTaskDescription);
            cbTaskCompleted = itemView.findViewById(R.id.cbTaskCompleted);
            btnDeleteTask = itemView.findViewById(R.id.btnDeleteTask);
        }
    }
}
