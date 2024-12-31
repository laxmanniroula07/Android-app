package com.example.Circuit;

public class Item {
    private String id;
    private String imageUrl;
    private String title;
    private double price;
    private String location;
    private String description;

    public Item() {
        // Default constructor required for calls to DataSnapshot.getValue(Item.class)
    }

    public Item(String id, String imageUrl, String title, double price, String location, String description) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.price = price;
        this.location = location;
        this.description = description;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
