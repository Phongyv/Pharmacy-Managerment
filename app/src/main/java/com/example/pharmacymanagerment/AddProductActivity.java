package com.example.pharmacymanagerment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {

    private ImageView productImage;
    private EditText etProductName, etProductDescription, etProductPrice;
    private Spinner spinnerCategory;
    private Button btnChooseImage, btnAddProduct, button4;
    private Uri imageUri;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ProgressDialog progressDialog;
    private String selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.white));

        productImage = findViewById(R.id.productImage);
        etProductName = findViewById(R.id.etProductName);
        etProductDescription = findViewById(R.id.etProductDescription);
        etProductPrice = findViewById(R.id.etProductPrice);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        button4 = findViewById(R.id.button4);

        button4.setOnClickListener(v -> {
            finish();
        });

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference("product_images");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý...");

        // Thiết lập danh sách loại sản phẩm
        String[] categories = {"Condom", "Medicine", "Vitamin", "Cosmetic"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCategory = categories[0]; // Chọn mặc định loại đầu tiên
            }
        });

        btnChooseImage.setOnClickListener(v -> selectImage());
        btnAddProduct.setOnClickListener(v -> addProductToFirestore());
    }

    private void selectImage() {
        ImagePicker.with(this)
                .crop()
                .compress(512)
                .maxResultSize(1080, 1080)
                .galleryOnly()
                .createIntent(intent -> {
                    startActivityForResult(intent, 101);
                    return null;
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            imageUri = data.getData();
            if (imageUri != null) {
                productImage.setImageURI(imageUri);
            }
        } else {
            Toast.makeText(this, "Chọn ảnh thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addProductToFirestore() {
        String name = etProductName.getText().toString().trim();
        String description = etProductDescription.getText().toString().trim();
        String priceStr = etProductPrice.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh!", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá sản phẩm không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        db.collection("products").whereEqualTo("name", name).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Sản phẩm đã tồn tại!", Toast.LENGTH_SHORT).show();
                    } else {
                        uploadImageAndSaveProduct(name, description, price);
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Lỗi kiểm tra dữ liệu!", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadImageAndSaveProduct(String name, String des, double price) {
        String imageName = UUID.randomUUID().toString();
        StorageReference imageRef = storageRef.child(imageName);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveProductToFirestore(name, des, price, uri.toString());
                }))
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Lỗi tải ảnh lên", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveProductToFirestore(String name, String des, double price, String img) {
        String productId = UUID.randomUUID().toString();
        HashMap<String, Object> product = new HashMap<>();
        product.put("id", productId);
        product.put("name", name);
        product.put("des", des);
        product.put("price", price);
        product.put("type", selectedCategory);
        product.put("img", img);

        db.collection("products").document(productId)
                .set(product)
                .addOnSuccessListener(aVoid -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Thêm sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                    resetForm();
                });
    }

    private void resetForm() {
        etProductName.setText("");
        etProductDescription.setText("");
        etProductPrice.setText("");
        spinnerCategory.setSelection(0);
        productImage.setImageResource(android.R.color.darker_gray);
        imageUri = null;
    }
}
