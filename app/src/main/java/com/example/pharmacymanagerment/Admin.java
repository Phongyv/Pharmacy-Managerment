package com.example.pharmacymanagerment;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class Admin extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AdminProductAdapter adapter;
    private List<AdminProduct> productList;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.app_color));


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        ImageView imageView36 = findViewById(R.id.imageView36);
        imageView36.setOnClickListener(v->{
            showLogoutDialog();
        });
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account.getPhotoUrl() != null ){
            Glide.with(this)
                    .load(account.getPhotoUrl())
                    .transform(new RoundedCornersTransformation(50)) // Chỉnh sửa 30 để thay đổi độ bo tròn
                    .into(imageView36);
        }else{
            Glide.with(this)
                    .load(R.drawable.medical_cross)
                    .into(imageView36);
        } // Ảnh đại diện

        ImageView imageView37 = findViewById(R.id.imageView37);
        imageView37.setOnClickListener(v->{
            startActivity(new Intent(this, AddProductActivity.class));
        });


        ImageView imageView45 = findViewById(R.id.imageView45);
        imageView45.setOnClickListener(v->{
            startActivity(new Intent(this,UserManager.class));
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        adapter = new AdminProductAdapter(this, productList, product -> {
            // Xử lý khi bấm nút Thao tác
            showProductDialog(product);
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

    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Toast.makeText(this,"Đăng xuất thành công",LENGTH_SHORT).show();
            Log.d("ProfileFragment", "Đăng xuất thành công");
            // Quay trở lại LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            this.finish();
        });
    }
    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận đăng xuất");
        builder.setMessage("Bạn có chắc chắn muốn đăng xuất?");

        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                signOut();
            }
        });

        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Đóng hộp thoại
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showProductDialog(AdminProduct product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_product_options, null);

        ImageView imgProduct = dialogView.findViewById(R.id.imgProduct);
        TextView tvProductName = dialogView.findViewById(R.id.tvProductName);
        Button btnEdit = dialogView.findViewById(R.id.btnEdit);
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        // Set dữ liệu
        tvProductName.setText(product.getName());

        // Load ảnh sản phẩm
        Glide.with(this)
                .load(product.getImg())
                .transform(new RoundedCornersTransformation(10))
                .placeholder(R.drawable.placeholder)
                .into(imgProduct);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Xử lý khi bấm nút Sửa
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(Admin.this, EditProductActivity.class);
            intent.putExtra("productId", product.getId());
            startActivity(intent);
            dialog.dismiss();
        });

        // Xử lý khi bấm nút Xóa
        btnDelete.setOnClickListener(v -> {
            showDeleteConfirmationDialog(product,dialog);
        });

        // Xử lý khi bấm nút Hủy
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showDeleteConfirmationDialog(AdminProduct product, AlertDialog parentDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa sản phẩm này?");

        builder.setPositiveButton("Xóa", (dialog, which) -> {
            ProductManager productManager = new ProductManager();
            productManager.deleteProduct(product.getId());
            Toast.makeText(this,"Đã xóa sản phẩm "+ product.getName(), LENGTH_SHORT).show();
            parentDialog.dismiss();
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
