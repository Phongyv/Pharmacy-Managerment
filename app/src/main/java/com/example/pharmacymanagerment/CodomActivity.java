package com.example.pharmacymanagerment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CodomActivity extends AppCompatActivity {

    private GridView gridView;
    private ProductAdapter medicineAdapter;
    private List<Product> medicineList;
    TextView title;
    ImageView imageView15,imageView24;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_medicine);

        title = findViewById(R.id.textView25);
        title.setText("Bao Cao Su");

        imageView15 = findViewById(R.id.imageView15);
        imageView15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        gridView = findViewById(R.id.gridView);
        medicineList = new ArrayList<>();
        medicineAdapter = new ProductAdapter(this, medicineList);
        gridView.setAdapter(medicineAdapter);

        gridView.setOnItemClickListener(((parent, view, position, id) -> {
            Product product = medicineList.get(position);
            Intent intent = new Intent(this, ProductItem.class);
            intent.putExtra("product_id", product.getId());
            startActivity(intent);
        }));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .whereEqualTo("type", "codom")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String img = document.getString("img");
                            String type = document.getString("type");
                            String des = document.getString("des");
                            double price = document.getDouble("price");
                            String id = document.getId();

                            medicineList.add(new Product(name, img, type, des, price,id));
                        }
                        medicineAdapter.notifyDataSetChanged(); // Cập nhật gridview
                    }
                });

        imageView24 = findViewById(R.id.imageView24);
        imageView24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_filter, null);
        builder.setView(dialogView);

        RadioGroup radioGroupFilter = dialogView.findViewById(R.id.radioGroupFilter);
        Button buttonApplyFilter = dialogView.findViewById(R.id.buttonApplyFilter);

        AlertDialog dialog = builder.create();

        buttonApplyFilter.setOnClickListener(v -> {
            int selectedId = radioGroupFilter.getCheckedRadioButtonId();
            String filter = "all";

            if (selectedId == R.id.radioBtnPriceLowToHigh) {
                filter = "price_low_to_high";
            } else if (selectedId == R.id.radioBtnPriceHighToLow) {
                filter = "price_high_to_low";
            }

            applyFilter(filter);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void applyFilter(String filter) {
        medicineList.clear(); // Clear the current list

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (filter.equals("all")) {
            db.collection("products")
                    .whereEqualTo("type", "codom")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            handleFirestoreResults(task);
                        }
                    });
        } else if (filter.equals("price_low_to_high")) {
            db.collection("products")
                    .whereEqualTo("type", "codom")
                    .orderBy("price", Query.Direction.ASCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            handleFirestoreResults(task);
                        }
                    });
        } else if (filter.equals("price_high_to_low")) {
            db.collection("products")
                    .whereEqualTo("type", "codom")
                    .orderBy("price", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            handleFirestoreResults(task);
                        }
                    });
        }
    }

    private void handleFirestoreResults(Task<QuerySnapshot> task) {
        for (QueryDocumentSnapshot document : task.getResult()) {
            String name = document.getString("name");
            String img = document.getString("img");
            String type = document.getString("type");
            String des = document.getString("des");
            double price = document.getDouble("price");
            String id = document.getId();
            medicineList.add(new Product(name, img, type, des, price,id));
        }
        medicineAdapter.notifyDataSetChanged(); // Update gridView
    }
}
