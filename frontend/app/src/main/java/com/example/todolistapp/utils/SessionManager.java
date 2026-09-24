package com.example.todolistapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.todolistapp.models.User;

public class SessionManager {
    private static final String PREF_NAME = "TodoListAppPref";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_ROLE = "user_role";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveAuthToken(String token) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply();
    }

    public String fetchAuthToken() {
        return prefs.getString(KEY_AUTH_TOKEN, null);
    }

    public void saveUser(User user) {
        if (user == null) return;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getUsername());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_ROLE, user.getRole());
        editor.apply();
    }

    public User getUser() {
        String id = prefs.getString(KEY_USER_ID, null);
        if (id == null) return null;
        String name = prefs.getString(KEY_USER_NAME, "");
        String email = prefs.getString(KEY_USER_EMAIL, "");
        String role = prefs.getString(KEY_USER_ROLE, "user");
        return new User(id, name, email, "", role);
    }

    public String getUserRole() {
        return prefs.getString(KEY_USER_ROLE, "user");
    }

    public boolean isLoggedIn() {
        return fetchAuthToken() != null;
    }

    public void clearSession() {
        prefs.edit().clear().apply();
    }
}
