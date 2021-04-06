package com.example.testapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.recyclerview.widget.DiffUtil;

public class ProfileCallback extends DiffUtil.Callback{
    HashMap<String, ArrayList<String>> oldListChild;
    HashMap<String, ArrayList<String>> newListChild;

    public ProfileCallback( HashMap<String, ArrayList<String>> oldListChild, HashMap<String, ArrayList<String>> newListChild) {
        this.oldListChild = oldListChild;
        this.newListChild = newListChild;
    }

    @Override
    public int getOldListSize() { return oldListChild.size(); }

    @Override
    public int getNewListSize() { return newListChild.size(); }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldListChild.get(oldItemPosition) == newListChild.get(newItemPosition);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldListChild.get(oldItemPosition) == newListChild.get(newItemPosition);
    }
}
