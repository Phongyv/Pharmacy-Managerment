package com.example.pharmacymanagerment;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class BloodSugar extends AppCompatActivity {

    private EditText sugarLevelEditText;
    private Spinner measurementTimeSpinner;
    private Button saveButton;
    private TextView resultTextView;
    private RecyclerView historyRecyclerView;
    private BloodSugarAdapter adapter;
    private List<BloodSugarRecord> recordList = new ArrayList<>();
    private AppDatabase db;
    private BloodSugarDao bloodSugarDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_sugar);

        // Initialize views
        sugarLevelEditText = findViewById(R.id.sugarLevelEditText);
        measurementTimeSpinner = findViewById(R.id.measurementTimeSpinner);
        saveButton = findViewById(R.id.saveButton);
        resultTextView = findViewById(R.id.resultTextView);
        historyRecyclerView = findViewById(R.id.historyRecyclerView);

        // Initialize database
        db = AppDatabase.getInstance(this);
        bloodSugarDao = db.bloodSugarDao();

        // Setup Spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.measurement_times, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        measurementTimeSpinner.setAdapter(spinnerAdapter);

        // Setup RecyclerView
        adapter = new BloodSugarAdapter(recordList);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyRecyclerView.setAdapter(adapter);

        // Load data
        loadBloodSugarData();

        saveButton.setOnClickListener(v -> saveBloodSugar());
    }

    private void loadBloodSugarData() {
        new Thread(() -> {
            List<BloodSugarRecord> records = bloodSugarDao.getAllRecords();
            runOnUiThread(() -> {
                recordList.clear();
                recordList.addAll(records);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }

    private void saveBloodSugar() {
        String sugarLevelStr = sugarLevelEditText.getText().toString();
        String measurementTime = measurementTimeSpinner.getSelectedItem().toString();

        if (sugarLevelStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập chỉ số đường huyết", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            float sugarLevel = Float.parseFloat(sugarLevelStr);
            String status = getBloodSugarStatus(sugarLevel, measurementTime);
            Date currentDate = new Date();

            // Create new record
            BloodSugarRecord record = new BloodSugarRecord(
                    sugarLevel, measurementTime, status, currentDate);

            new Thread(() -> {
                bloodSugarDao.insert(record);
                runOnUiThread(() -> {
                    recordList.add(0, record);
                    adapter.notifyItemInserted(0);
                    historyRecyclerView.smoothScrollToPosition(0);

                    // Show result
                    resultTextView.setText(String.format("Kết quả: %.1f mg/dL - %s (%s)",
                            sugarLevel, status, measurementTime));
                    resultTextView.setTextColor(getStatusColor(status));

                    // Clear input
                    sugarLevelEditText.setText("");
                });
            }).start();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }

    private String getBloodSugarStatus(float sugarLevel, String measurementTime) {
        if (measurementTime.equals("Sáng đói")) {
            if (sugarLevel < 70) return "Hạ đường huyết";
            if (sugarLevel < 100) return "Bình thường";
            if (sugarLevel < 126) return "Tiền tiểu đường";
            return "Tiểu đường";
        } else if (measurementTime.equals("Sau ăn 2h")) {
            if (sugarLevel < 70) return "Hạ đường huyết";
            if (sugarLevel < 140) return "Bình thường";
            if (sugarLevel < 200) return "Tiền tiểu đường";
            return "Tiểu đường";
        } else {
            if (sugarLevel < 70) return "Hạ đường huyết";
            if (sugarLevel < 140) return "Bình thường";
            if (sugarLevel < 200) return "Tiền tiểu đường";
            return "Tiểu đường";
        }
    }

    private int getStatusColor(String status) {
        switch (status) {
            case "Bình thường": return getResources().getColor(R.color.green);
            case "Tiền tiểu đường": return getResources().getColor(R.color.orange);
            case "Tiểu đường": return getResources().getColor(R.color.red);
            case "Hạ đường huyết": return getResources().getColor(R.color.gray);
            default: return getResources().getColor(R.color.black);
        }
    }
}
