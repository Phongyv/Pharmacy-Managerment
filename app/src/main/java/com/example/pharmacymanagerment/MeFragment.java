package com.example.pharmacymanagerment;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.LENGTH_SHORT;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class MeFragment extends Fragment {
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);

        //get userdata
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(requireContext());
        if (account != null) {
            // Hiển thị thông tin người dùng
            String id = account.getId(); // id
            String name = account.getDisplayName(); // Tên hiển thị
            String email = account.getEmail(); // Địa chỉ email
            String photoUrl = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : null;

            //render data
            TextView textView32 = view.findViewById(R.id.textView32);
            TextView textView33 = view.findViewById(R.id.textView33);
            TextView textView34 = view.findViewById(R.id.textView34);
            textView32.setText(id);
            textView33.setText(name);
            textView34.setText(email);
            ImageView imageView28 = view.findViewById(R.id.imageView28);
            if (photoUrl != null){
                Glide.with(requireContext())
                        .load(photoUrl)
                        .transform(new RoundedCornersTransformation(50)) // Chỉnh sửa 30 để thay đổi độ bo tròn
                        .into(imageView28);
            } else{
                Glide.with(requireContext())
                        .load(R.drawable.medical_cross)
                        .into(imageView28);
            }
        } // Ảnh đại diện

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        //handle event logout
        Button logout = view.findViewById(R.id.button2);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });


        return view;
    }
    private void signOut() {
        mGoogleSignInClient.signOut().addOnCompleteListener(requireActivity(), task -> {
            Toast.makeText(requireContext(),"Đăng xuất thành công",LENGTH_SHORT).show();
            Log.d("ProfileFragment", "Đăng xuất thành công");
            // Quay trở lại LoginActivity
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Xác nhận đăng xuất");
        builder.setMessage("Bạn có chắc chắn muốn đăng xuất?");

        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                signOut();
            }
        });

        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Đóng hộp thoại
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

