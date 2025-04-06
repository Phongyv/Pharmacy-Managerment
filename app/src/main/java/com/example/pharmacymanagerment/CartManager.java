package com.example.pharmacymanagerment;

import android.content.Context;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private FirebaseFirestore db;
    private CartListener listener;

    public CartManager(CartListener listener) {
        this.db = FirebaseFirestore.getInstance();
        this.listener = listener;
    }

    public void getCartItemsAndCalculateTotal(Context context) {
        if (context == null) {
            listener.onError("Context không hợp lệ!");
            return;
        }

        GoogleSignInAccount currentUser = GoogleSignIn.getLastSignedInAccount(context);
        if (currentUser == null) {
            listener.onError("Người dùng chưa đăng nhập!");
            return;
        }

        String userId = currentUser.getId();
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> cart = (List<String>) documentSnapshot.get("cart");
                        if (cart == null || cart.isEmpty()) {
                            listener.onCartLoaded(new ArrayList<>(), 0);
                            return;
                        }
                        calculateTotal(cart);
                    }
                })
                .addOnFailureListener(e -> listener.onError("Lỗi lấy giỏ hàng: " + e.getMessage()));
    }

//    private void calculateTotal(List<String> cart) {
//        final double[] totalPrice = {0};
//        final List<CartItem> cartItems = new ArrayList<>();
//        final int[] count = {0};
//
//        for (String productId : cart) {
//            db.collection("products").document(productId)
//                    .get()
//                    .addOnSuccessListener(productSnapshot -> {
//                        if (productSnapshot.exists()) {
//                            String name = productSnapshot.getString("name");
//                            Double price = productSnapshot.getDouble("price");
//
//                            if (price != null) {
//                                totalPrice[0] += price;
//                                cartItems.add(new CartItem(name, price));
//                            }
//
//                            count[0]++;
//                            if (count[0] == cart.size()) {
//                                listener.onCartLoaded(cartItems, totalPrice[0]);
//                            }
//                        }
//                    })
//                    .addOnFailureListener(e -> listener.onError("Lỗi lấy sản phẩm: " + e.getMessage()));
//        }
//    }

    private void calculateTotal(List<String> cart) {
        final double[] totalPrice = {0};
        final List<CartItem> cartItems = new ArrayList<>();
        final int[] count = {0};

        for (String productId : cart) {
            db.collection("products").document(productId)
                    .get()
                    .addOnSuccessListener(productSnapshot -> {
                        if (productSnapshot.exists()) {
                            String name = productSnapshot.getString("name");
                            Double price = productSnapshot.getDouble("price");

                            if (price != null) {
                                totalPrice[0] += price;
                                cartItems.add(new CartItem(name, price));
                            }
                        }

                        // Tăng count dù sản phẩm không tồn tại
                        count[0]++;
                        if (count[0] == cart.size()) {
                            listener.onCartLoaded(cartItems, totalPrice[0]);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Tăng count dù lỗi
                        count[0]++;
                        if (count[0] == cart.size()) {
                            listener.onCartLoaded(cartItems, totalPrice[0]);
                        }
                    });
        }
    }


    public interface CartListener {
        void onCartLoaded(List<CartItem> cartItems, double total);
        void onError(String errorMessage);
    }

    public static class CartItem {
        String name;
        double price;

        public CartItem(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }
    }
}
