package com.example.pharmacymanagerment;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class NotificationHelper {

    private static final String CHANNEL_ID = "my_channel_id";
    private static final String CHANNEL_NAME = "Thông báo chung";
    private static final int NOTIFICATION_ID = 1;
    private static final String PREFS_NAME = "NotificationPrefs";
    private static final String KEY_NOTIFICATIONS = "notifications";

    private Context context;

    public NotificationHelper(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    // Tạo kênh thông báo
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Kênh thông báo nội bộ");

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    // Hiển thị thông báo và lưu vào SharedPreferences
    public void showNotification(String title, String message) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.medical_cross) // Đổi thành icon của bạn
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }

        // Lưu thông báo vào SharedPreferences
        saveNotification(title, message);
    }

    // Lưu thông báo vào SharedPreferences
    private void saveNotification(String title, String message) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Set<String> notifications = prefs.getStringSet(KEY_NOTIFICATIONS, new HashSet<>());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String timestamp = sdf.format(new Date());

        notifications.add(title + " - " + message + " - " + timestamp);
        editor.putStringSet(KEY_NOTIFICATIONS, notifications);
        editor.apply();
    }


    // Lấy danh sách thông báo đã lưu
    public Set<String> getSavedNotifications() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getStringSet(KEY_NOTIFICATIONS, new HashSet<>());
    }

    // Xóa tất cả thông báo đã lưu
    public void clearNotifications() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(KEY_NOTIFICATIONS);
        editor.apply();
    }
}
