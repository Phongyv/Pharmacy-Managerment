package com.example.pharmacymanagerment;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ProductItem extends AppCompatActivity {

    private FirebaseFirestore firestore;
    ImageView imageView25;
    TextView textView26,textView27,textView29,textView31;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_item);
        imageView25 = findViewById(R.id.imageView25);
        textView26 = findViewById(R.id.textView26);
        textView27 = findViewById(R.id.textView27);
        textView29 = findViewById(R.id.textView29);
        textView31 = findViewById(R.id.textView31);

        firestore = FirebaseFirestore.getInstance();
        String productId = getIntent().getStringExtra("product_id");
        if (productId != null) {
            firestore.collection("products").document(productId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String des = documentSnapshot.getString("des");
                            String img = documentSnapshot.getString("img");
                            String type = documentSnapshot.getString("type");
                            Double price = documentSnapshot.getDouble("price");

                            Glide.with(this).load(img).into(imageView25);

                            textView26.setText(name);
                            textView27.setText(price +"đ");
                            textView29.setText(des);

                            if(Objects.equals(type, "medicine")){
                                textView31.setText("Thuoc");
                            } else if (Objects.equals(type, "codom")) {
                                textView31.setText("Bao cao su");
                            } else if (Objects.equals(type, "vitamin")) {
                                textView31.setText("Vitamin");
                            }else if (Objects.equals(type, "comestic")) {
                                textView31.setText("My pham");
                            }
                        }
                    });
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}