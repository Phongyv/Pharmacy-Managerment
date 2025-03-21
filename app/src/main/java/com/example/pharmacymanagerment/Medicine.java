package com.example.pharmacymanagerment;

public class Medicine {
    private String name;
    private int price;
    private String description;
    private String image_url;


    public Medicine() {
        // Constructor rỗng cần thiết cho Firebase
    }

    public Medicine(String name, int price, String description, String image_url) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.image_url = image_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_Url() {
        return image_url;
    }

    public void setUrl(String Image_url) {
        this.image_url = Image_url;
    }
}
