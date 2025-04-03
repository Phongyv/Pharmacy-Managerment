package com.example.pharmacymanagerment;

import android.net.Uri;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ProductManager {
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    public ProductManager() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    // 🟢 Thêm sản phẩm vào Firestore
    public void addProduct(String name, double price, String des, String img) {
        Map<String, Object> product = new HashMap<>();
        product.put("name", name);
        product.put("price", price);
        product.put("des", des);
        product.put("img", img);

        db.collection("products")
                .add(product)
                .addOnSuccessListener(documentReference -> {
                    System.out.println("Sản phẩm đã được thêm: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    System.err.println("Lỗi khi thêm sản phẩm: " + e.getMessage());
                });
    }

    // 🟡 Cập nhật sản phẩm và cập nhật ảnh trong Storage
    public void updateProduct(String productId, String name, double price, String des, Uri newImageUri) {
        DocumentReference productRef = db.collection("products").document(productId);

        // Lấy ảnh cũ trước khi cập nhật
        productRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String oldImageUrl = documentSnapshot.getString("img");

                if (newImageUri != null) {
                    // Nếu có ảnh mới, xóa ảnh cũ trước khi tải ảnh mới
                    if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                        StorageReference oldImageRef = storage.getReferenceFromUrl(oldImageUrl);
                        oldImageRef.delete().addOnSuccessListener(aVoid -> {
                            System.out.println("Ảnh cũ đã được xóa.");
                        }).addOnFailureListener(e -> {
                            System.err.println("Lỗi khi xóa ảnh cũ: " + e.getMessage());
                        });
                    }

                    // Tải ảnh mới lên Storage
                    StorageReference newImageRef = storage.getReference().child("product_images/" + productId + ".jpg");
                    newImageRef.putFile(newImageUri)
                            .addOnSuccessListener(taskSnapshot -> newImageRef.getDownloadUrl().addOnSuccessListener(newImageUrl -> {
                                // Cập nhật thông tin sản phẩm với ảnh mới
                                updateProductInfo(productId, name, price, des, newImageUrl.toString());
                            }))
                            .addOnFailureListener(e -> System.err.println("Lỗi khi tải ảnh mới: " + e.getMessage()));
                } else {
                    // Nếu không có ảnh mới, chỉ cập nhật thông tin sản phẩm
                    updateProductInfo(productId, name, price, des, oldImageUrl);
                }
            }
        }).addOnFailureListener(e -> {
            System.err.println("Lỗi khi lấy thông tin sản phẩm: " + e.getMessage());
        });
    }

    // Hàm cập nhật thông tin sản phẩm trong Firestore
    private void updateProductInfo(String productId, String name, double price, String des, String img) {
        DocumentReference productRef = db.collection("products").document(productId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("price", price);
        updates.put("des", des);
        updates.put("img", img);

        productRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Sản phẩm đã được cập nhật.");
                })
                .addOnFailureListener(e -> {
                    System.err.println("Lỗi khi cập nhật sản phẩm: " + e.getMessage());
                });
    }

    // 🔴 Xóa sản phẩm và ảnh khỏi Firestore và Storage
    public void deleteProduct(String productId) {
        DocumentReference productRef = db.collection("products").document(productId);

        // Lấy thông tin sản phẩm để xóa ảnh trước
        productRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String imageUrl = documentSnapshot.getString("img");

                if (imageUrl != null && !imageUrl.isEmpty()) {
                    StorageReference photoRef = storage.getReferenceFromUrl(imageUrl);

                    // Xóa ảnh trong Firebase Storage
                    photoRef.delete().addOnSuccessListener(aVoid -> {
                        System.out.println("Ảnh đã được xóa thành công.");

                        // Sau khi xóa ảnh, tiếp tục xóa sản phẩm trong Firestore
                        productRef.delete().addOnSuccessListener(aVoid1 -> {
                            System.out.println("Sản phẩm đã được xóa.");
                        }).addOnFailureListener(e -> {
                            System.err.println("Lỗi khi xóa sản phẩm: " + e.getMessage());
                        });

                    }).addOnFailureListener(e -> {
                        System.err.println("Lỗi khi xóa ảnh: " + e.getMessage());
                    });

                } else {
                    // Nếu không có ảnh, chỉ xóa sản phẩm
                    productRef.delete().addOnSuccessListener(aVoid -> {
                        System.out.println("Sản phẩm đã được xóa.");
                    }).addOnFailureListener(e -> {
                        System.err.println("Lỗi khi xóa sản phẩm: " + e.getMessage());
                    });
                }
            }
        }).addOnFailureListener(e -> {
            System.err.println("Lỗi khi lấy thông tin sản phẩm: " + e.getMessage());
        });
    }
}


//ProductManager productManager = new ProductManager();
//
//// Thêm sản phẩm mới
//productManager.addProduct("Paracetamol", 50000, "Thuốc giảm đau, hạ sốt", "https://firebasestorage.googleapis.com/example.jpg");
//
//// Cập nhật sản phẩm với ảnh mới
//Uri newImageUri = Uri.parse("file:///storage/emulated/0/DCIM/new_image.jpg"); // Đây là ảnh mới được chọn
//productManager.updateProduct("id_san_pham", "Paracetamol Extra", 60000, "Thuốc giảm đau mạnh hơn", newImageUri);
//
//// Xóa sản phẩm (Sẽ xóa cả ảnh)
//productManager.deleteProduct("id_san_pham");

