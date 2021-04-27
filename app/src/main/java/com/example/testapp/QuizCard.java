package com.example.testapp;

import android.app.Dialog;
import android.content.ClipData;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuizCard extends Fragment {

    private static final String TAG = QuizCard.class.getSimpleName();
    private RelativeLayout progressBar;
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    private boolean gatheringPreferences = true; //Set to false in paginate()
    private PreferenceList preferenceList;
    private List<ItemModel> likedPreferences;
    private List<ItemModel> currentSetLikedRestaurants;
    private List<ItemModel> currentSetDislikedRestaurants;
    private View rootView;

    // Okhttp Client
    private final OkHttpClient client = new OkHttpClient();

    public QuizCard() {
        super(R.layout.fragment_quiz_card);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_quiz_card, container, false);
            progressBar = rootView.findViewById(R.id.loadingPanel);
            init(rootView);
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        if (rootView.getParent() != null)
            ((ViewGroup)rootView.getParent()).removeView(rootView);
        super.onDestroyView();
    }

    private void init(View root) {
        CardStackView cardStackView = root.findViewById(R.id.card_stack_view);
        preferenceList = new PreferenceList();
        likedPreferences = new ArrayList<>();
        currentSetLikedRestaurants = new ArrayList<>();
        currentSetDislikedRestaurants = new ArrayList<>();
        manager = new CardStackLayoutManager(getContext(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                //Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " count=" + adapter.getItemCount() + " d=" + direction);
                Log.i(TAG, "Categories: " + Utilities.categoriesToString(adapter.getItems().get(manager.getTopPosition()-1).getCategories()));

                if (direction == Direction.Right && gatheringPreferences) { //Tracking initial preferences
                    likedPreferences.add(adapter.getItems().get(manager.getTopPosition()-1));
                }
                if (direction == Direction.Right && !gatheringPreferences) { //Tracking liked restaurants
                    currentSetLikedRestaurants.add(adapter.getItems().get(manager.getTopPosition()-1));
                    MainActivity.likes += itemToString(adapter.getItems().get(manager.getTopPosition()-1));
                }
                if (direction ==Direction.Left && !gatheringPreferences) { //Tracking disliked restaurants
                    currentSetDislikedRestaurants.add(adapter.getItems().get(manager.getTopPosition()-1));
                }

                //If getTopPosition == original item count, paginate card stack
                if (manager.getTopPosition() == adapter.getItemCount()) {
                    if (likedPreferences.size() > 0) {
                        paginate(); //Paginating: see function definition below
                    } else paginatePreferences();
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
                //Log.d(TAG, "onCardDisappeared: " + position + ", name: " + textView.getText());
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
        adapter = new CardStackAdapter(preferenceList.getPreferenceSubset());
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
    }

    //When current card stack is near end, integrate new items into stack
    private void paginate() {
        //Start spinner
        progressBar.setVisibility(View.VISIBLE);
        //new thread
        new Thread(() -> {
            //See: https://developer.android.com/reference/android/support/v7/util/DiffUtil
            List<ItemModel> old_items = adapter.getItems();
            List<ItemModel> new_items = new ArrayList<>(addList());
            CardStackCallback callback = new CardStackCallback(old_items, new_items);
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
            adapter.setItems(new_items);
            //run below on UI thread
            new Handler(Looper.getMainLooper()).post(() -> {
                gatheringPreferences = false;
                result.dispatchUpdatesTo(adapter);
                progressBar.setVisibility(View.GONE);
            });
        }).start();
    }

    /*Paginates cardstack when empty with subset of preferences; locks card when preferences run out*/
    private void paginatePreferences() {
        List<ItemModel> old_items = adapter.getItems();
        List<ItemModel> new_items = preferenceList.getPreferenceSubset();
        if (new_items.isEmpty()) {
            manager.setCanScrollVertical(false);
            manager.setCanScrollHorizontal(false);
            new_items.add(new ItemModel(
                    "https://images.unsplash.com/photo-1454117096348-e4abbeba002c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
                    "Are you sure you're hungry?", "", "", ""));
        }
        CardStackCallback callback = new CardStackCallback(old_items, new_items);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        adapter.setItems(new_items);
        result.dispatchUpdatesTo(adapter);
    }

    private String constructURL() {
        Log.i("Preferences: ", ProfileFragment.getDietaryPreferencesString());
        StringBuilder _url = new StringBuilder("&categories=");
        if (gatheringPreferences) {
            _url.append(likedPreferences.get(0).getIdentifier());
            for (int i = 1; i < likedPreferences.size(); i++)
                _url.append(",").append(likedPreferences.get(i).getIdentifier());
            _url.append(ProfileFragment.getDietaryPreferencesString());
        } else
            _url.append(identifyCommonCategories());
        _url.append("&sort_by=rating");
        return _url.toString();
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

                List<String> categories = new ArrayList<>();
                JSONArray cats = jsonArray.getJSONObject(i).getJSONArray("categories");
                for (int j = 0; j < cats.length(); j++)
                    categories.add(cats.getJSONObject(j).getString("alias"));

                items.add(new ItemModel(image_url,name,location,price,id, categories));
            }

        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    private String identifyCommonCategories() {
        StringBuilder result = new StringBuilder("all");
        int mostCommonCat = -1;
        int secondMostCom = -1;
        Map<String, Integer> catMap = new HashMap<>();
        Map<String, Integer> badCatMap = new HashMap<>();
        //Count the occurrence of categories and track two highest counts
        for (ItemModel list : currentSetLikedRestaurants) {
            int tempCount;
            for (String cat : list.getCategories()) {
                if (!catMap.containsKey(cat))
                    catMap.put(cat, 1);
                else
                    catMap.put(cat, catMap.get(cat) + 1);

                tempCount = catMap.get(cat);
                if (tempCount > mostCommonCat)
                    mostCommonCat = tempCount;
                else if (tempCount > secondMostCom)
                    secondMostCom = tempCount;
            }
        }
        //Count occurrence of disliked categories and track the count
        int mostDislikedCat = -1;
        for (ItemModel list : currentSetDislikedRestaurants) {
            int tempCount;
            for (String cat : list.getCategories()) {
                if (!badCatMap.containsKey(cat))
                    badCatMap.put(cat, 1);
                else
                    badCatMap.put(cat, badCatMap.get(cat) + 1);

                tempCount = badCatMap.get(cat);
                if (tempCount > mostDislikedCat)
                    mostDislikedCat = tempCount;
            }
        }
        //Remove less common disliked categories from dislikes Map
        List<String> tempSet = new ArrayList<>(badCatMap.keySet()); //Needed to avoid ConcurrentModificationException
        for (String cat : tempSet) {
            if (badCatMap.get(cat) == mostDislikedCat)
                badCatMap.remove(cat);
        }
        //Add 1st and 2nd most common categories to string - most disliked (unless it is also top liked)
        int count;
        boolean firstCatNotYetAdded = true;
        for (String cat : catMap.keySet()) {
            count = catMap.get(cat);
            if (firstCatNotYetAdded) {
                if (count == mostCommonCat || (count == secondMostCom && !badCatMap.containsKey(cat))) {
                    result = new StringBuilder(cat);
                    firstCatNotYetAdded = false;
                }
            } else if (count == mostCommonCat || (count == secondMostCom && !badCatMap.containsKey(cat))) {
                result.append(",").append(cat);
            }
        }

        Log.d("IdentifyCommonCategories", result.toString());
        currentSetLikedRestaurants.clear(); //Prepare for next set
        currentSetDislikedRestaurants.clear();
        return result.toString();
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

class Utilities {
    public static String categoriesToString(List<String> list) {
        StringBuilder res = new StringBuilder();

        if (list == null || list.isEmpty())
            return "";

        res.append(list.get(0));
        for (int i=1; i<list.size(); i++)
            res.append(", ").append(list.get(i));

        return res.toString();
    }
}