package com.example.pharmacymanagerment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class ZaloPayHelper {
    private static final String ZALO_PAY_SCHEME = "zalopay";
    private static final String ZALO_PAY_HOST = "paymentresult";

    private Activity activity;

    public ZaloPayHelper(Activity activity) {
        this.activity = activity;
    }

    /**
     * Gửi yêu cầu thanh toán đến ZaloPay
     *
     * @param amount    Số tiền cần thanh toán
     * @param appId     App ID từ ZaloPay
     * @param transId   Mã giao dịch duy nhất
     */
    public void startPayment(int amount, String appId, String transId) {
        try {
            String deepLink = "zalopay://app?app_id=" + appId +
                    "&amount=" + amount +
                    "&trans_id=" + transId +
                    "&callback_url=" + ZALO_PAY_SCHEME + "://" + ZALO_PAY_HOST;

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(deepLink));
            activity.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(activity, "Không thể mở ZaloPay!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Nhận phản hồi thanh toán từ ZaloPay
     *
     * @param intent Intent nhận được từ Deep Link
     */
    public void handlePaymentResult(Intent intent) {
        Uri data = intent.getData();
        if (data != null && ZALO_PAY_SCHEME.equals(data.getScheme()) && ZALO_PAY_HOST.equals(data.getHost())) {
            String status = data.getQueryParameter("status");
            String transId = data.getQueryParameter("transId");
            String amount = data.getQueryParameter("amount");

            if ("success".equals(status)) {
                Toast.makeText(activity, "Thanh toán thành công! Mã GD: " + transId, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(activity, "Thanh toán thất bại!", Toast.LENGTH_LONG).show();
            }
        }
    }
}


//💡 Trong Activity (MainActivity.java)
//public class MainActivity extends AppCompatActivity {
//    private ZaloPayHelper zaloPayHelper;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        zaloPayHelper = new ZaloPayHelper(this);
//        // Gửi yêu cầu thanh toán (ví dụ: 50,000 VNĐ)
//        zaloPayHelper.startPayment(50000, "your_app_id", "unique_trans_id");
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        zaloPayHelper.handlePaymentResult(intent);
//    }
//}
//💡 Trong Fragment (PaymentFragment.java)
//public class PaymentFragment extends Fragment {
//    private ZaloPayHelper zaloPayHelper;
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_payment, container, false);
//        zaloPayHelper = new ZaloPayHelper(getActivity());
//
//        Button payButton = view.findViewById(R.id.btnPay);
//        payButton.setOnClickListener(v -> zaloPayHelper.startPayment(100000, "your_app_id", "unique_trans_id"));
//
//        return view;
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        zaloPayHelper.handlePaymentResult(data);
//    }
//}