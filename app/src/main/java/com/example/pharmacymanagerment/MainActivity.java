package com.example.pharmacymanagerment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    SearchView searchView;
    ImageView cart,notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_PharmacyManagerment);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.app_color));

        searchView = findViewById(R.id.searchView);
        cart = findViewById(R.id.imageView);
        notification = findViewById(R.id.imageView2);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,CartActivity.class);
                startActivity(intent);
            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,NotificationActivity.class);
                startActivity(intent);
            }
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new HomeFragment()).commit();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private final BottomNavigationView.OnItemSelectedListener navListener = new BottomNavigationView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
            }else {
                return false;
            }
            // Thay thế Fragment
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, selectedFragment).commit();
            return true;
        }
    };
    }