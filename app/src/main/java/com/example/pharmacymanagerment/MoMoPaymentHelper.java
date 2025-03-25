package com.example.pharmacymanagerment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

public class MoMoPaymentHelper {
    private static final String MOMO_SCHEME = "momo://";
    private static final String MOMO_PACKAGE = "com.mservice.momotransfer";

    private Activity activity;

    public MoMoPaymentHelper(Activity activity) {
        this.activity = activity;
    }

    // Kiểm tra MoMo đã được cài đặt chưa
    public boolean isMoMoInstalled() {
        PackageManager packageManager = activity.getPackageManager();
        Intent momoIntent = packageManager.getLaunchIntentForPackage(MOMO_PACKAGE);
        return momoIntent != null;
    }

    // Chuyển tiền qua MoMo
    public void transferMoney(String receiverPhone, String amount, String message) {
        if (!isMoMoInstalled()) {
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
            Log.e("MoMoPaymentHelper", "Lỗi khi gọi MoMo", e);
        }
    }

    // Xử lý phản hồi từ MoMo sau khi giao dịch
    public void handleMoMoResponse(Intent intent) {
        Uri data = intent.getData();
        if (data != null && "momo".equals(data.getScheme())) {
            String status = data.getQueryParameter("status");
            String message = data.getQueryParameter("message");

            if ("0".equals(status)) {
                // Giao dịch thành công
                Toast.makeText(activity, "Chuyển tiền thành công!", Toast.LENGTH_SHORT).show();
            } else {
                // Giao dịch thất bại
                Toast.makeText(activity, "Thất bại: " + message, Toast.LENGTH_SHORT).show();
            }
        }
    }
}

// Khởi tạo và gọi hàm trong Activity
//Khi người dùng nhấn nút để thực hiện thanh toán, hãy gọi MoMo:
//MoMoPaymentHelper momoHelper = new MoMoPaymentHelper(this);
//Button btnTransfer = findViewById(R.id.btn_transfer);
//btnTransfer.setOnClickListener(v -> {
// momoHelper.transferMoney("0912345678", "50000", "Thanh toán MoMo");
//});


//Nhận phản hồi trong onNewIntent()
//Sau khi giao dịch hoàn tất, phản hồi từ MoMo sẽ được xử lý:
//@Override
//protected void onNewIntent(Intent intent) {
//    super.onNewIntent(intent);
//    setIntent(intent);
//    // Xử lý phản hồi từ MoMo
//    MoMoPaymentHelper momoHelper = new MoMoPaymentHelper(this);
//    momoHelper.handleMoMoResponse(intent);
//}
//Lưu ý: Vì Fragment không có onNewIntent(), nên ta dùng onResume() để kiểm tra phản hồi từ MoMo.