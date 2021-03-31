package com.example.testapp;

public class ItemModel {
    private String name, location, price_range, image, identifier;

    //Set default values if ItemModel called without arguments
    public ItemModel() {


    }

    public ItemModel(String image, String name, String price_range, String location) {
        this.image = image;
        this.name = name;
        this.location = location;
        this.price_range = price_range;
        this.identifier = "No identifier given";
    }

    public ItemModel(String image, String name, String location, String price_range, String identifier) {
        this.image = image;
        this.name = name;
        this.location = location;
        this.price_range = price_range;
        this.identifier = identifier;
    }

    public String getImage() { return image; }

    public String getName() { return name; }

    public String getLocation() { return location; }

    public String getPrice_range() { return price_range; }

    public String getIdentifier() { return identifier; }
}
