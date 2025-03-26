package com.example.pharmacymanagerment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class OrderFragment extends Fragment {

    View view15,view16;
    private PaymentHelper paymentHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        paymentHelper = new PaymentHelper(requireActivity(),requireContext());
        view15 = view.findViewById(R.id.view15);
        view16 = view.findViewById(R.id.view16);

        view15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentHelper.payWithZaloPay("0338191380", "100000", "Thanh toán đơn hàng");
            }
        });

        view16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentHelper.payWithMoMo("0338191380", "100000", "Thanh toán đơn hàng");
            }
        });

        return view;
    }
     @Override
    public void onResume() {
        super.onResume();
        Intent intent = requireActivity().getIntent();
        if (intent != null) {
            paymentHelper.handlePaymentResponse(intent);
            requireActivity().setIntent(null); // Xóa intent để tránh xử lý lại khi quay lại Fragment
        }
    }
}

