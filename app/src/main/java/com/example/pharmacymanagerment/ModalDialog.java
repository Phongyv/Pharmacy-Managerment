package com.example.pharmacymanagerment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.pharmacymanagerment.R;

public class ModalDialog {
    private final Dialog dialog;
    private TextView tvTitle, tvMessage;
    private Button btnClose, btnOk;

    public ModalDialog(Context context) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_modal);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Làm trong suốt nền
        dialog.setCancelable(true); // Cho phép bấm ra ngoài để tắt

        // Ánh xạ các view
        tvTitle = dialog.findViewById(R.id.tvTitle);
        tvMessage = dialog.findViewById(R.id.tvMessage);
        btnClose = dialog.findViewById(R.id.btnClose);
        btnOk = dialog.findViewById(R.id.btnOk);

        // Mặc định nút "Đóng" sẽ tắt modal
        btnClose.setOnClickListener(v -> dismiss());
    }

    public ModalDialog setTitle(String title) {
        tvTitle.setText(title);
        return this;
    }

    public ModalDialog setMessage(String message) {
        tvMessage.setText(message);
        return this;
    }

    public ModalDialog setCloseButtonText(String text) {
        btnClose.setText(text);
        return this;
    }

    public ModalDialog setOkButtonText(String text) {
        btnOk.setText(text);
        return this;
    }

    public ModalDialog setCloseButtonListener(View.OnClickListener listener) {
        btnClose.setOnClickListener(listener);
        return this;
    }

    public ModalDialog setOkButtonListener(View.OnClickListener listener) {
        btnOk.setOnClickListener(listener);
        return this;
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}


//                 new ModalDialog(this)
//                .setTitle("Xác nhận")
//                .setMessage("Bạn có chắc chắn muốn tiếp tục?")
//                .setCloseButtonText("Hủy")
//                .setOkButtonText("Đồng ý")
//                .setCloseButtonListener(v1 -> {
//                    Toast.makeText(this, "Đã hủy!", Toast.LENGTH_SHORT).show();
//                })
//                .setOkButtonListener(v2 -> {
//                    Toast.makeText(this, "Bạn đã đồng ý!", Toast.LENGTH_SHORT).show();
//                })
//                .show();
