package com.example.pharmacymanagerment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class PaymentHelper {
    private static final String MOMO_PACKAGE = "com.mservice.momotransfer";
    private static final String ZALOPAY_PACKAGE = "vn.com.vng.zalopay";
    private static final String MOMO_SCHEME = "momo://";
    private static final String ZALOPAY_SCHEME = "zalopay://";

    private Activity activity;
    private Context context;

    public PaymentHelper(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    // Kiểm tra MoMo đã được cài đặt chưa
    private boolean isMoMoInstalled() {
        try {
            activity.getPackageManager().getPackageInfo(MOMO_PACKAGE, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    // Kiểm tra ZaloPay đã được cài đặt chưa
    private boolean isZaloPayInstalled() {
        try {
            activity.getPackageManager().getPackageInfo(ZALOPAY_PACKAGE, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    // Mở MoMo
    public void openMoMo() {
        if (!isMoMoInstalled()) {
            Toast.makeText(activity, "MoMo chưa được cài đặt!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Uri uri = Uri.parse(MOMO_SCHEME);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage(MOMO_PACKAGE);
            activity.startActivity(intent);
        } catch (Exception e) {
            Log.e("PaymentHelper", "Lỗi khi mở MoMo", e);
        }
    }

    // Mở ZaloPay
    public void openZaloPay() {
        if (!isZaloPayInstalled()) {
            Toast.makeText(activity, "ZaloPay chưa được cài đặt!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Uri uri = Uri.parse(ZALOPAY_SCHEME);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage(ZALOPAY_PACKAGE);
            activity.startActivity(intent);
        } catch (Exception e) {
            Log.e("PaymentHelper", "Lỗi khi mở ZaloPay", e);
        }
    }
}
