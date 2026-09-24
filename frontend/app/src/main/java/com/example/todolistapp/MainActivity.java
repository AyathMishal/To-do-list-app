package com.example.todolistapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.adapters.DateAdapter;
import com.example.todolistapp.adapters.TaskAdapter;
import com.example.todolistapp.models.Task;
import com.example.todolistapp.network.ApiClient;
import com.example.todolistapp.utils.DateUtils;
import com.example.todolistapp.utils.SessionManager;
import com.example.todolistapp.utils.TaskManager;
import com.example.todolistapp.utils.TimerDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextView tvCurrentHeaderDate, tvTaskCount;
    private Button btnAddNew, btnAdminDashboard;
    private ImageView ivTimer, ivLogout;
    private RecyclerView rvDates, rvTasks;
    private TaskAdapter taskAdapter;
    private DateAdapter dateAdapter;
    private SessionManager sessionManager;
    private TaskManager taskManager;
    private String selectedDate;
    private List<DateAdapter.DateModel> datesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);
        taskManager = TaskManager.getInstance(this);

        tvCurrentHeaderDate = findViewById(R.id.tvCurrentHeaderDate);
        tvTaskCount = findViewById(R.id.tvTaskCount);
        btnAddNew = findViewById(R.id.btnAddNew);
        btnAdminDashboard = findViewById(R.id.btnAdminDashboard);
        ivTimer = findViewById(R.id.ivTimer);
        ivLogout = findViewById(R.id.ivLogout);
        rvDates = findViewById(R.id.rvDates);
        rvTasks = findViewById(R.id.rvTasks);

        selectedDate = DateUtils.getTodayFormatted();
        tvCurrentHeaderDate.setText(selectedDate);

        if ("admin".equalsIgnoreCase(sessionManager.getUserRole())) {
            btnAdminDashboard.setVisibility(View.VISIBLE);
            btnAdminDashboard.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AdminActivity.class)));
        }

        btnAddNew.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ChooseActivity.class)));

        if (ivTimer != null) {
            ivTimer.setOnClickListener(v -> new TimerDialog(MainActivity.this).show());
        }

        if (ivLogout != null) {
            ivLogout.setOnClickListener(v -> {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Logout", (dialog, which) -> {
                            sessionManager.clearSession();
                            Toast.makeText(MainActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.putExtra("is_logout", true);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }

        setupDateStrip();
        setupTasksRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasksForDate(selectedDate);
    }

    private void setupDateStrip() {
        datesList = DateUtils.getPresentAndFutureDates(14);

        if (!datesList.isEmpty()) {
            selectedDate = datesList.get(0).fullDate;
            tvCurrentHeaderDate.setText(selectedDate);
        }

        dateAdapter = new DateAdapter(datesList, dateModel -> {
            selectedDate = dateModel.fullDate;
            tvCurrentHeaderDate.setText(selectedDate);
            loadTasksForDate(selectedDate);
        });

        rvDates.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvDates.setAdapter(dateAdapter);
    }

    private void setupTasksRecyclerView() {
        taskAdapter = new TaskAdapter(new ArrayList<>(), new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onTaskStatusChanged(Task task, boolean isCompleted) {
                taskManager.updateTask(task);
                updateTaskStatusOnServer(task);
                loadTasksForDate(selectedDate);
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

        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(taskAdapter);
    }

    private void confirmAndDeleteTask(Task task) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Delete Task")
                .setMessage("Do you want to delete '" + task.getTitle() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    taskManager.deleteTask(task.getId());
                    Toast.makeText(MainActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
                    loadTasksForDate(selectedDate);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadTasksForDate(String date) {
        List<Task> localTasks = taskManager.getTasksForDate(date);
        taskAdapter.setTasks(localTasks);
        tvTaskCount.setText(localTasks.size() + " Tasks");

        ApiClient.getApiService(this).getTasks(date).enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<Task> apiTasks = response.body();
                    taskAdapter.setTasks(apiTasks);
                    tvTaskCount.setText(apiTasks.size() + " Tasks");
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
            }
        });
    }

    private void updateTaskStatusOnServer(Task task) {
        ApiClient.getApiService(this).updateTask(task.getId(), task).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
            }
        });
    }
}
