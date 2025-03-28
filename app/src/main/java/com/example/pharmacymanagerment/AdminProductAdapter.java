package com.example.pharmacymanagerment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.pharmacymanagerment.R;
import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder> {
    private Context context;
    private List<AdminProduct> productList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onActionClick(AdminProduct adminproduct);
    }

    public AdminProductAdapter(Context context, List<AdminProduct> productList, OnItemClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        AdminProduct product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.format("%.0fđ", product.getPrice()));

        // Load ảnh bằng Glide
        Glide.with(context)
                .load(product.getImg())
                .placeholder(R.drawable.placeholder)
                .into(holder.productImage);

        holder.btnAction.setOnClickListener(v -> onItemClickListener.onActionClick(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice;
        Button btnAction;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            btnAction = itemView.findViewById(R.id.btnAction);
        }
    }
}
