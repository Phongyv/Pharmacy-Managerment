package com.example.pharmacymanagerment;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PeriodWatch extends AppCompatActivity {

    private LineChart cycleChart;
    private TextView cycleInfoTextView;
    private TextView predictionTextView;
    private Button logPeriodButton;
    private Button viewHistoryButton;
    private Button setReminderButton;

    private AppDatabase db;
    private PeriodDao periodDao;
    private List<PeriodRecord> recordList = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_period_watch);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        cycleChart = findViewById(R.id.cycleChart);
        cycleInfoTextView = findViewById(R.id.cycleInfoTextView);
        predictionTextView = findViewById(R.id.predictionTextView);
        logPeriodButton = findViewById(R.id.logPeriodButton);
        viewHistoryButton = findViewById(R.id.viewHistoryButton);
        setReminderButton = findViewById(R.id.setReminderButton);

        // Initialize database
        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "period-db")
                .allowMainThreadQueries() // For simplicity, in production use background thread
                .build();
        periodDao = db.periodDao();

        // Load data
        loadPeriodData();

        // Set up buttons
        logPeriodButton.setOnClickListener(v -> showLogPeriodDialog());
        viewHistoryButton.setOnClickListener(v -> showHistoryDialog());
        setReminderButton.setOnClickListener(v -> showReminderTimePicker());
    }

    private void loadPeriodData() {
        new Thread(() -> {
            List<PeriodRecord> records = periodDao.getAllRecords();
            // Lọc bỏ các bản ghi có cycleLength không hợp lệ
            records.removeIf(record -> record.getCycleLength() <= 0);

            runOnUiThread(() -> {
                recordList.clear();
                recordList.addAll(records);
                updateCycleInfo();
                updateChart();
                predictNextPeriod();
            });
        }).start();
    }
    private void updateCycleInfo() {
        if (recordList.isEmpty()) {
            cycleInfoTextView.setText("Chưa có dữ liệu chu kỳ");
            return;
        }

        PeriodRecord lastRecord = recordList.get(0);
        String info = "Kỳ kinh gần nhất: " + dateFormat.format(lastRecord.getStartDate()) + " - " +
                dateFormat.format(lastRecord.getEndDate()) + "\n" +
                "Độ dài chu kỳ: " + lastRecord.getCycleLength() + " ngày\n" +
                "Độ dài kỳ kinh: " + lastRecord.getPeriodLength() + " ngày";

        cycleInfoTextView.setText(info);
    }

    private void updateChart() {
        if (recordList.size() < 2) {
            cycleChart.clear();
            cycleChart.setNoDataText("Cần ít nhất 2 chu kỳ để hiển thị biểu đồ");
            return;
        }

        List<Entry> cycleEntries = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM", Locale.getDefault());

        // Lấy 6 chu kỳ gần nhất để hiển thị
        int count = Math.min(6, recordList.size());
        for (int i = 0; i < count; i++) {
            PeriodRecord record = recordList.get(i);
            // Đảm bảo cycleLength là số dương
            if (record.getCycleLength() > 0) {
                cycleEntries.add(new Entry(i, record.getCycleLength()));
                dates.add(sdf.format(record.getStartDate()));
            }
        }

        // Kiểm tra lại nếu không có dữ liệu hợp lệ
        if (cycleEntries.isEmpty()) {
            cycleChart.clear();
            cycleChart.setNoDataText("Không có dữ liệu hợp lệ để hiển thị");
            return;
        }

        LineDataSet dataSet = new LineDataSet(cycleEntries, "Độ dài chu kỳ (ngày)");
        dataSet.setColor(getResources().getColor(android.R.color.holo_red_dark));
        dataSet.setValueTextColor(getResources().getColor(android.R.color.black));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setCircleColor(getResources().getColor(android.R.color.holo_red_dark));

        LineData lineData = new LineData(dataSet);
        cycleChart.setData(lineData);

        // Cấu hình trục X
        XAxis xAxis = cycleChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(dates.size());

        // Cấu hình biểu đồ
        cycleChart.getDescription().setEnabled(false);
        cycleChart.getLegend().setEnabled(false);
        cycleChart.getAxisRight().setEnabled(false);
        cycleChart.setVisibleXRangeMaximum(6); // Giới hạn hiển thị 6 điểm dữ liệu
        cycleChart.animateY(1000);
        cycleChart.invalidate();
    }
    private void predictNextPeriod() {
        if (recordList.isEmpty()) {
            predictionTextView.setText("Không thể dự đoán - Chưa có dữ liệu chu kỳ");
            return;
        }

        // Lọc các chu kỳ có độ dài hợp lệ
        List<PeriodRecord> validRecords = new ArrayList<>();
        for (PeriodRecord record : recordList) {
            if (record.getCycleLength() > 0) {
                validRecords.add(record);
            }
        }

        if (validRecords.isEmpty()) {
            predictionTextView.setText("Không có dữ liệu chu kỳ hợp lệ");
            return;
        }

        // Tính độ dài chu kỳ trung bình
        int total = 0;
        int count = Math.min(3, validRecords.size());
        for (int i = 0; i < count; i++) {
            total += validRecords.get(i).getCycleLength();
        }
        int averageCycle = total / count;

        // Dự đoán ngày bắt đầu kỳ kinh tiếp theo
        PeriodRecord lastRecord = validRecords.get(0);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastRecord.getStartDate());
        calendar.add(Calendar.DAY_OF_YEAR, averageCycle);

        String prediction = "Dự đoán kỳ kinh tiếp theo: " + dateFormat.format(calendar.getTime()) +
                "\n(Chu kỳ trung bình: " + averageCycle + " ngày)";

        predictionTextView.setText(prediction);
    }
    private void showLogPeriodDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ghi nhận kỳ kinh mới");

        View view = getLayoutInflater().inflate(R.layout.dialog_log_period, null);

        // Initialize views from dialog layout
        DatePicker startDatePicker = view.findViewById(R.id.startDatePicker);
        DatePicker endDatePicker = view.findViewById(R.id.endDatePicker);
        EditText symptomsEditText = view.findViewById(R.id.symptomsEditText);
        EditText moodEditText = view.findViewById(R.id.moodEditText);

        // Set current date as default
        Calendar calendar = Calendar.getInstance();
        startDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), null);
        endDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), null);

        builder.setView(view);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            // Get selected dates
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.set(startDatePicker.getYear(), startDatePicker.getMonth(),
                    startDatePicker.getDayOfMonth());

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.set(endDatePicker.getYear(), endDatePicker.getMonth(),
                    endDatePicker.getDayOfMonth());

            // Validate dates
            if (endCalendar.before(startCalendar)) {
                Toast.makeText(this, "Ngày kết thúc phải sau ngày bắt đầu", Toast.LENGTH_SHORT).show();
                return;
            }

            Date startDate = startCalendar.getTime();
            Date endDate = endCalendar.getTime();

            // Calculate period length
            long diff = endDate.getTime() - startDate.getTime();
            int periodLength = (int) (diff / (1000 * 60 * 60 * 24)) + 1;

            // Calculate cycle length
            int cycleLength = 28; // Default
            if (!recordList.isEmpty()) {
                PeriodRecord lastRecord = recordList.get(0);
                diff = startDate.getTime() - lastRecord.getStartDate().getTime();
                cycleLength = (int) (diff / (1000 * 60 * 60 * 24));
            }



            // Create new record
            PeriodRecord newRecord = new PeriodRecord(startDate, endDate, cycleLength, periodLength);
            newRecord.setSymptoms(symptomsEditText.getText().toString());
            newRecord.setMood(moodEditText.getText().toString());

            if (cycleLength <= 0) {
                Toast.makeText(this, "Độ dài chu kỳ phải là số dương", Toast.LENGTH_SHORT).show();
                return;
            }
            // Save to database
            periodDao.insert(newRecord);
            loadPeriodData();
            Toast.makeText(this, "Đã lưu kỳ kinh mới", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void showHistoryDialog() {
        if (recordList.isEmpty()) {
            Toast.makeText(this, "Chưa có dữ liệu lịch sử", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Lịch sử chu kỳ");

        StringBuilder historyText = new StringBuilder();
        for (PeriodRecord record : recordList) {
            historyText.append(dateFormat.format(record.getStartDate()))
                    .append(" - ")
                    .append(dateFormat.format(record.getEndDate()))
                    .append(" (")
                    .append(record.getCycleLength())
                    .append(" ngày)\n");
        }

        builder.setMessage(historyText.toString());
        builder.setPositiveButton("Đóng", null);
        builder.show();
    }

    private void showReminderTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> setPeriodReminder(hourOfDay, minute),
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
        timePickerDialog.setTitle("Chọn thời gian nhắc nhở hàng ngày");
        timePickerDialog.show();
    }

    private void setPeriodReminder(int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, PeriodReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Set the alarm to repeat daily
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);

        Toast.makeText(this, "Đã đặt lời nhắc hàng ngày lúc " + hour + ":" + minute, Toast.LENGTH_SHORT).show();
    }
}
