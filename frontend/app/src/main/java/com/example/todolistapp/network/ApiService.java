package com.example.todolistapp.network;

import com.example.todolistapp.models.AuthResponse;
import com.example.todolistapp.models.LoginRequest;
import com.example.todolistapp.models.RegisterRequest;
import com.example.todolistapp.models.Task;
import com.example.todolistapp.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("api/auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @POST("api/auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @GET("api/tasks")
    Call<List<Task>> getTasks(@Query("date") String date);

    @POST("api/tasks")
    Call<Task> createTask(@Body Task task);

    @PUT("api/tasks/{id}")
    Call<Task> updateTask(@Path("id") String id, @Body Task task);

    @DELETE("api/tasks/{id}")
    Call<Void> deleteTask(@Path("id") String id);

    @GET("api/admin/users")
    Call<List<User>> getAllUsers();

    @GET("api/admin/tasks")
    Call<List<Task>> getAllGlobalTasks();
}
