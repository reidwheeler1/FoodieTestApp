package com.example.testapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PreferenceList {
    private List<ItemModel> general;
    //private List<ItemModel> cultural_regional;
    private int gIndex = 0;
    //private int cIndex = 0;

    PreferenceList() {
        //Add General Categories
        //Testing now; Change to read from file later (When regional/ethnic categories are added)
        general = new ArrayList<>();
        general.add(new ItemModel(
                "https://images.unsplash.com/photo-1454117096348-e4abbeba002c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
                "Chicken", "", "", "chickenshop,chicken_wings"));
        general.add(new ItemModel(
                "https://images.unsplash.com/photo-1454117096348-e4abbeba002c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
                "Barbeque", "", "", "bbq"));
        general.add(new ItemModel(
                "https://images.unsplash.com/photo-1454117096348-e4abbeba002c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
                "Buffets", "", "", "buffets"));
        general.add(new ItemModel(
                "https://images.unsplash.com/photo-1454117096348-e4abbeba002c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
                "Burgers", "", "", "burgers"));
        general.add(new ItemModel(
                "https://images.unsplash.com/photo-1454117096348-e4abbeba002c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
                "Comfort Food", "", "", "comfortfood"));
        general.add(new ItemModel(
                "https://images.unsplash.com/photo-1454117096348-e4abbeba002c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
                "Fish & Chips", "", "", "fishnchips"));
        general.add(new ItemModel(
                "https://images.unsplash.com/photo-1454117096348-e4abbeba002c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
                "Fondue", "", "", "fondue"));
        general.add(new ItemModel(
                "https://images.unsplash.com/photo-1454117096348-e4abbeba002c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
                "Hot Dogs", "", "", "hotdogs"));
        general.add(new ItemModel(
                "https://images.unsplash.com/photo-1454117096348-e4abbeba002c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
                "Hot Pot", "", "", "hotpot"));
        general.add(new ItemModel(
                "https://images.unsplash.com/photo-1454117096348-e4abbeba002c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
                "Tacos", "", "", "tacos"));
        general.add(new ItemModel(
                "https://images.unsplash.com/photo-1454117096348-e4abbeba002c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
                "Noodles", "", "", "noodles"));
        general.add(new ItemModel(
                "https://images.unsplash.com/photo-1454117096348-e4abbeba002c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
                "Pizza", "", "", "pizza"));
        general.add(new ItemModel(
                "https://images.unsplash.com/photo-1454117096348-e4abbeba002c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
                "Salad", "", "", "salad"));
        general.add(new ItemModel(
                "https://images.unsplash.com/photo-1454117096348-e4abbeba002c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
                "Sandwiches", "", "", "sandwiches"));
        general.add(new ItemModel(
                "https://images.unsplash.com/photo-1454117096348-e4abbeba002c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
                "Seafood", "", "", "seafood"));
        general.add(new ItemModel(
                "https://images.unsplash.com/photo-1454117096348-e4abbeba002c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
                "Soup", "", "", "soup"));
        general.add(new ItemModel(
                "https://images.unsplash.com/photo-1454117096348-e4abbeba002c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
                "Steak", "", "", "steakhouses"));
        general.add(new ItemModel(
                "https://images.unsplash.com/photo-1454117096348-e4abbeba002c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
                "Sushi", "", "", "sushi"));
        general.add(new ItemModel(
                "https://images.unsplash.com/photo-1454117096348-e4abbeba002c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
                "Vegetarian", "", "", "vegetarian"));
        general.add(new ItemModel(
                "https://images.unsplash.com/photo-1454117096348-e4abbeba002c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
                "Wraps", "", "", "wraps"));

        //Shuffle PreferenceList
        Collections.shuffle(general);
    }

    //Will be useful if we add a reset button or if PreferenceList is held by MainActivity
    public void shuffleAndResetIndex() {
        gIndex = 0;
        Collections.shuffle(general);
    }

    public List<ItemModel> getPreferenceSubset() {
        //Need to handle situation where all preferences have been seen
        List<ItemModel> ret = new ArrayList<>();
        for (int i=0; i < 5 && gIndex < general.size(); i++, gIndex++)
            ret.add(general.get(gIndex));

        return ret;
    }
}
