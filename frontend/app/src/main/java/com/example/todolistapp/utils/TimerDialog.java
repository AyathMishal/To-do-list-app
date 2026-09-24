package com.example.todolistapp.utils;

import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.todolistapp.R;

import java.util.Locale;

public class TimerDialog extends Dialog {

    private TextView tvTimerDisplay;
    private Button btnStartTimer, btnResetTimer, btnMode25, btnMode15, btnMode5;
    private ImageView ivCloseTimer;

    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long totalTimeInMillis = 25 * 60 * 1000L; // Default 25 min
    private long timeRemainingInMillis = totalTimeInMillis;
    private ToneGenerator toneGenerator;

    public TimerDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_timer);

        if (getWindow() != null) {
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        tvTimerDisplay = findViewById(R.id.tvTimerDisplay);
        btnStartTimer = findViewById(R.id.btnStartTimer);
        btnResetTimer = findViewById(R.id.btnResetTimer);
        btnMode25 = findViewById(R.id.btnMode25);
        btnMode15 = findViewById(R.id.btnMode15);
        btnMode5 = findViewById(R.id.btnMode5);
        ivCloseTimer = findViewById(R.id.ivCloseTimer);

        ivCloseTimer.setOnClickListener(v -> {
            pauseTimer();
            dismiss();
        });

        btnMode25.setOnClickListener(v -> setTimerDuration(25, btnMode25));
        btnMode15.setOnClickListener(v -> setTimerDuration(15, btnMode15));
        btnMode5.setOnClickListener(v -> setTimerDuration(5, btnMode5));

        btnStartTimer.setOnClickListener(v -> {
            if (isTimerRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        btnResetTimer.setOnClickListener(v -> resetTimer());

        updateTimerDisplay();
    }

    private void setTimerDuration(int minutes, Button selectedBtn) {
        pauseTimer();
        totalTimeInMillis = minutes * 60 * 1000L;
        timeRemainingInMillis = totalTimeInMillis;

        btnMode25.setBackgroundResource(R.drawable.bg_date_unselected);
        btnMode15.setBackgroundResource(R.drawable.bg_date_unselected);
        btnMode5.setBackgroundResource(R.drawable.bg_date_unselected);

        btnMode25.setTextColor(getContext().getColor(R.color.text_dark));
        btnMode15.setTextColor(getContext().getColor(R.color.text_dark));
        btnMode5.setTextColor(getContext().getColor(R.color.text_dark));

        selectedBtn.setBackgroundResource(R.drawable.bg_date_selected);
        selectedBtn.setTextColor(getContext().getColor(R.color.white));

        updateTimerDisplay();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeRemainingInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeRemainingInMillis = millisUntilFinished;
                updateTimerDisplay();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                btnStartTimer.setText("Start");
                timeRemainingInMillis = totalTimeInMillis;
                updateTimerDisplay();

                playAlarmSound();
                Toast.makeText(getContext(), "Focus Time Finished!", Toast.LENGTH_LONG).show();
            }
        }.start();

        isTimerRunning = true;
        btnStartTimer.setText("Pause");
    }

    private void playAlarmSound() {
        try {
            if (toneGenerator == null) {
                toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
            }
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 5000); // Beep alarm for 5 seconds

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (toneGenerator != null) {
                    toneGenerator.stopTone();
                }
            }, 5000);
        } catch (Exception ignored) {
        }
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (toneGenerator != null) {
            toneGenerator.stopTone();
        }
        isTimerRunning = false;
        btnStartTimer.setText("Start");
    }

    private void resetTimer() {
        pauseTimer();
        timeRemainingInMillis = totalTimeInMillis;
        updateTimerDisplay();
    }

    private void updateTimerDisplay() {
        int minutes = (int) (timeRemainingInMillis / 1000) / 60;
        int seconds = (int) (timeRemainingInMillis / 1000) % 60;
        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tvTimerDisplay.setText(formattedTime);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        pauseTimer();
        if (toneGenerator != null) {
            toneGenerator.release();
            toneGenerator = null;
        }
    }
}
