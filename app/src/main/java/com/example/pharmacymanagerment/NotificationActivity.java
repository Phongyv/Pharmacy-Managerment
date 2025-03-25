package com.example.pharmacymanagerment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NotificationActivity extends AppCompatActivity {

    private ListView listView;
    private NotificationHelper notificationHelper;
    private NotificationAdapter notificationAdapter;
    private List<NotificationModel> notificationList;
    ImageView imageView33,imageView32;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        listView = findViewById(R.id.listView);
        notificationHelper = new NotificationHelper(this);

        // Lấy danh sách thông báo từ SharedPreferences
        Set<String> savedNotifications = notificationHelper.getSavedNotifications();
        notificationList = new ArrayList<>();

        for (String item : savedNotifications) {
            String[] parts = item.split(" - ");
            if (parts.length == 3) {
                notificationList.add(new NotificationModel(parts[0], parts[1], parts[2]));
            }
        }

        // Gán adapter cho ListView
        notificationAdapter = new NotificationAdapter(this, notificationList);
        listView.setAdapter(notificationAdapter);

        imageView33 = findViewById(R.id.imageView33);
        imageView32 = findViewById(R.id.imageView32);

        imageView32.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageView33.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationHelper.clearNotifications(); // Xóa dữ liệu trong SharedPreferences
                notificationList.clear(); // Xóa danh sách trong bộ nhớ
                notificationAdapter.notifyDataSetChanged(); // Cập nhật UI
            }
        });
    }
}
