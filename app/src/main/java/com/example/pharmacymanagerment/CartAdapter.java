package com.example.pharmacymanagerment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<Map<String, Object>> cartItems;

    public CartAdapter(List<Map<String, Object>> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate item layout for each product in the cart
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get the product data for the current position
        Map<String, Object> item = cartItems.get(position);
        holder.productId.setText("Product ID: " + item.get("productId"));
        holder.productName.setText("Product Name: " + item.get("name"));
        holder.price.setText("Price: $" + item.get("price"));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productId, productName, price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productId = itemView.findViewById(R.id.productId);
            productName = itemView.findViewById(R.id.name); // TextView cho tên sản phẩm
            price = itemView.findViewById(R.id.price);
        }
    }
}
