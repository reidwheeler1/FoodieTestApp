package com.example.testapp;

import android.content.ClipData;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
    private String resCat;
    private String[] photos;
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

    public ItemModel getRestaurant() {
        return restaurant;
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

                    Headers responseHeaders = response.headers();
                    for (int i = 0; i < responseHeaders.size(); i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }

                    jsonResponse[0] = response.body().string();
                    Log.i("TEST", jsonResponse[0]);

                    // Parsing jsonResponse
                    JSONObject jsonObjResponse = new JSONObject(jsonResponse[0]);
                    phoneNumber = jsonObjResponse.getString("display_phone");
                    String reviewCount = jsonObjResponse.getString("review_count");
                    String rate = jsonObjResponse.getString("rating");
                    rating = "Rating: " + rate + " / 5 "+ "(" + reviewCount + " Reviews)";


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