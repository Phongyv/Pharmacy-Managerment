package com.example.pharmacymanagerment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductAdapter extends BaseAdapter {
    private Context context;
    private List<Product> medicineList;

    public ProductAdapter(Context context, List<Product> medicineList) {
        this.context = context;
        this.medicineList = medicineList;
    }

    @Override
    public int getCount() {
        return medicineList.size();
    }

    @Override
    public Object getItem(int position) {
        return medicineList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        }

        ImageView imageView = view.findViewById(R.id.image_view);
        TextView nameView = view.findViewById(R.id.textView20);
        TextView priceView = view.findViewById(R.id.textView24);

        Product medicine = medicineList.get(position);

        // Sử dụng Glide để tải hình ảnh
        Glide.with(context).load(medicine.getImg()).into(imageView);

        nameView.setText(medicine.getName());
        priceView.setText(medicine.getPrice()+"đ");

        return view;
    }

}

