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
    private static final String MOMO_SCHEME = "momo://";
    private static final String ZALOPAY_PACKAGE = "vn.com.vng.zalopay";
    private static final String ZALOPAY_SCHEME = "zalopay://";

    private Activity activity;
    Context context;

    public PaymentHelper(Activity activity,Context context) {
        this.activity = activity;
        this.context = context;
    }

    // Kiểm tra MoMo đã được cài đặt chưa
    private boolean isMoMoInstalled() {
        PackageManager packageManager = activity.getPackageManager();
        Intent momoIntent = packageManager.getLaunchIntentForPackage(MOMO_PACKAGE);
        return momoIntent != null;
    }

    // Kiểm tra ZaloPay đã được cài đặt chưa
    private boolean isZaloPayInstalled() {
        PackageManager packageManager = activity.getPackageManager();
        Intent zaloIntent = packageManager.getLaunchIntentForPackage(ZALOPAY_PACKAGE);
        return zaloIntent != null;
    }

    // Chuyển tiền qua MoMo
    public void payWithMoMo(String receiverPhone, String amount, String message) {
        if (!isMoMoInstalled()) {
            NotificationHelper notificationHelper = new NotificationHelper(context);
            notificationHelper.showNotification("Pharmacy Managerment","MoMo chưa được cài đặt!" );
            Toast.makeText(activity, "MoMo chưa được cài đặt!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Uri uri = Uri.parse(MOMO_SCHEME + "app?action=transfer" +
                    "&amount=" + amount +
                    "&receiver=" + receiverPhone +
                    "&message=" + Uri.encode(message));

            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage(MOMO_PACKAGE);
            activity.startActivity(intent);
        } catch (Exception e) {
            Log.e("PaymentHelper", "Lỗi khi mở MoMo", e);
        }
    }

    // Chuyển tiền qua ZaloPay
    public void payWithZaloPay(String receiverPhone, String amount, String message) {
        if (!isZaloPayInstalled()) {
            NotificationHelper notificationHelper = new NotificationHelper(context);
            notificationHelper.showNotification("Pharmacy Managerment","ZaloPay chưa được cài đặt!" );
            Toast.makeText(activity, "ZaloPay chưa được cài đặt!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String deepLink = ZALOPAY_SCHEME + "app?action=transfer" +
                    "&amount=" + amount +
                    "&receiver=" + receiverPhone +
                    "&message=" + Uri.encode(message) +
                    "&callback_url=zalopay://callback";

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(deepLink));
            intent.setPackage(ZALOPAY_PACKAGE);
            activity.startActivity(intent);
        } catch (Exception e) {
            Log.e("PaymentHelper", "Lỗi khi mở ZaloPay", e);
        }
    }

    // Xử lý phản hồi từ MoMo và ZaloPay
    public void handlePaymentResponse(Intent intent) {
        if (intent == null || intent.getData() == null) return;
        Uri data = intent.getData();

        if ("momo".equals(data.getScheme())) {
            // Xử lý phản hồi từ MoMo
            String status = data.getQueryParameter("status");
            String message = data.getQueryParameter("message");

            if ("0".equals(status)) {
                Toast.makeText(activity, "Thanh toán MoMo thành công!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Thất bại: " + message, Toast.LENGTH_SHORT).show();
            }
        } else if ("zalopay".equals(data.getScheme())) {
            // Xử lý phản hồi từ ZaloPay
            String status = data.getQueryParameter("status");
            String message = data.getQueryParameter("message");
            String transId = data.getQueryParameter("transId");

            if ("0".equals(status)) {
                Toast.makeText(activity, "Thanh toán ZaloPay thành công! Mã GD: " + transId, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(activity, "Thất bại: " + message, Toast.LENGTH_LONG).show();
            }
        }
    }
}

//private PaymentHelper paymentHelper;
//paymentHelper = new PaymentHelper(requireActivity());
// paymentHelper.payWithMoMo("0338191380", "100000", "Thanh toán đơn hàng");
//        paymentHelper.payWithZaloPay("0338191380", "100000", "Thanh toán đơn hàng");
// @Override
//    public void onResume() {
//        super.onResume();
//        Intent intent = requireActivity().getIntent();
//        if (intent != null) {
//            paymentHelper.handlePaymentResponse(intent);
//            requireActivity().setIntent(null); // Xóa intent để tránh xử lý lại khi quay lại Fragment
//        }
//    }