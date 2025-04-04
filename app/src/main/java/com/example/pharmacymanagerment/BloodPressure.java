package com.example.pharmacymanagerment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BloodPressure extends AppCompatActivity {

    private EditText systolicEditText, diastolicEditText, heartRateEditText;
    private Button saveButton;
    private TextView resultTextView;
    private RecyclerView historyRecyclerView;
    private BloodPressureAdapter adapter;
    private List<BloodPressureRecord> recordList = new ArrayList<>();
    private AppDatabase db;
    private BloodPressureDao bloodPressureDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure);

        // Initialize views
        systolicEditText = findViewById(R.id.systolicEditText);
        diastolicEditText = findViewById(R.id.diastolicEditText);
        heartRateEditText = findViewById(R.id.heartRateEditText);
        saveButton = findViewById(R.id.saveButton);
        resultTextView = findViewById(R.id.resultTextView);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);

        // Initialize database
        db = AppDatabase.getInstance(this);
        bloodPressureDao = db.bloodPressureDao();

        // Setup RecyclerView
        adapter = new BloodPressureAdapter(recordList);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyRecyclerView.setAdapter(adapter);

        // Load data
        loadBloodPressureData();

        saveButton.setOnClickListener(v -> saveBloodPressure());
    }

    private void loadBloodPressureData() {
        new Thread(() -> {
            List<BloodPressureRecord> records = bloodPressureDao.getAllRecords();
            runOnUiThread(() -> {
                recordList.clear();
                recordList.addAll(records);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    private void saveBloodPressure() {
        String systolicStr = systolicEditText.getText().toString();
        String diastolicStr = diastolicEditText.getText().toString();
        String heartRateStr = heartRateEditText.getText().toString();

        if (systolicStr.isEmpty() || diastolicStr.isEmpty() || heartRateStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int systolic = Integer.parseInt(systolicStr);
            int diastolic = Integer.parseInt(diastolicStr);
            int heartRate = Integer.parseInt(heartRateStr);

            String status = getBloodPressureStatus(systolic, diastolic);
            Date currentDate = new Date();

            // Create new record
            BloodPressureRecord record = new BloodPressureRecord(
                    systolic, diastolic, heartRate, status, currentDate);

            new Thread(() -> {
                bloodPressureDao.insert(record);
                runOnUiThread(() -> {
                    recordList.add(0, record);
                    adapter.notifyItemInserted(0);
                    historyRecyclerView.smoothScrollToPosition(0);

                    // Show result
                    resultTextView.setText(String.format("Kết quả: %d/%d mmHg - %s",
                            systolic, diastolic, status));
                    resultTextView.setTextColor(getStatusColor(status));

                    // Clear input
                    systolicEditText.setText("");
                    diastolicEditText.setText("");
                    heartRateEditText.setText("");
                });
            }).start();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private String getBloodPressureStatus(int systolic, int diastolic) {
        if (systolic < 90 || diastolic < 60) {
            return "Huyết áp thấp";
        } else if (systolic < 120 && diastolic < 80) {
            return "Bình thường";
        } else if (systolic < 130 && diastolic < 80) {
            return "Bình thường cao";
        } else if (systolic < 140 || diastolic < 90) {
            return "Tăng huyết áp độ 1";
        } else if (systolic < 180 || diastolic < 120) {
            return "Tăng huyết áp độ 2";
        } else {
            return "Tăng huyết áp khủng hoảng";
        }
    }

    private int getStatusColor(String status) {
        switch (status) {
            case "Bình thường": return getResources().getColor(R.color.green);
            case "Bình thường cao": return getResources().getColor(R.color.orange_light);
            case "Huyết áp thấp":
            case "Tăng huyết áp độ 1": return getResources().getColor(R.color.orange);
            case "Tăng huyết áp độ 2":
            case "Tăng huyết áp khủng hoảng": return getResources().getColor(R.color.red);
            default: return getResources().getColor(R.color.black);
        }
    }
}