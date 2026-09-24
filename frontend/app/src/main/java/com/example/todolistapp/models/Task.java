package com.example.todolistapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Task {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("timeRange")
    private String timeRange; // e.g., "6:00 - 7:30"

    @SerializedName("category")
    private String category; // e.g., "Idea", "Food", "Work", "Sport", "Music"

    @SerializedName("date")
    private String date; // e.g., "14 Sept" or "2026-09-14"

    @SerializedName("completed")
    private boolean completed;

    @SerializedName("userId")
    private String userId;

    public Task() {
    }

    public Task(String id, String title, String description, String timeRange, String category, String date, boolean completed, String userId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.timeRange = timeRange;
        this.category = category;
        this.date = date;
        this.completed = completed;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return completed == task.completed &&
                Objects.equals(id, task.id) &&
                Objects.equals(title, task.title) &&
                Objects.equals(category, task.category) &&
                Objects.equals(date, task.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, category, date, completed);
    }
}
