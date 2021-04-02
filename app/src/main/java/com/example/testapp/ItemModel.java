package com.example.testapp;

import java.util.ArrayList;
import java.util.List;

public class ItemModel {
    private String name, location, price_range, image, identifier;
    private List<String> categories;

    //Set default values if ItemModel called without arguments
    public ItemModel() {


    }

    public ItemModel(String image, String name, String price_range, String location) {
        this.image = image;
        this.name = name;
        this.location = location;
        this.price_range = price_range;
        this.identifier = "No identifier given";
        this.categories = null; //Probably should change this later
    }

    public ItemModel(String image, String name, String location, String price_range, String identifier) {
        this.image = image;
        this.name = name;
        this.location = location;
        this.price_range = price_range;
        this.identifier = identifier;
        this.categories = null; //Probably should change this later
    }

    public ItemModel(String image, String name, String location, String price_range, String identifier, List<String> categories) {
        this.image = image;
        this.name = name;
        this.location = location;
        this.price_range = price_range;
        this.identifier = identifier;
        this.categories = new ArrayList<>(categories);
    }

    public String getImage() { return image; }

    public String getName() { return name; }

    public String getLocation() { return location; }

    public String getPrice_range() { return price_range; }

    public String getIdentifier() { return identifier; }

    public List<String> getCategories() { return categories; }
}
