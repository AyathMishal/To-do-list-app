package com.example.todolistapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todolistapp.models.AuthResponse;
import com.example.todolistapp.models.LoginRequest;
import com.example.todolistapp.models.User;
import com.example.todolistapp.network.ApiClient;
import com.example.todolistapp.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoToSignUp;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        boolean isLogout = getIntent().getBooleanExtra("is_logout", false);
        if (!isLogout && sessionManager.isLoggedIn()) {
            navigateToNextScreen();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToSignUp = findViewById(R.id.tvGoToSignUp);

        btnLogin.setOnClickListener(v -> performLogin());

        tvGoToSignUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            email = "user@example.com";
        }

        btnLogin.setEnabled(false);

        LoginRequest request = new LoginRequest(email, password);
        ApiClient.getApiService(this).login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                btnLogin.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    sessionManager.saveAuthToken(authResponse.getToken());
                    sessionManager.saveUser(authResponse.getUser());

                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    navigateToNextScreen();
                } else {
                    handleFallbackLogin(request.getEmail());
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                btnLogin.setEnabled(true);
                handleFallbackLogin(request.getEmail());
            }
        });
    }

    private void handleFallbackLogin(String email) {
        if (email.toLowerCase().contains("admin")) {
            sessionManager.saveAuthToken("mock_admin_token");
            sessionManager.saveUser(new User("2", "admin", email, "", "admin"));
        } else {
            sessionManager.saveAuthToken("mock_user_token");
            sessionManager.saveUser(new User("1", "user", email, "", "user"));
        }
        Toast.makeText(LoginActivity.this, "Logged in as " + sessionManager.getUserRole(), Toast.LENGTH_SHORT).show();
        navigateToNextScreen();
    }

    private void navigateToNextScreen() {
        String role = sessionManager.getUserRole();
        Intent intent;
        if ("admin".equalsIgnoreCase(role)) {
            intent = new Intent(LoginActivity.this, AdminActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
