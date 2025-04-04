package com.example.pharmacymanagerment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BloodPressureAdapter extends RecyclerView.Adapter<BloodPressureAdapter.ViewHolder> {

    private List<BloodPressureRecord> recordList;

    public BloodPressureAdapter(List<BloodPressureRecord> recordList) {
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_blood_pressure, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BloodPressureRecord record = recordList.get(position);

        holder.bpValueTextView.setText(String.format("%d/%d",
                record.getSystolic(), record.getDiastolic()));
        holder.bpStatusTextView.setText(record.getStatus());
        holder.heartRateTextView.setText(String.valueOf(record.getHeartRate()));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.dateTextView.setText(sdf.format(record.getDate()));

        // Set color based on status
        int color = getStatusColor(holder.itemView, record.getStatus());
        holder.bpValueTextView.setTextColor(color);
        holder.bpStatusTextView.setTextColor(color);
    }

    private int getStatusColor(View view, String status) {
        switch (status) {
            case "Bình thường": return view.getContext().getResources().getColor(R.color.green);
            case "Bình thường cao": return view.getContext().getResources().getColor(R.color.orange_light);
            case "Huyết áp thấp":
            case "Tăng huyết áp độ 1": return view.getContext().getResources().getColor(R.color.orange);
            case "Tăng huyết áp độ 2":
            case "Tăng huyết áp khủng hoảng": return view.getContext().getResources().getColor(R.color.red);
            default: return view.getContext().getResources().getColor(R.color.black);
        }
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView bpValueTextView, bpStatusTextView, heartRateTextView, dateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bpValueTextView = itemView.findViewById(R.id.bpValueTextView);
            bpStatusTextView = itemView.findViewById(R.id.bpStatusTextView);
            heartRateTextView = itemView.findViewById(R.id.heartRateTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}