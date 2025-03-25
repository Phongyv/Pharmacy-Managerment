package com.example.pharmacymanagerment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ProductItem extends AppCompatActivity {

    private FirebaseFirestore firestore;
    ImageView imageView25,imageView26;
    TextView textView26,textView27,textView29;
    Button button;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_item);

        db = FirebaseFirestore.getInstance();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        imageView25 = findViewById(R.id.imageView25);
        imageView26 = findViewById(R.id.imageView26);
        textView26 = findViewById(R.id.textView26);
        textView27 = findViewById(R.id.textView27);
        textView29 = findViewById(R.id.textView29);

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

                        }
                    });
        }

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (account!=null){
                    String id = account.getId(); // id
                    DocumentReference userRef = db.collection("users").document(id);
                    userRef.update("cart", FieldValue.arrayUnion(productId));
                    showAdd("Đã thêm vào giỏ hàng");
                }

            }
        });
        imageView26 = findViewById(R.id.imageView26);
        imageView26.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void showAdd(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
