package com.example.pharmacymanagerment;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;


import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    SearchView searchView;
    ImageView cart,notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);



        if (account != null) {
            String id = account.getId(); // ID người dùng
            String name = account.getDisplayName(); // Tên hiển thị
            String email = account.getEmail(); // Địa chỉ email

            // Kiểm tra xem người dùng đã tồn tại trong Firestore hay chưa
            DocumentReference userDoc = db.collection("users").document(id);
            userDoc.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (!document.exists()) {
                        // Nếu document không tồn tại, tạo người dùng mới
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("userId", id);
                        userData.put("name", name);
                        userData.put("email", email);
                        userData.put("cart", new ArrayList<>()); // Tạo giỏ hàng mới



                        userDoc.set(userData)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "User document successfully created!"))
                                .addOnFailureListener(e -> Log.w(TAG, "Error creating user document", e));
                    } else {
                        // Người dùng đã tồn tại, có thể cập nhật nếu cần
                        Log.d(TAG, "User already exists.");
                    }
                } else {
                    Log.w(TAG, "Error getting user document.", task.getException());
                }
            });
        }

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.app_color));

        searchView = findViewById(R.id.searchView);
        cart = findViewById(R.id.imageView);
        notification = findViewById(R.id.imageView2);
        cart.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
        });

        notification.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
            startActivity(intent);
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new HomeFragment()).commit();

        // Xử lý window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });
    }

    // Listener cho Bottom Navigation
    private final BottomNavigationView.OnItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            selectedFragment = new HomeFragment();
        } else if (itemId == R.id.nav_medicine) {
            selectedFragment = new MedicineFragment();
        } else if (itemId == R.id.nav_order) {
            selectedFragment = new OrderFragment();
        } else if (itemId == R.id.nav_me) {
            selectedFragment = new MeFragment();
        } else {
            return false;
        }
        // Thay thế Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, selectedFragment).commit();
        return true;
    };

}
