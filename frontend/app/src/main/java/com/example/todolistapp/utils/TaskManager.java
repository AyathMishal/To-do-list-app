package com.example.todolistapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.todolistapp.models.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskManager {

    private static final String PREF_NAME = "TodoListTasksPref";
    private static final String KEY_TASKS = "tasks_list";

    private static TaskManager instance;
    private final SharedPreferences prefs;
    private final Gson gson;

    private TaskManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        initDefaultTasksIfEmpty();
    }

    public static synchronized TaskManager getInstance(Context context) {
        if (instance == null) {
            instance = new TaskManager(context);
        }
        return instance;
    }

    private void initDefaultTasksIfEmpty() {
        List<Task> tasks = getAllTasksRaw();
        if (tasks.isEmpty()) {
            String today = DateUtils.getTodayFormatted();
            tasks.add(new Task("1", "Fitness", "Exercise and gym", "06:00 - 07:30", "Sport", today, true, "u1"));
            tasks.add(new Task("2", "Check Emails and sms", "Review and respond to emails and SMS", "07:30 - 08:00", "Work", today, true, "u1"));
            tasks.add(new Task("3", "Work on Projects", "Focus on all the tasks related to Project", "08:00 - 10:00", "Work", today, true, "u1"));
            tasks.add(new Task("4", "Attend Meeting", "Team meeting with the client ABC", "10:00 - 11:00", "Work", today, false, "u1"));
            tasks.add(new Task("5", "Work of XYZ", "Change theme and ideas in XYZ", "11:00 - 13:00", "Idea", today, false, "u1"));
            tasks.add(new Task("6", "Lunch Break", "Enjoy a healthy lunch and take some rest", "13:00 - 14:30", "Food", today, false, "u1"));
            saveAllTasks(tasks);
        }
    }

    private synchronized List<Task> getAllTasksRaw() {
        String json = prefs.getString(KEY_TASKS, null);
        if (json == null || json.isEmpty()) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<Task>>() {}.getType();
        List<Task> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    public synchronized List<Task> getAllTasks() {
        List<Task> rawTasks = getAllTasksRaw();
        List<Task> validTasks = new ArrayList<>();
        boolean hasExpired = false;

        for (Task task : rawTasks) {
            if (DateUtils.isTaskExpired(task.getDate(), task.getTimeRange())) {
                hasExpired = true;
            } else {
                validTasks.add(task);
            }
        }

        if (hasExpired) {
            saveAllTasks(validTasks);
        }

        return validTasks;
    }

    public synchronized void saveAllTasks(List<Task> tasks) {
        String json = gson.toJson(tasks);
        prefs.edit().putString(KEY_TASKS, json).apply();
    }

    public synchronized void addTask(Task task) {
        if (task.getId() == null || task.getId().isEmpty()) {
            task.setId(UUID.randomUUID().toString());
        }
        List<Task> tasks = getAllTasks();
        tasks.add(0, task);
        saveAllTasks(tasks);
    }

    public synchronized void updateTask(Task updatedTask) {
        List<Task> tasks = getAllTasks();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).getId() != null && tasks.get(i).getId().equals(updatedTask.getId())) {
                tasks.set(i, updatedTask);
                break;
            }
        }
        saveAllTasks(tasks);
    }

    public synchronized void deleteTask(String taskId) {
        List<Task> tasks = getAllTasks();
        List<Task> updated = new ArrayList<>();
        for (Task task : tasks) {
            if (!taskId.equalsIgnoreCase(task.getId())) {
                updated.add(task);
            }
        }
        saveAllTasks(updated);
    }

    public synchronized List<Task> getTasksForDate(String date) {
        List<Task> all = getAllTasks();
        List<Task> filtered = new ArrayList<>();
        if (date == null) return all;

        for (Task task : all) {
            if (date.equalsIgnoreCase(task.getDate()) || isDateMatching(date, task.getDate())) {
                filtered.add(task);
            }
        }
        return filtered;
    }

    private boolean isDateMatching(String targetDate, String taskDate) {
        if (taskDate == null) return false;
        String t1 = targetDate.replaceAll("\\s+", "").toLowerCase();
        String t2 = taskDate.replaceAll("\\s+", "").toLowerCase();
        return t1.contains(t2) || t2.contains(t1);
    }

    public synchronized int getCategoryTaskCount(String category) {
        List<Task> all = getAllTasks();
        int count = 0;
        for (Task task : all) {
            if (category.equalsIgnoreCase(task.getCategory())) {
                count++;
            }
        }
        return count;
    }
}
