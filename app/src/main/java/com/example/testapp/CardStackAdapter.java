package com.example.testapp;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {

    private List<ItemModel> items;

    public CardStackAdapter(List<ItemModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(items.get(position));

        // Setting CLick Listener on Card View
        holder.image.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Launches Restaurant Info Activity
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                // Passing item view Model into restaurant info view model
                RestaurantInfoViewModel resViewModel = new RestaurantInfoViewModel(items.get(position));
                // Passing view RestaurantInfoViewModel into fragment
                Fragment resInfo = new RestaurantInfo(resViewModel);
                // Replacing view model with new fragment
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view,resInfo).addToBackStack(null).commit();
                Log.i("CARD", "onClick: CLICKED");

            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name, location, price_range;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
            location = itemView.findViewById(R.id.item_location);
            price_range = itemView.findViewById(R.id.item_price_range);
        }

        void setData(ItemModel data) {
            Picasso.get().load(data.getImage()).into(image);
            name.setText(data.getName());
            location.setText(data.getLocation());
            price_range.setText(data.getPrice_range());
        }
    }

    public List<ItemModel> getItems() {
        return items;
    }

    public void setItems(List<ItemModel> items) {
        this.items = items;
    }
}
