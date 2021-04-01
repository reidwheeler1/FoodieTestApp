package com.example.testapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RestaurantInfoAdapter extends RecyclerView.Adapter<RestaurantInfoAdapter.ViewHolder> {


    private List<String> restaurantImages;


    public RestaurantInfoAdapter(List<String> items) {
        this.restaurantImages = items;
        Log.i("DISCOVER ADAPTER", "THERE ARE " + restaurantImages.size() + " RESTAURANTS LOADED");
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.restaurant_info_slider_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(restaurantImages.get(position));
    }

    @Override
    public int getItemCount() {
        return restaurantImages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.restViewPagerImage);
        }

        void setData(String data) {
            //Log.i("DISCOVER ADAPTER SET DATA", data.getImage());
            Picasso.get().load(data).into(image);
            //gridImage.setImageResource(R.drawable.bagel);
        }
    }

}