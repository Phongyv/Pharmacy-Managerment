package com.example.pharmacymanagerment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class BmiCaculate extends AppCompatActivity {

    private EditText edtWeight, edtHeight;
    private TextView txtResult, txtCategory;
    private Button btnCalculate;
    private BarChart bmiChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_caculate);

        ImageView imageView38 = findViewById(R.id.imageView38);
        imageView38.setOnClickListener(v->{finish();});

        // Ánh xạ view
        edtWeight = findViewById(R.id.edtWeight);
        edtHeight = findViewById(R.id.edtHeight);
        txtResult = findViewById(R.id.txtResult);
        txtCategory = findViewById(R.id.txtCategory);
        btnCalculate = findViewById(R.id.btnCalculate);
        bmiChart = findViewById(R.id.bmiChart);

        btnCalculate.setOnClickListener(view -> calculateBMI());
    }

    private void calculateBMI() {
        String weightStr = edtWeight.getText().toString();
        String heightStr = edtHeight.getText().toString();

        if (weightStr.isEmpty() || heightStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            float weight = Float.parseFloat(weightStr);
            float height = Float.parseFloat(heightStr) / 100;

            if (weight <= 0 || height <= 0) {
                Toast.makeText(this, "Dữ liệu không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }

            float bmi = weight / (height * height);
            txtResult.setText(String.format("Chỉ số BMI: %.2f", bmi));

            String category = getBMICategory(bmi);
            txtCategory.setText(category);

            // Hiệu ứng mờ dần khi hiển thị kết quả
            AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
            fadeIn.setDuration(1000);
            txtResult.startAnimation(fadeIn);
            txtCategory.startAnimation(fadeIn);

            // Hiển thị BMI trên biểu đồ
            updateChart(bmi);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Dữ liệu nhập vào không hợp lệ!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getBMICategory(float bmi) {
        if (bmi < 18.5) return "Gầy";
        else if (bmi < 24.9) return "Bình thường";
        else if (bmi < 29.9) return "Thừa cân";
        else return "Béo phì";
    }

    private void updateChart(float bmi) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 18.5f));  // Mức gầy
        entries.add(new BarEntry(1, 24.9f));  // Mức bình thường
        entries.add(new BarEntry(2, 29.9f));  // Mức thừa cân
        entries.add(new BarEntry(3, 35.0f));  // Mức béo phì
        entries.add(new BarEntry(4, bmi));    // BMI của người dùng

        BarDataSet dataSet = new BarDataSet(entries, "Chỉ số BMI");
        dataSet.setColors(new int[]{Color.BLUE, Color.GREEN, Color.YELLOW, Color.RED, Color.MAGENTA});
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);

        BarData barData = new BarData(dataSet);
        bmiChart.setData(barData);

        // Cấu hình trục X và Y
        XAxis xAxis = bmiChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);

        YAxis leftAxis = bmiChart.getAxisLeft();
        leftAxis.setAxisMinimum(10f);
        leftAxis.setAxisMaximum(40f);
        leftAxis.setGranularity(5f);

        bmiChart.getAxisRight().setEnabled(false);
        bmiChart.setFitBars(true);
        bmiChart.animateY(1000);
        bmiChart.invalidate();
    }
}
