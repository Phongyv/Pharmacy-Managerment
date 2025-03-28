package com.example.pharmacymanagerment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class Admin extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AdminProductAdapter adapter;
    private List<AdminProduct> productList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.app_color));

        ImageView imageView36 = findViewById(R.id.imageView36);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null){
            Glide.with(this)
                    .load(account.getPhotoUrl())
                    .transform(new RoundedCornersTransformation(50)) // Chỉnh sửa 30 để thay đổi độ bo tròn
                    .into(imageView36);
        } else{
            Glide.with(this)
                    .load(R.drawable.medical_cross)
                    .into(imageView36);
        } // Ảnh đại diện

        ImageView imageView37 = findViewById(R.id.imageView37);
        imageView37.setOnClickListener(v->{
            startActivity(new Intent(this, AddProductActivity.class));
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        adapter = new AdminProductAdapter(this, productList, product -> {
            // Xử lý khi bấm nút Thao tác
            Toast.makeText(Admin.this, "Clicked: " + product.getName(), Toast.LENGTH_SHORT).show();
        });

        recyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();

        listenForProductUpdates();
    }

    private void loadProducts() {
        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productList.clear();
                        for (DocumentSnapshot doc : task.getResult()) {
                            AdminProduct product = doc.toObject(AdminProduct.class);
                            productList.add(product);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(Admin.this, "Lỗi khi lấy sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void listenForProductUpdates() {
        db.collection("products")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(Admin.this, "Lỗi khi cập nhật sản phẩm", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    productList.clear();
                    if (value != null) {
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            AdminProduct product = doc.toObject(AdminProduct.class);
                            productList.add(product);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
    }

}
