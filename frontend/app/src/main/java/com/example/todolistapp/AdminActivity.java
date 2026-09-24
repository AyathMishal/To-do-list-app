package com.example.todolistapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.adapters.TaskAdminAdapter;
import com.example.todolistapp.adapters.UserAdminAdapter;
import com.example.todolistapp.models.Task;
import com.example.todolistapp.models.User;
import com.example.todolistapp.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView rvAdminUsers, rvAdminTasks;
    private UserAdminAdapter userAdapter;
    private TaskAdminAdapter taskAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        rvAdminUsers = findViewById(R.id.rvAdminUsers);
        rvAdminTasks = findViewById(R.id.rvAdminTasks);

        userAdapter = new UserAdminAdapter(new ArrayList<>());
        rvAdminUsers.setLayoutManager(new LinearLayoutManager(this));
        rvAdminUsers.setAdapter(userAdapter);

        taskAdapter = new TaskAdminAdapter(new ArrayList<>());
        rvAdminTasks.setLayoutManager(new LinearLayoutManager(this));
        rvAdminTasks.setAdapter(taskAdapter);

        fetchAdminData();
    }

    private void fetchAdminData() {
        ApiClient.getApiService(this).getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userAdapter.setUsers(response.body());
                } else {
                    loadSampleAdminUsers();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                loadSampleAdminUsers();
            }
        });

        ApiClient.getApiService(this).getAllGlobalTasks().enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    taskAdapter.setTasks(response.body());
                } else {
                    loadSampleAdminTasks();
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                loadSampleAdminTasks();
            }
        });
    }

    private void loadSampleAdminUsers() {
        List<User> users = new ArrayList<>();
        users.add(new User("1", "john_doe", "john@example.com", "", "user"));
        users.add(new User("2", "admin_user", "admin@example.com", "", "admin"));
        userAdapter.setUsers(users);
    }

    private void loadSampleAdminTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task("1", "Fitness", "Gym", "6:00 - 7:30", "Sport", "14 Sept", true, "1"));
        tasks.add(new Task("2", "Work on Projects", "Coding", "8:00 - 10:00", "Work", "14 Sept", true, "1"));
        tasks.add(new Task("3", "Admin Task Review", "Review users", "10:00 - 11:00", "Work", "14 Sept", false, "2"));
        taskAdapter.setTasks(tasks);
    }
}
