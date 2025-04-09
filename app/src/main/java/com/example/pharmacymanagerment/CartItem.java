package com.example.pharmacymanagerment;

public class CartItem {
    public String name;
    public double price;

    public CartItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return name + " - " + price + " VNĐ";
    }
}
