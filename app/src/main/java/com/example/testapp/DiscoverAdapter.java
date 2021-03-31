package com.example.testapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.ViewHolder> {


    private List<ItemModel> restaurants;


    public DiscoverAdapter(List<ItemModel> items) {
        this.restaurants = items;
        Log.i("DISCOVER ADAPTER", "THERE ARE " + restaurants.size() + " RESTAURANTS LOADED");
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.discover_grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(restaurants.get(position));

        // Setting CLick Listener on Card View
        holder.gridImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Launches Restaurant Info Activity
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                // Passing item view Model into restaurant info view model
                RestaurantInfoViewModel resViewModel = new RestaurantInfoViewModel(restaurants.get(position));
                // Passing view RestaurantInfoViewModel into fragment
                Fragment resInfo = new RestaurantInfo(resViewModel);
                // Replacing view model with new fragment
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view,resInfo).addToBackStack(null).commit();
                Log.i("DISCOVER", "onClick: CLICKED");

            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView gridImage;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            gridImage = itemView.findViewById(R.id.discover_restImage);
        }

        void setData(ItemModel data) {
            //Log.i("DISCOVER ADAPTER SET DATA", data.getImage());
            Picasso.get().load(data.getImage()).into(gridImage);
            //gridImage.setImageResource(R.drawable.bagel);
        }
    }

}
