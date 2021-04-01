package com.example.testapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class RestaurantInfo extends Fragment {

    private RestaurantInfoViewModel mViewModel;
    public RestaurantInfo(RestaurantInfoViewModel itemModel){
        this.mViewModel = itemModel;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.restaurant_info_fragment, container, false);
        init(root);
        return root;

    }

    private void init(View root) {
        ViewPager2 viewPager = root.findViewById(R.id.restViewPager2);
        TextView resName = root.findViewById(R.id.resName);
        TextView resRating = root.findViewById(R.id.resRating);
        TextView resCat = root.findViewById(R.id.resCat);
        TextView price = root.findViewById(R.id.resPrice);
        TextView phoneNumber = root.findViewById(R.id.resPhoneNumber);
        TextView resAddress = root.findViewById(R.id.resAddress);

        RestaurantInfoAdapter adapter = new RestaurantInfoAdapter(mViewModel.getPhotos());
        viewPager.setAdapter(adapter);
        resName.setText(mViewModel.getName());
        resRating.setText(mViewModel.getRating());
        resCat.setText(mViewModel.getCat());
        price.setText(mViewModel.getRestaurant().getPrice_range());
        phoneNumber.setText(mViewModel.getPhoneNumber());
        resAddress.setText(mViewModel.getAddress());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // mViewModel = new ViewModelProvider(this).get(RestaurantInfoViewModel.class);
        // TODO: Use the ViewModel
    }



}