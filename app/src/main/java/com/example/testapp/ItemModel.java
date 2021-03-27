package com.example.testapp;

public class ItemModel {
    private String name, location, price_range, image, id;

    //Set default values if ItemModel called without arguments
    public ItemModel() {

    }

    public ItemModel(String image, String name, String price_range, String location, String id) {
        this.image = image;
        this.name = name;
        this.location = location;
        this.price_range = price_range;
        this.id = id;
    }

    public String getImage() { return image; }

    public String getName() { return name; }

    public String getLocation() { return location; }

    public String getPrice_range() { return price_range; }

    public String getId() {return id ;};
}
