package com.example.pharmacymanagerment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pharmacymanagerment.databinding.FragmentMedicineBinding;


public class MedicineFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medicine, container, false);

        ConstraintLayout constraintLayout = view.findViewById(R.id.item1);
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Xử lý sự kiện click tại đây

                Intent intent = new Intent(requireContext(), MedicineActivity.class);
                startActivity(intent);
            }
        });
        ConstraintLayout constraintLayout1 = view.findViewById(R.id.item2);
        constraintLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Xử lý sự kiện click tại đây
                Intent intent = new Intent(requireContext(), CodomActivity.class);
                startActivity(intent);
            }
        });
        ConstraintLayout constraintLayout2 = view.findViewById(R.id.item3);
        constraintLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Xử lý sự kiện click tại đây
                Intent intent = new Intent(requireContext(), VitaminActivity.class);
                startActivity(intent);
            }
        });
        ConstraintLayout constraintLayout3 = view.findViewById(R.id.item4);
        constraintLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Xử lý sự kiện click tại đây
                Intent intent = new Intent(requireContext(), ComesticActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}

