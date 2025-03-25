package com.example.pharmacymanagerment;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

        // Khởi tạo Firestore
        db = FirebaseFirestore.getInstance();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        // Khởi tạo RecyclerView
         RecyclerView recyclerView = findViewById(R.id.recyclerView);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            cartItems = new ArrayList<>(); // Khởi tạo danh sách sản phẩm
            cartAdapter = new CartAdapter(cartItems);
            recyclerView.setAdapter(cartAdapter);
            // Gọi hàm để lấy sản phẩm từ cart
            getProductsFromCart(account.getId()); //  ID người dùng thực tế
        }
    }
    private void getProductsFromCart(String userId) {
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Lấy dữ liệu từ field "cart"
                            List<String> cart = (List<String>) document.get("cart");
                            if (cart != null) {
                                cartItems.clear(); // Xóa danh sách cũ
                                fetchProductDetails(cart); // Gọi phương thức để lấy chi tiết sản phẩm
                            } else {
                                Log.d("TAG", "Giỏ hàng trống.");
                            }
                        } else {
                            Log.d("TAG", "Không tìm thấy document.");
                        }
                    } else {
                        Log.w("TAG", "Lỗi khi lấy dữ liệu: ", task.getException());
                    }
                });
    }

    private void fetchProductDetails(List<String> productIds) {
        // Khởi tạo danh sách để chứa dữ liệu sản phẩm
        List<Map<String, Object>> products = new ArrayList<>();
        for (String productId : productIds) {
            DocumentReference productRef = db.collection("products").document(productId);
            productRef.get().addOnCompleteListener(productTask -> {
                if (productTask.isSuccessful()) {
                    DocumentSnapshot productDocument = productTask.getResult();
                    if (productDocument.exists()) {
                        Map<String, Object> productData = new HashMap<>();
                        productData.put("productId", productId);
                        productData.put("name", productDocument.getString("name"));
                        productData.put("price", productDocument.getDouble("price"));
                        products.add(productData);
                    } else {
                        Log.d("TAG", "Không tìm thấy sản phẩm: " + productId);
                    }

                    // Cập nhật RecyclerView sau khi tất cả sản phẩm đã được lấy
                    if (products.size() == productIds.size()) {
                        cartItems.clear(); // Xóa danh sách cũ
                        cartItems.addAll(products); // Thêm sản phẩm vào danh sách
                        cartAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView
                    }
                } else {
                    Log.w("TAG", "Lỗi khi lấy sản phẩm: ", productTask.getException());
                }
            });
        }
    }
}
