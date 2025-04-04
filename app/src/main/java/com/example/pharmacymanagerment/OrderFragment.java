package com.example.pharmacymanagerment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

public class OrderFragment extends Fragment implements CartManager.CartListener {

    private TextView tvTotalPrice;
    private RecyclerView recyclerViewCart;
    private OrderCartAdapter orderCartAdapter;
    private CartManager cartManager;
    private PaymentHelper paymentHelper;
    private ShimmerFrameLayout shimmerFrameLayout;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        recyclerViewCart = view.findViewById(R.id.recyclerViewCart);
        View view15 = view.findViewById(R.id.view15);
        View view16 = view.findViewById(R.id.view16);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();

        recyclerViewCart.setLayoutManager(new LinearLayoutManager(getContext()));

        paymentHelper = new PaymentHelper(requireActivity(), requireContext());

        cartManager = new CartManager(this);
        cartManager.getCartItemsAndCalculateTotal(requireContext());

        view15.setOnClickListener(v -> paymentHelper.openZaloPay());
        view16.setOnClickListener(v -> paymentHelper.openMoMo());

        return view;
    }

    @Override
    public void onCartLoaded(List<CartManager.CartItem> cartItems, double total) {
        if (!isAdded() || getContext() == null) {
            System.out.println("Fragment chưa được gắn vào Activity!");
            return;
        }
        orderCartAdapter = new OrderCartAdapter(cartItems);
        recyclerViewCart.setAdapter(orderCartAdapter);
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE); // Ẩn shimmer khi tải xong
        tvTotalPrice.setText("Tổng tiền: " + total + " VND");
        NotificationHelper notificationHelper = new NotificationHelper(requireContext());
        notificationHelper.showNotification("Bạn có đơn hàng chưa thanh toán","Tổng tiền: " + total + " VND" );
    }

    @Override
    public void onError(String errorMessage) {
        tvTotalPrice.setText(errorMessage);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cartManager = null; // Ngăn chặn callback khi Fragment bị hủy
    }

}
