package com.example.pharmacymanagerment;

import static android.widget.Toast.LENGTH_SHORT;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    VideoView videoView;
    TextView titleTextView;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_PharmacyManagerment);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //logic login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        View signInButton = findViewById(R.id.view13);
        signInButton.setOnClickListener(view -> signIn());

        //animation login
        titleTextView = findViewById(R.id.textView31);
        titleTextView.setTranslationY(-200f); // Thiết lập vị trí ban đầu ở trên 200 pixels so với vị trí ban đầu
        // Tạo hiệu ứng di chuyển từ trên xuống cho TextView
        ObjectAnimator moveAnimator = ObjectAnimator.ofFloat(titleTextView, "translationY", -200f, 0f); // Di chuyển từ -200 đến 0
        moveAnimator.setDuration(3000); // Thời gian hoạt hình 1000ms (1 giây)
        moveAnimator.start();
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        titleTextView.startAnimation(fadeInAnimation); // Bắt đầu hoạt họa cho TextView

        //background video
        videoView = findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.backgroud); // Thay "your_video_file" bằng tên file video của bạn
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(mp -> mp.setLooping(true)); // Lặp video
        videoView.start(); // Bắt đầu phát video
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Đăng nhập thành công, chuyển đến màn hình chính
            Toast.makeText(this,"Đăng nhập thành công",LENGTH_SHORT).show();
            if (account != null) {
                String userEmail = account.getEmail(); // Lấy email của người đăng nhập
                db.collection("admins")
                        .whereEqualTo("email", userEmail)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("LoginActivity", "Admin found: " + document.getData());
                                }
                                if (!task.getResult().isEmpty()) {
                                    Log.d("LoginActivity", "User is Admin!");
                                    startActivity(new Intent(this, Admin.class));
                                } else {
                                    Log.d("LoginActivity", "User is NOT Admin!");
                                    startActivity(new Intent(this, MainActivity.class));
                                }
                            } else {
                                Log.e("LoginActivity", "Firestore query failed", task.getException());
                            }
                            finish();
                        });

            }


        } catch (ApiException e) {
            Toast.makeText(this,"Đăng nhập thất bại",LENGTH_SHORT).show();
            Log.w("LoginActivity", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
