package com.example.pharmacymanagerment;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CartAdapter cartAdapter;
    private List<Map<String, Object>> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        db = FirebaseFirestore.getInstance();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        if (recyclerView != null && account != null) {
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                cartItems = new ArrayList<>();
                cartAdapter = new CartAdapter(this,cartItems,account.getId());
                recyclerView.setAdapter(cartAdapter);
                getProductsFromCart(account.getId());
        }
    }

    private void getProductsFromCart(String userId) {
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            List<String> cart = (List<String>) document.get("cart");
                            if (cart != null) {
                                cartItems.clear();
                                fetchProductDetails(cart);
                            } else {
                                Log.d("Firestore", "Giỏ hàng trống.");
                            }
                        } else {
                            Log.d("Firestore", "Không tìm thấy tài khoản người dùng.");
                        }
                    } else {
                        Log.e("Firestore", "Lỗi khi lấy giỏ hàng: ", task.getException());
                    }
                });
    }

    private void fetchProductDetails(List<String> productIds) {
        List<Map<String, Object>> products = new ArrayList<>();
        for (String productId : productIds) {
            db.collection("products").document(productId)
                    .get()
                    .addOnCompleteListener(productTask -> {
                        if (productTask.isSuccessful()) {
                            DocumentSnapshot productDocument = productTask.getResult();
                            if (productDocument.exists()) {
                                Map<String, Object> productData = new HashMap<>();
                                productData.put("productId", productId);
                                productData.put("name", productDocument.getString("name"));
                                productData.put("price", productDocument.getDouble("price"));
                                productData.put("img", productDocument.getString("img"));
                                products.add(productData);
                            } else {
                                Log.d("Firestore", "Không tìm thấy sản phẩm: " + productId);
                            }

                            if (products.size() == productIds.size()) {
                                cartItems.clear();
                                cartItems.addAll(products);
                                cartAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.e("Firestore", "Lỗi khi lấy sản phẩm: ", productTask.getException());
                        }
                    });
        }
    }
}
