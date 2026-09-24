package com.example.todolistapp;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todolistapp.models.Task;
import com.example.todolistapp.network.ApiClient;
import com.example.todolistapp.utils.DateUtils;
import com.example.todolistapp.utils.TaskManager;
import com.example.todolistapp.utils.TimerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTaskActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private Spinner spCategory;
    private EditText etTaskName, etTaskDescription;
    private Button btnStartTime, btnEndTime, btnSubmitCreateTask;
    private ImageView ivBack, ivCreateTimer;
    private String selectedDate;
    private String startTime = "06:00";
    private String endTime = "07:30";
    private TaskManager taskManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        taskManager = TaskManager.getInstance(this);

        calendarView = findViewById(R.id.calendarView);
        spCategory = findViewById(R.id.spCategory);
        etTaskName = findViewById(R.id.etTaskName);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        btnStartTime = findViewById(R.id.btnStartTime);
        btnEndTime = findViewById(R.id.btnEndTime);
        btnSubmitCreateTask = findViewById(R.id.btnSubmitCreateTask);
        ivBack = findViewById(R.id.ivBack);
        ivCreateTimer = findViewById(R.id.ivCreateTimer);

        ivBack.setOnClickListener(v -> finish());

        if (ivCreateTimer != null) {
            ivCreateTimer.setOnClickListener(v -> new TimerDialog(CreateTaskActivity.this).show());
        }

        selectedDate = DateUtils.getTodayFormatted();

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, dayOfMonth);
            selectedDate = sdf.format(cal.getTime());
        });

        btnStartTime.setOnClickListener(v -> showTimePicker(true));
        btnEndTime.setOnClickListener(v -> showTimePicker(false));

        String[] categories = new String[]{"Idea", "Food", "Work", "Sport", "Music"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spCategory.setAdapter(adapter);

        String passedCategory = getIntent().getStringExtra("selected_category");
        if (passedCategory != null) {
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equalsIgnoreCase(passedCategory)) {
                    spCategory.setSelection(i);
                    break;
                }
            }
        }

        btnSubmitCreateTask.setOnClickListener(v -> createNewTask());
    }

    private void showTimePicker(boolean isStart) {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, selectedMinute) -> {
            String timeStr = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, selectedMinute);
            if (isStart) {
                startTime = timeStr;
                btnStartTime.setText(startTime);
            } else {
                endTime = timeStr;
                btnEndTime.setText(endTime);
            }
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void createNewTask() {
        String name = etTaskName.getText().toString().trim();
        String description = etTaskDescription.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter a task name", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmitCreateTask.setEnabled(false);

        String timeRange = startTime + " - " + endTime;

        Task newTask = new Task(null, name, description, timeRange, category, selectedDate, false, "u1");
        taskManager.addTask(newTask);

        Toast.makeText(CreateTaskActivity.this, "Task created successfully!", Toast.LENGTH_SHORT).show();

        ApiClient.getApiService(this).createTask(newTask).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
            }
        });

        finish();
    }
}
