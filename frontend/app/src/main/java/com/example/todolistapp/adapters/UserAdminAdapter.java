package com.example.todolistapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.R;
import com.example.todolistapp.models.User;

import java.util.List;

public class UserAdminAdapter extends RecyclerView.Adapter<UserAdminAdapter.ViewHolder> {

    private List<User> users;

    public UserAdminAdapter(List<User> users) {
        this.users = users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.tvAdminUsername.setText(user.getUsername() != null ? user.getUsername() : "N/A");
        holder.tvAdminEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");
        holder.tvAdminRole.setText("Role: " + (user.getRole() != null ? user.getRole() : "user"));
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAdminUsername, tvAdminEmail, tvAdminRole;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAdminUsername = itemView.findViewById(R.id.tvAdminUsername);
            tvAdminEmail = itemView.findViewById(R.id.tvAdminEmail);
            tvAdminRole = itemView.findViewById(R.id.tvAdminRole);
        }
    }
}
