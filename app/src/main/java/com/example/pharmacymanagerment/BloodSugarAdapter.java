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

public class BloodSugarAdapter extends RecyclerView.Adapter<BloodSugarAdapter.ViewHolder> {

    private List<BloodSugarRecord> recordList;

    public BloodSugarAdapter(List<BloodSugarRecord> recordList) {
        this.recordList = recordList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_blood_sugar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BloodSugarRecord record = recordList.get(position);

        holder.sugarLevelTextView.setText(String.format("%.1f mg/dL", record.getSugarLevel()));
        holder.statusTextView.setText(record.getStatus());
        holder.measurementTimeTextView.setText(record.getMeasurementTime());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.dateTextView.setText(sdf.format(record.getDate()));

        // Set color based on status
        int color = getStatusColor(holder.itemView, record.getStatus());
        holder.sugarLevelTextView.setTextColor(color);
        holder.statusTextView.setTextColor(color);
    }

    private int getStatusColor(View view, String status) {
        switch (status) {
            case "Bình thường": return view.getContext().getResources().getColor(R.color.green);
            case "Tiền tiểu đường": return view.getContext().getResources().getColor(R.color.orange);
            case "Tiểu đường": return view.getContext().getResources().getColor(R.color.red);
            case "Hạ đường huyết": return view.getContext().getResources().getColor(R.color.app_color);
            default: return view.getContext().getResources().getColor(R.color.black);
        }
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView sugarLevelTextView, statusTextView, measurementTimeTextView, dateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sugarLevelTextView = itemView.findViewById(R.id.sugarLevelTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            measurementTimeTextView = itemView.findViewById(R.id.measurementTimeTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}