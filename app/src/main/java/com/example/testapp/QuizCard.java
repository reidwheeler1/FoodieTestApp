package com.example.testapp;

import android.content.ClipData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuizCard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizCard extends Fragment {

    private static final String TAG = QuizCard.class.getSimpleName();
    private RelativeLayout progressBar;
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    private List<ItemModel> itemModelReference;
    //
    private boolean gatheringPreferences = true; //Set to false in onCardSwiped() if paginating
    private List<ItemModel> likedPreferences;

    // Okhttp Client
    private final OkHttpClient client = new OkHttpClient();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //GPS variables
    static String postalcode;

    public QuizCard() {
        super(R.layout.fragment_quiz_card);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuizCard.
     */
    // TODO: Rename and change types and number of parameters
    public static QuizCard newInstance(String param1, String param2) {
        QuizCard fragment = new QuizCard();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_quiz_card, container, false);
        progressBar = root.findViewById(R.id.loadingPanel);
        init(root);
        return root;
    }

    private void init(View root) {
        CardStackView cardStackView = root.findViewById(R.id.card_stack_view);
        likedPreferences = new ArrayList<>();
        manager = new CardStackLayoutManager(getContext(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                //Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " count=" + adapter.getItemCount() + " d=" + direction);
                if (direction == Direction.Right && gatheringPreferences) {
                    likedPreferences.add(itemModelReference.get(manager.getTopPosition()-1));
                }
                if (direction == Direction.Right && !gatheringPreferences) {
                    //likedPreferences.add(itemModelReference.get(manager.getTopPosition()-1));
                    MainActivity.likes += itemToString(adapter.getItems().get(manager.getTopPosition()-1));
                }

                //If getTopPosition == original item count, paginate card stack
                //Can optionally choose not to paginate; instead, lock card movement with
                //prompt to reset by tapping suggestions on the bottom nav bar
                if (manager.getTopPosition() == adapter.getItemCount()) {

                    gatheringPreferences = false;
                    paginate(); //Paginating: see function definition below
                }
            }

            @Override
            public void onCardRewound() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardCanceled() {
                Log.d(TAG, "onCardCanceled: " + manager.getTopPosition());
            }

            @Override
            public void onCardAppeared(View view, int position) {
                TextView textView = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", name: " + textView.getText());
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                TextView textView = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", name: " + textView.getText());
            }
        });
        //Modify carding swiping UX here:
        manager.setStackFrom(StackFrom.Top);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(4.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);

        manager.setDirections(Direction.HORIZONTAL); //Other options: VERTICAL, FREEDOM
        manager.setCanScrollHorizontal(true); //Enables card to move on horizontal axis
        //manager.setCanScrollVertical(true); //Enables card to move on vertical axis, regardless of set Direction


        //SwipeableMethod determines whether buttons are used (Automatic) or touch-and-drag (Manual)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);

        manager.setOverlayInterpolator(new LinearInterpolator());
        adapter = new CardStackAdapter(addPreferenceList());
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
    }

    //When current card stack is near end, integrate new items into stack
    private void paginate() {
        //Start spinner
        progressBar.setVisibility(View.VISIBLE);
        //new thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                //See: https://developer.android.com/reference/android/support/v7/util/DiffUtil
                List<ItemModel> old_items = adapter.getItems();
                List<ItemModel> new_items = new ArrayList<>(addList());
                CardStackCallback callback = new CardStackCallback(old_items, new_items);
                DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
                adapter.setItems(new_items);
                //run below on UI thread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                   @Override
                   public void run() {
                       result.dispatchUpdatesTo(adapter);
                       progressBar.setVisibility(View.GONE);
                   }
                });
            }
        }).start();
    }

    /*Creates a list of 5 preference options used to customize restaurant search*/
    private List<ItemModel> addPreferenceList() {
        itemModelReference = new ArrayList<>();
        itemModelReference.add(new ItemModel(
                "https://images.unsplash.com/photo-1501200291289-c5a76c232e5f?ixlib=rb-1.2.1&ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&auto=format&fit=crop&w=634&q=80",
                "Chicken", "", "", "chicken_wings"));
        itemModelReference.add(new ItemModel(
                "https://images.unsplash.com/photo-1565299715199-866c917206bb?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=714&q=80",
                "Steak", "", "", "steak"));
        itemModelReference.add(new ItemModel(
                "https://images.unsplash.com/photo-1587841424505-4205a6e73ef7?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80",
                "Burgers", "", "", "burgers"));

        return itemModelReference;
    }

    private String constructURL() {
        String url = "&categories=";
        url = url + likedPreferences.get(0).getIdentifier();
        for (int i=1; i < likedPreferences.size(); i++)
            url = url + "," + likedPreferences.get(i).getIdentifier();
        return url;
    }

    private List<ItemModel> addList() {
        List<ItemModel> items = new ArrayList<>();
        //Add items here

        String yelpAPIKey = "ON2gpPfKlpMDaoU6OTZy-ES-ibzcfONKyS6VoTTdiVNjrN4rZ60Q3JUN-Lz_DKZtHDMfT6-MBhsTFrukQ-dTppuVw8wvuuUS6OufEsSleuD182x8fUiTYoZHt80uYHYx";
        //      Searches businesses with location query of zip code 33620 (USF Zip)
        String url = "https://api.yelp.com/v3/businesses/search?location=" + MainActivity.getPostalcode() + constructURL();
        Log.i("addList()", url);
        final String[] jsonResponse = new String[1];

                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization", "Bearer " + yelpAPIKey)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0; i < responseHeaders.size(); i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }
                    //Log.i("TEST",response.body().string());

                    jsonResponse[0] = response.body().string();
                    Log.i("TEST", jsonResponse[0]);

                } catch (IOException e) {
                    e.printStackTrace();
                }

        addPrototypeItems(jsonResponse[0], items);
        return items;
    }

    //Only intended for use with the prototype app
    private void addPrototypeItems(String jsonResponse, List<ItemModel> items) {
        Log.i("addPrototypeItems", "Calling Prototype Items");
        try {
            Log.i("addPrototypeItems - RESPONSE", jsonResponse);
            JSONObject response = new JSONObject(jsonResponse);
            JSONArray jsonArray = response.getJSONArray("businesses");


            for (int i = 0; i < jsonArray.length(); i++) {
                String name = jsonArray.getJSONObject(i).getString("name");
                String price = jsonArray.getJSONObject(i).optString("price");
                String location = jsonArray.getJSONObject(i).getJSONObject("location").getString("address1");
                String image_url = jsonArray.getJSONObject(i).getString("image_url");
                String id = jsonArray.getJSONObject(i).getString("id");
                items.add(new ItemModel(image_url,name,price,location,id));
            }

        } catch (JSONException e){
            e.printStackTrace();
        }
    }



    private String itemToString (ItemModel item) {
        for (int i = 0; i < MainActivity.items.size(); i++) {
            if (MainActivity.items.get(i).getIdentifier().equals(item.getIdentifier())) {
                return "";
            }
        }

        String additem = item.getImage();
        additem += "," + item.getName();
        additem += "," + item.getLocation();
        additem += "," + item.getPrice_range();
        additem += "," + item.getIdentifier() + "\n";
        MainActivity.items.add(item);
        return additem;
    }
}