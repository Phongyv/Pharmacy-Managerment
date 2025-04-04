package com.example.pharmacymanagerment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class EditProductActivity extends AppCompatActivity {
    private EditText etProductName, etProductPrice;
    private ImageView imgProduct;
    private Button btnUpdate, btnChooseImage;
    private Uri imageUri;

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private String productId, imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        etProductName = findViewById(R.id.etProductName);
        etProductPrice = findViewById(R.id.etProductPrice);
        imgProduct = findViewById(R.id.imgProduct);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnChooseImage = findViewById(R.id.btnChooseImage);

        Button button5 = findViewById(R.id.button5);
        button5.setOnClickListener(v -> {
            finish();
        });

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Nhận productId từ Intent
        productId = getIntent().getStringExtra("productId");
        if (productId != null) {
            loadProductData();
        }

        btnChooseImage.setOnClickListener(v -> chooseImage());
        btnUpdate.setOnClickListener(v -> updateProduct());
    }

    private void loadProductData() {
        DocumentReference productRef = db.collection("products").document(productId);
        productRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                String price = String.valueOf(documentSnapshot.getDouble("price"));
                imageUrl = documentSnapshot.getString("img");

                etProductName.setText(name);
                etProductPrice.setText(price);

                Glide.with(this).load(imageUrl).into(imgProduct);
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            imgProduct.setImageURI(imageUri);
        }
    }

    private void updateProduct() {
        String newName = etProductName.getText().toString().trim();
        double newPrice = Double.parseDouble(etProductPrice.getText().toString().trim());

        if (imageUri != null) {
            uploadImageAndUpdate(newName, newPrice);
        } else {
            updateProductInFirestore(newName, newPrice, imageUrl);
        }
    }

    private void uploadImageAndUpdate(String newName, double newPrice) {
        StorageReference storageRef = storage.getReference().child("product_images/" + UUID.randomUUID().toString());

        storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                updateProductInFirestore(newName, newPrice, uri.toString());
            });
        }).addOnFailureListener(e ->
                Toast.makeText(EditProductActivity.this, "Lỗi tải ảnh", Toast.LENGTH_SHORT).show()
        );
    }

    private void updateProductInFirestore(String name, double price, String newImageUrl) {
        DocumentReference productRef = db.collection("products").document(productId);
        productRef.update("name", name, "price", price, "img", newImageUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProductActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(EditProductActivity.this, "Lỗi cập nhật", Toast.LENGTH_SHORT).show()
                );
    }
}
