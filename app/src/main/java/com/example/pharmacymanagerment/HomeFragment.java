package com.example.pharmacymanagerment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Now that the layout is inflated, you can find your ImageSlider
        ImageSlider imageSlider = view.findViewById(R.id.image_slider); // Correct: Use 'view' here
        // Create a list of SlideModels
        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.slide1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.slide2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.slide3, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.slide4, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.slide5, ScaleTypes.FIT));
        // Set the list of SlideModels to the ImageSlider
        imageSlider.setImageList(slideModels);

//        //Biểu đồ
//        LineChart lineChart = view.findViewById(R.id.lineChart);
//        ArrayList<Entry> entries = new ArrayList<>();
//        entries.add(new Entry(2004, 20000));
//        entries.add(new Entry(2005, 21000));
//        entries.add(new Entry(2006, 30000));
//        entries.add(new Entry(2007, 35000));
//        entries.add(new Entry(2008, 40000));
//        entries.add(new Entry(2009, 30000));
//        entries.add(new Entry(2010, 60000));
//        entries.add(new Entry(2011, 70000));
//        entries.add(new Entry(2012, 80000));
//        entries.add(new Entry(2013, 90000));
//        entries.add(new Entry(2014, 80000));
//        entries.add(new Entry(2015, 65000));
//        LineDataSet dataSet = new LineDataSet(entries, "Biểu đồ bệnh nhân ARV và nhiễm HIV mới");
//        dataSet.setValueTextColor(Color.BLUE); // Màu sắc của nhãn giá trị
//        dataSet.setColor(Color.BLUE); // Màu sắc đường biểu đồ
//        LineData lineData = new LineData(dataSet);
//        // Thiết lập dữ liệu cho biểu đồ
//        lineChart.setData(lineData);
//        lineChart.invalidate(); // Làm mới biểu đồ

        return view; // Return the inflated layout
    }
}
