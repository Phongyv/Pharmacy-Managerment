package com.example.pharmacymanagerment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<Map<String, Object>> cartItems;
    private FirebaseFirestore db;
    private String userId;
    private Context context;

    private GoogleSignInAccount account;


    public CartAdapter(Context context,List<Map<String, Object>> cartItems,String userId) {
        this.cartItems = cartItems;
        this.userId = userId;
        this.context = context;
        db = FirebaseFirestore.getInstance();
        if (context != null) {
            account = GoogleSignIn.getLastSignedInAccount(context);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> item = cartItems.get(position);

        holder.productId.setText("ID: " + item.get("productId"));
        holder.productName.setText(item.get("name") + "");
        holder.price.setText(item.get("price") + " đ");

        Glide.with(holder.img.getContext())
                .load(item.get("img"))
                .apply(new RequestOptions().transform(new RoundedCorners(20))) // Cách mới để bo góc ảnh
                .into(holder.img);

        // Xử lý sự kiện khi nhấn nút "Xóa"
        holder.btndelete.setOnClickListener(v -> {
            String productIdToRemove = item.get("productId").toString();
            removeItemFromFirestore(productIdToRemove, position);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productId, productName, price;
        ImageView img, btndelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productId = itemView.findViewById(R.id.productId);
            productName = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            img = itemView.findViewById(R.id.imageView30);
            btndelete = itemView.findViewById(R.id.imageView31);
        }
    }

    // Hàm xóa sản phẩm khỏi Firestore
    private void removeItemFromFirestore(String productId, int position) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> cart = (List<String>) documentSnapshot.get("cart");

                        if (cart != null && cart.contains(productId)) {
                            cart.remove(productId); // Xóa sản phẩm theo ID

                            // Cập nhật lại giỏ hàng trên Firestore
                            db.collection("users").document(userId)
                                    .update("cart", cart)
                                    .addOnSuccessListener(aVoid -> {
                                        cartItems.remove(position);
                                        notifyItemRemoved(position);
                                        Log.d("Firestore", "Sản phẩm đã được xóa thành công");
                                    })
                                    .addOnFailureListener(e -> Log.e("Firestore", "Lỗi khi cập nhật giỏ hàng", e));
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Lỗi khi lấy giỏ hàng", e));
    }
}
