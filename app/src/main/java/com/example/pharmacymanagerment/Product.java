package com.example.pharmacymanagerment;

public class Product {
    private String name;
    private String img;
    private String type;
    private String des;
    private double price;

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Cần một constructor không tham số để Firestore sử dụng
    public Product() {}

    public Product(String name, String img, String type, String des, double price,String id) {
        this.name = name;
        this.img = img;
        this.type = type;
        this.des = des;
        this.price = price;
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}