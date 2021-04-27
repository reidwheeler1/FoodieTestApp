package com.example.testapp;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.RelativeLayout;

import com.yuyakaido.android.cardstackview.CardStackView;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DiscoverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DiscoverFragment extends Fragment {

    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    // Okhttp Client
    private final OkHttpClient client = new OkHttpClient();
    private RelativeLayout progressBar;
    RecyclerView gridData;
    DiscoverAdapter _adapter;

    public DiscoverFragment() {
        // Required empty public constructor
    }


    public static DiscoverFragment newInstance() {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_discover, container, false);
        progressBar = root.findViewById(R.id.discoverProgressPanel);
        init(root);
        return root;
    }

    //TODO: Fix error "RecyclerView: No adapter attached; skipping layout
    private void init(View root) {
        progressBar.setVisibility(View.VISIBLE);
        gridData = root.findViewById(R.id.discover_recyclerView);
        List<ItemModel> items = new ArrayList<>();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);
        gridData.setLayoutManager(gridLayoutManager);
        final int spacing = 0;
        gridData.setPadding(spacing, spacing, spacing, spacing);
        gridData.setClipToPadding(false);
        gridData.setClipChildren(false);
        gridData.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.set(spacing, spacing, spacing, spacing);
            }
        });
        gridData.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) { //check for scroll down
                    visibleItemCount = gridLayoutManager.getChildCount();
                    totalItemCount = gridLayoutManager.getItemCount();
                    pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            Log.v("...", "Last Item Wow !");
                            progressBar.setVisibility(View.VISIBLE);
                            // Do pagination.. i.e. fetch new data
                            new Thread(() -> {
                                List<ItemModel> newItems = fetchYelp();
                                items.addAll(newItems);
                                DiscoverAdapter adapter = new DiscoverAdapter(items);
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    gridData.setAdapter(adapter);
                                    loading = true;
                                    progressBar.setVisibility(View.GONE);
                                });
                            }).start();
                        }
                    }
                }
            }
        });
        new Thread(() -> {
            List<ItemModel> _items = fetchYelp();
            DiscoverAdapter adapter = new DiscoverAdapter(_items);
            new Handler(Looper.getMainLooper()).post(() -> {
                gridData.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            });
        }).start();
    }



    private List<ItemModel> fetchYelp() {


        List<ItemModel> items = new ArrayList<>();

        String yelpAPIKey = "ON2gpPfKlpMDaoU6OTZy-ES-ibzcfONKyS6VoTTdiVNjrN4rZ60Q3JUN-Lz_DKZtHDMfT6-MBhsTFrukQ-dTppuVw8wvuuUS6OufEsSleuD182x8fUiTYoZHt80uYHYx";
        String categories = "&categories=";
        String preferences = ProfileFragment.getDietaryPreferencesString();
        categories = (!preferences.isEmpty()) ? categories + preferences.substring(1) : categories;
        String url = "https://api.yelp.com/v3/businesses/search?location="+MainActivity.getPostalcode()+"&sort_by=rating&limit=50"+categories;
        final String[] jsonResponse = new String[1];

/*        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {*/
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + yelpAPIKey)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }
                    jsonResponse[0] = response.body().string();
                    Log.i("TEST", jsonResponse[0]);

                    // Parsing jsonResponse
                    JSONObject jsonObjResponse = new JSONObject(jsonResponse[0]);
                    JSONArray jsonArray = jsonObjResponse.getJSONArray("businesses");


                    for (int i = 0; i < jsonArray.length(); i++) {
                        String name = jsonArray.getJSONObject(i).getString("name");
                        String price = jsonArray.getJSONObject(i).optString("price");
                        String location = jsonArray.getJSONObject(i).getJSONObject("location").getString("address1");
                        String image_url = jsonArray.getJSONObject(i).getString("image_url");
                        String id = jsonArray.getJSONObject(i).getString("id");
                        items.add(new ItemModel(image_url,name,location,price,id));
                    }


                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
/*            }
        });*/

/*        t.start();

        try {
            t.join();
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }*/

        return items;
    }
}