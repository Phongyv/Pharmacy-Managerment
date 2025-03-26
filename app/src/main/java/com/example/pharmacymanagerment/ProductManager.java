package com.example.pharmacymanagerment;

import com.google.firebase.firestore.*;
import java.util.HashMap;
import java.util.Map;

public class ProductManager {
    private FirebaseFirestore db;

    public ProductManager() {
        db = FirebaseFirestore.getInstance();
    }

    // 🟢 Thêm sản phẩm vào Firestore
    public void addProduct(String name, double price, String description, String imageUrl) {
        Map<String, Object> product = new HashMap<>();
        product.put("name", name);
        product.put("price", price);
        product.put("description", description);
        product.put("imageUrl", imageUrl);

        db.collection("products")
                .add(product)
                .addOnSuccessListener(documentReference -> {
                    System.out.println("Sản phẩm đã được thêm: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    System.err.println("Lỗi khi thêm sản phẩm: " + e.getMessage());
                });
    }

    // 🟡 Cập nhật sản phẩm (Sửa thông tin)
    public void updateProduct(String productId, String name, double price, String description, String imageUrl) {
        DocumentReference productRef = db.collection("products").document(productId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("price", price);
        updates.put("description", description);
        updates.put("imageUrl", imageUrl);

        productRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Sản phẩm đã được cập nhật.");
                })
                .addOnFailureListener(e -> {
                    System.err.println("Lỗi khi cập nhật sản phẩm: " + e.getMessage());
                });
    }

    // 🔴 Xóa sản phẩm khỏi Firestore
    public void deleteProduct(String productId) {
        db.collection("products").document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Sản phẩm đã được xóa.");
                })
                .addOnFailureListener(e -> {
                    System.err.println("Lỗi khi xóa sản phẩm: " + e.getMessage());
                });
    }
}


//ProductManager productManager = new ProductManager();
//productManager.addProduct("Paracetamol", 50000, "Thuốc giảm đau, hạ sốt", "https://example.com/paracetamol.jpg");
//productManager.updateProduct("id_san_pham", "Paracetamol Extra", 60000, "Thuốc giảm đau mạnh hơn", "https://example.com/newimage.jpg");
//productManager.deleteProduct("id_san_pham");