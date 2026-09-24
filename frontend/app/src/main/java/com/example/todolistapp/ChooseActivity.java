package com.example.todolistapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.adapters.CategoryAdapter;
import com.example.todolistapp.adapters.DateAdapter;
import com.example.todolistapp.adapters.TaskAdapter;
import com.example.todolistapp.models.Task;
import com.example.todolistapp.utils.DateUtils;
import com.example.todolistapp.utils.TaskManager;
import com.example.todolistapp.utils.TimerDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ChooseActivity extends AppCompatActivity {

    private RecyclerView rvChooseDates, rvCategories, rvChooseTasks;
    private FloatingActionButton fabCreateTask;
    private ImageView ivChooseMenu, ivChooseTimer;
    private TextView tvChooseTasksHeader;
    private TaskManager taskManager;
    private TaskAdapter taskAdapter;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        taskManager = TaskManager.getInstance(this);
        selectedDate = DateUtils.getTodayFormatted();

        rvChooseDates = findViewById(R.id.rvChooseDates);
        rvCategories = findViewById(R.id.rvCategories);
        rvChooseTasks = findViewById(R.id.rvChooseTasks);
        fabCreateTask = findViewById(R.id.fabCreateTask);
        ivChooseMenu = findViewById(R.id.ivChooseMenu);
        ivChooseTimer = findViewById(R.id.ivChooseTimer);
        tvChooseTasksHeader = findViewById(R.id.tvChooseTasksHeader);

        ivChooseMenu.setOnClickListener(v -> finish());

        if (ivChooseTimer != null) {
            ivChooseTimer.setOnClickListener(v -> new TimerDialog(ChooseActivity.this).show());
        }

        setupDateStrip();
        setupTasksRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupCategories();
        loadTasksForSelectedDate();
    }

    private void setupDateStrip() {
        List<DateAdapter.DateModel> dates = DateUtils.getPresentAndFutureDates(14);
        if (!dates.isEmpty()) {
            selectedDate = dates.get(0).fullDate;
        }

        DateAdapter adapter = new DateAdapter(dates, dateModel -> {
            selectedDate = dateModel.fullDate;
            loadTasksForSelectedDate();
        });

        rvChooseDates.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvChooseDates.setAdapter(adapter);
    }

    private void setupCategories() {
        List<CategoryAdapter.CategoryItem> categories = new ArrayList<>();
        categories.add(new CategoryAdapter.CategoryItem("Idea", taskManager.getCategoryTaskCount("Idea"), R.drawable.ic_idea));
        categories.add(new CategoryAdapter.CategoryItem("Food", taskManager.getCategoryTaskCount("Food"), R.drawable.ic_food));
        categories.add(new CategoryAdapter.CategoryItem("Work", taskManager.getCategoryTaskCount("Work"), R.drawable.ic_work));
        categories.add(new CategoryAdapter.CategoryItem("Sport", taskManager.getCategoryTaskCount("Sport"), R.drawable.ic_sport));
        categories.add(new CategoryAdapter.CategoryItem("Music", taskManager.getCategoryTaskCount("Music"), R.drawable.ic_music));

        CategoryAdapter adapter = new CategoryAdapter(categories, category -> {
            Intent intent = new Intent(ChooseActivity.this, CreateTaskActivity.class);
            intent.putExtra("selected_category", category.name);
            startActivity(intent);
        });

        rvCategories.setLayoutManager(new LinearLayoutManager(this));
        rvCategories.setAdapter(adapter);

        fabCreateTask.setOnClickListener(v -> {
            Intent intent = new Intent(ChooseActivity.this, CreateTaskActivity.class);
            startActivity(intent);
        });
    }

    private void setupTasksRecyclerView() {
        taskAdapter = new TaskAdapter(new ArrayList<>(), new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onTaskStatusChanged(Task task, boolean isCompleted) {
                taskManager.updateTask(task);
                loadTasksForSelectedDate();
                setupCategories();
            }

            @Override
            public void onTaskDeleteClicked(Task task) {
                confirmAndDeleteTask(task);
            }

            @Override
            public void onTaskLongClicked(Task task) {
                confirmAndDeleteTask(task);
            }
        });

        rvChooseTasks.setLayoutManager(new LinearLayoutManager(this));
        rvChooseTasks.setAdapter(taskAdapter);
    }

    private void loadTasksForSelectedDate() {
        List<Task> tasks = taskManager.getTasksForDate(selectedDate);
        taskAdapter.setTasks(tasks);
        if (tvChooseTasksHeader != null) {
            tvChooseTasksHeader.setText("Tasks for " + selectedDate + " (" + tasks.size() + ")");
        }
    }

    private void confirmAndDeleteTask(Task task) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Task")
                .setMessage("Delete '" + task.getTitle() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    taskManager.deleteTask(task.getId());
                    loadTasksForSelectedDate();
                    setupCategories();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
