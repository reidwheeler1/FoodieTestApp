package com.example.testapp;

public class ItemModel {
    private int image;
    private String name, location, price_range;

    //Set default values if ItemModel called without arguments
    public ItemModel() {

    }

    public ItemModel(int image, String name, String location, String price_range) {
        this.image = image;
        this.name = name;
        this.location = location;
        this.price_range = price_range;
    }

    public int getImage() { return image; }

    public String getName() { return name; }

    public String getLocation() { return location; }

    public String getPrice_range() { return price_range; }
}
