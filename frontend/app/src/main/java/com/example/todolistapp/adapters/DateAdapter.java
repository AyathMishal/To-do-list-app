package com.example.todolistapp.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolistapp.R;

import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder> {

    public static class DateModel {
        public String dayNumber;
        public String dayName;
        public String fullDate;

        public DateModel(String dayNumber, String dayName, String fullDate) {
            this.dayNumber = dayNumber;
            this.dayName = dayName;
            this.fullDate = fullDate;
        }
    }

    public interface OnDateSelectedListener {
        void onDateSelected(DateModel dateModel);
    }

    private final List<DateModel> dateList;
    private int selectedPosition = 0;
    private final OnDateSelectedListener listener;

    public DateAdapter(List<DateModel> dateList, OnDateSelectedListener listener) {
        this.dateList = dateList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DateModel item = dateList.get(position);
        holder.tvDateDayNumber.setText(item.dayNumber);
        holder.tvDateDayName.setText(item.dayName);

        boolean isSelected = (position == selectedPosition);
        if (isSelected) {
            holder.itemView.setBackgroundResource(R.drawable.bg_date_selected);
            holder.tvDateDayNumber.setTextColor(Color.WHITE);
            holder.tvDateDayName.setTextColor(Color.WHITE);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_date_unselected);
            holder.tvDateDayNumber.setTextColor(Color.parseColor("#2B2D42"));
            holder.tvDateDayName.setTextColor(Color.parseColor("#8D99AE"));
        }

        holder.itemView.setOnClickListener(v -> {
            int prevPos = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(prevPos);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onDateSelected(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dateList != null ? dateList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDateDayNumber, tvDateDayName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateDayNumber = itemView.findViewById(R.id.tvDateDayNumber);
            tvDateDayName = itemView.findViewById(R.id.tvDateDayName);
        }
    }
}
