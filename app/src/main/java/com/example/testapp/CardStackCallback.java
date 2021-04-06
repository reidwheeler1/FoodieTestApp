package com.example.testapp;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

//DiffUtil.Callback: https://developer.android.com/reference/android/support/v7/util/DiffUtil.Callback
public class CardStackCallback extends DiffUtil.Callback {

    private List<ItemModel> old_item, new_item;

    public CardStackCallback(List<ItemModel> old_item, List<ItemModel> new_item) {
        this.old_item = old_item;
        this.new_item = new_item;
    }

    @Override
    public int getOldListSize() { return old_item.size(); }

    @Override
    public int getNewListSize() { return new_item.size(); }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        boolean areNamesEqual = old_item.get(oldItemPosition).getName().equals(new_item.get(newItemPosition).getName());
        boolean areImageEqual = old_item.get(oldItemPosition).getImage().equals(new_item.get(newItemPosition).getImage());
        return (areNamesEqual && areImageEqual);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return old_item.get(oldItemPosition) == new_item.get(newItemPosition);
    }
}
