package com.example.pharmacymanagerment;

import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class BmrCaculate extends AppCompatActivity {

    private EditText edtWeight, edtHeight, edtAge;
    private RadioGroup radioGroupGender;
    private Spinner spinnerActivityLevel;
    private TextView txtBmrResult, txtDietRecommendation;
    private Button btnCalculateBMR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmr_caculate);

        // Ánh xạ View
        edtWeight = findViewById(R.id.edtWeight);
        edtHeight = findViewById(R.id.edtHeight);
        edtAge = findViewById(R.id.edtAge);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        spinnerActivityLevel = findViewById(R.id.spinnerActivityLevel);
        txtBmrResult = findViewById(R.id.txtBmrResult);
        txtDietRecommendation = findViewById(R.id.txtDietRecommendation);
        btnCalculateBMR = findViewById(R.id.btnCalculateBMR);

        ImageView imageView39 = findViewById(R.id.imageView39);
        imageView39.setOnClickListener(v->{finish();});

        // Thiết lập danh sách mức độ hoạt động
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.activity_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivityLevel.setAdapter(adapter);

        // Bắt sự kiện click
        btnCalculateBMR.setOnClickListener(view -> calculateBMR());
    }

    private void calculateBMR() {
        String weightStr = edtWeight.getText().toString();
        String heightStr = edtHeight.getText().toString();
        String ageStr = edtAge.getText().toString();

        if (weightStr.isEmpty() || heightStr.isEmpty() || ageStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            float weight = Float.parseFloat(weightStr);
            float height = Float.parseFloat(heightStr);
            int age = Integer.parseInt(ageStr);

            if (weight <= 0 || height <= 0 || age <= 0) {
                Toast.makeText(this, "Dữ liệu không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }

            float bmr;
            int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
            if (selectedGenderId == R.id.radioMale) {
                bmr = 88.36f + (13.4f * weight) + (4.8f * height) - (5.7f * age);
            } else {
                bmr = 447.6f + (9.2f * weight) + (3.1f * height) - (4.3f * age);
            }

            txtBmrResult.setText(String.format("Chỉ số BMR: %.2f kcal/ngày", bmr));

            // Tính TDEE dựa vào mức độ hoạt động
            float tdee = getTDEE(bmr);
            String recommendation = getDietRecommendation(tdee);

            txtDietRecommendation.setText(recommendation);

            // Hiệu ứng xuất hiện mờ dần
            AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
            fadeIn.setDuration(1000);
            txtBmrResult.startAnimation(fadeIn);
            txtDietRecommendation.startAnimation(fadeIn);

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Dữ liệu nhập vào không hợp lệ!", Toast.LENGTH_SHORT).show();
        }
    }

    private float getTDEE(float bmr) {
        int position = spinnerActivityLevel.getSelectedItemPosition();
        float[] activityFactors = {1.2f, 1.375f, 1.55f, 1.725f, 1.9f};
        return bmr * activityFactors[position];
    }

    private String getDietRecommendation(float tdee) {
        float maintainCalories = tdee;
        float loseWeightCalories = tdee - 500;
        float gainWeightCalories = tdee + 500;

        return String.format("🔥 TDEE: %.2f kcal/ngày\n" +
                        "💪 Để duy trì cân nặng: %.2f kcal/ngày\n" +
                        "📉 Để giảm cân: %.2f kcal/ngày\n" +
                        "📈 Để tăng cân: %.2f kcal/ngày",
                tdee, maintainCalories, loseWeightCalories, gainWeightCalories);
    }
}
