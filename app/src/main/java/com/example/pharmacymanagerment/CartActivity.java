package com.example.pharmacymanagerment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
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
    ImageView imageView29;
    Button button3;
    private ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        NotificationHelper notificationHelper = new NotificationHelper(this);
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();

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
        imageView29 = findViewById(R.id.imageView29);
        imageView29.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        button3 = findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("open_fragment", "OrderFragment");
                notificationHelper.showNotification("Pharmacy Managerment", "Tạo đơn hàng thành công");
                startActivity(intent);
                finish();
            }
        });
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
                                Toast.makeText(this,"Giỏ hàng trống.",Toast.LENGTH_SHORT).show();
                                Log.d("Firestore", "Giỏ hàng trống.");
                            }
                        } else {
                            Toast.makeText(this,"Không tìm thấy tài khoản người dùng.",Toast.LENGTH_SHORT).show();
                            Log.d("Firestore", "Không tìm thấy tài khoản người dùng.");
                        }
                    } else {
                        Toast.makeText(this,"Lỗi khi lấy giỏ hàng: ",Toast.LENGTH_SHORT).show();
                        Log.e("Firestore", "Lỗi khi lấy giỏ hàng: ", task.getException());
                    }
                });
    }

//    private void fetchProductDetails(List<String> productIds) {
//        List<Map<String, Object>> products = new ArrayList<>();
//        for (String productId : productIds) {
//            db.collection("products").document(productId)
//                    .get()
//                    .addOnCompleteListener(productTask -> {
//                        if (productTask.isSuccessful()) {
//                            DocumentSnapshot productDocument = productTask.getResult();
//                            if (productDocument.exists()) {
//                                Map<String, Object> productData = new HashMap<>();
//                                productData.put("productId", productId);
//                                productData.put("name", productDocument.getString("name"));
//                                productData.put("price", productDocument.getDouble("price"));
//                                productData.put("img", productDocument.getString("img"));
//                                products.add(productData);
//                            } else {
//                                Toast.makeText(this,"Không tìm thấy sản phẩm: ",Toast.LENGTH_SHORT).show();
//                                Log.d("Firestore", "Không tìm thấy sản phẩm: " + productId);
//                            }
//
//                            if (products.size() == productIds.size()) {
//                                cartItems.clear();
//                                cartItems.addAll(products);
//                                cartAdapter.notifyDataSetChanged();
//                                shimmerFrameLayout.stopShimmer();
//                                shimmerFrameLayout.setVisibility(View.GONE);
//                            }
//                        } else {
//                            Toast.makeText(this,"Lỗi không tìm thấy sản phẩm: ",Toast.LENGTH_SHORT).show();
//                            Log.e("Firestore", "Lỗi khi lấy sản phẩm: ", productTask.getException());
//                        }
//                    });
//        }
//    }

    private void fetchProductDetails(List<String> productIds) {
        List<Map<String, Object>> products = new ArrayList<>();
        final int[] completedCount = {0}; // Dùng để đếm số lượt hoàn tất

        for (String productId : productIds) {
            db.collection("products").document(productId)
                    .get()
                    .addOnCompleteListener(productTask -> {
                        completedCount[0]++;

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
                        } else {
                            Log.e("Firestore", "Lỗi khi lấy sản phẩm: ", productTask.getException());
                        }

                        // Khi hoàn tất tất cả các sản phẩm
                        if (completedCount[0] == productIds.size()) {
                            cartItems.clear();
                            cartItems.addAll(products);
                            cartAdapter.notifyDataSetChanged();
                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);

                            if (products.isEmpty()) {
                                Toast.makeText(this, "Không tìm thấy sản phẩm nào trong giỏ hàng.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

}
