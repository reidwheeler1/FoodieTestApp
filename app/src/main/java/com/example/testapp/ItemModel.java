package com.example.testapp;

import java.util.ArrayList;
import java.util.List;

public class ItemModel {
    private String name, location, price_range, image, identifier;
    private List<String> categories;
    private static final String defaultImage = "https://images.unsplash.com/photo-1506159904226-d6cfd457c30c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80";

    //Set default values if ItemModel called without arguments
    public ItemModel() {


    }

    public ItemModel(String image, String name, String price_range, String location) {
        if (!image.equals(""))
            this.image = image;
        else this.image = defaultImage;
        this.name = name;
        this.location = location;
        this.price_range = price_range;
        this.identifier = "No identifier given";
        this.categories = null; //Probably should change this later
    }

    public ItemModel(String image, String name, String location, String price_range, String identifier) {
        if (!image.equals(""))
            this.image = image;
        else this.image = defaultImage;
        this.name = name;
        this.location = location;
        this.price_range = price_range;
        this.identifier = identifier;
        this.categories = null; //Probably should change this later
    }

    public ItemModel(String image, String name, String location, String price_range, String identifier, List<String> categories) {
        if (!image.equals(""))
            this.image = image;
        else this.image = defaultImage;
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

    public String printItem(){
        String additem = this.getImage();
        additem += "," + this.getName();
        additem += "," + this.getLocation();
        additem += "," + this.getPrice_range();
        additem += "," + this.getIdentifier() + "\n";
        return additem;
    }

    public List<String> getCategories() { return categories; }

}
