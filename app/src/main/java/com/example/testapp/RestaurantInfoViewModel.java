package com.example.testapp;

import android.content.ClipData;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RestaurantInfoViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    // Okhttp Client
    private final OkHttpClient client = new OkHttpClient();
    private String name;
    private String rating;
    private String type;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String resCat;
    private List<String> photos = new ArrayList<>();
    private ItemModel restaurant;

    public RestaurantInfoViewModel(ItemModel restaurant) {
        this.name = restaurant.getName();
        this.restaurant = restaurant;
        getMoreInfo(restaurant);
    }

    public String getName() {
        return name;
    }

    public String getRating() {
        return rating;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public ItemModel getRestaurant() {
        return restaurant;
    }

    public String getCat() {
        return resCat;
    }

    public String getAddress() {
        return address;
    }

    private void getMoreInfo(ItemModel restaurant){
        String yelpAPIKey = "ON2gpPfKlpMDaoU6OTZy-ES-ibzcfONKyS6VoTTdiVNjrN4rZ60Q3JUN-Lz_DKZtHDMfT6-MBhsTFrukQ-dTppuVw8wvuuUS6OufEsSleuD182x8fUiTYoZHt80uYHYx";
        String url = "https://api.yelp.com/v3/businesses/"+restaurant.getIdentifier();
        final String[] jsonResponse = new String[1];
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + yelpAPIKey)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    jsonResponse[0] = response.body().string();
                    Log.i("TEST", jsonResponse[0]);

                    // Parsing jsonResponse
                    JSONObject jsonObjResponse = new JSONObject(jsonResponse[0]);
                    phoneNumber = jsonObjResponse.getString("display_phone");
                    String reviewCount = jsonObjResponse.getString("review_count");
                    String rate = jsonObjResponse.getString("rating");
                    rating = "Rating: " + rate + " / 5 "+ "(" + reviewCount + " Reviews)";
                    JSONArray jsonPhotosArray = jsonObjResponse.getJSONArray("photos");
                    for (int i = 0; i < jsonPhotosArray.length(); i++) {
                        photos.add(jsonPhotosArray.getString(i));
                    }
                    JSONArray jsonCat = jsonObjResponse.getJSONArray("categories");
                    resCat = jsonCat.getJSONObject(0).getString("title") + ", ";
                    resCat = resCat + jsonCat.getJSONObject(1).getString("title");

                    JSONObject jsonLocation = jsonObjResponse.getJSONObject("location");
                    address = jsonLocation.getString("address1") + " " + jsonLocation.getString("address2")+ " " + jsonLocation.getString("address3")
                            + "\n" + jsonLocation.getString("city")+ " " + jsonLocation.getString("zip_code")+ " " + jsonLocation.getString("state");
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();

        try {
            t.join();
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }
    }
}