package com.example.pharmacymanagerment;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserManager extends AppCompatActivity {

    ListView userListView;
    FirebaseFirestore db;
    List<String> userNames = new ArrayList<>();
    List<String> userIds = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manager);

        userListView = findViewById(R.id.userListView);
        db = FirebaseFirestore.getInstance();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userNames);
        userListView.setAdapter(adapter);

        loadUsers();

        userListView.setOnItemClickListener((parent, view, position, id) -> {
            String userId = userIds.get(position);
            fetchCartTotal(userId);
        });
    }

    private void loadUsers() {
        db.collection("users").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String name = doc.getString("name");
                String uid = doc.getId();
                userNames.add(name);
                userIds.add(uid);
            }
            adapter.notifyDataSetChanged();
        });
    }

    private void fetchCartTotal(String userId) {
        db.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            List<String> cart = (List<String>) documentSnapshot.get("cart");

            if (cart == null || cart.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
                return;
            }

            List<CartItem> cartItems = new ArrayList<>();
            final double[] total = {0};
            final int[] loadedCount = {0};

            for (String productId : cart) {
                db.collection("products").document(productId).get().addOnSuccessListener(productDoc -> {
                    if (productDoc.exists()) {
                        String name = productDoc.getString("name");
                        Double price = productDoc.getDouble("price");
                        if (name != null && price != null) {
                            cartItems.add(new CartItem(name, price));
                            total[0] += price;
                        }
                    }

                    loadedCount[0]++;
                    if (loadedCount[0] == cart.size()) {
                        showCartDialog(cartItems, total[0]);
                    }
                });
            }
        });
    }

    private void showCartDialog(List<CartItem> cartItems, double totalPrice) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_cart_detail, null);

        ListView cartListView = dialogView.findViewById(R.id.cartListView);
        TextView totalTextView = dialogView.findViewById(R.id.totalPriceTextView);

        ArrayAdapter<CartItem> cartAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, cartItems);
        cartListView.setAdapter(cartAdapter);
        totalTextView.setText("Tổng tiền: " + totalPrice + " VNĐ");

        new AlertDialog.Builder(this)
                .setTitle("Chi tiết giỏ hàng")
                .setView(dialogView)
                .setPositiveButton("Đóng", null)
                .show();
    }
}
