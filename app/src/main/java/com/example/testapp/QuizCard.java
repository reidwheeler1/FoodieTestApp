package com.example.testapp;

import android.app.Dialog;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    //
    private boolean gatheringPreferences = true; //Set to false in paginate()
    private PreferenceList preferenceList;
    private List<ItemModel> likedPreferences;
    private List<ItemModel> currentSetLikedRestaurants;
    private List<ItemModel> currentSetDislikedRestaurants;

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
                       gatheringPreferences = false;
                       result.dispatchUpdatesTo(adapter);
                       progressBar.setVisibility(View.GONE);
                   }
                });
            }
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
        String url = "&categories=";
        if (gatheringPreferences) {
            url = url + likedPreferences.get(0).getIdentifier();
            for (int i = 1; i < likedPreferences.size(); i++)
                url = url + "," + likedPreferences.get(i).getIdentifier();
        } else
            url = url + identifyCommonCategories();
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
        String result = "all";
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
                    result = cat;
                    firstCatNotYetAdded = false;
                }
            } else if (count == mostCommonCat || (count == secondMostCom && !badCatMap.containsKey(cat))) {
                result = result + "," + cat;
            }
        }

        Log.d("IdentifyCommonCategories", result);
        currentSetLikedRestaurants.clear(); //Prepare for next set
        currentSetDislikedRestaurants.clear();
        return result;
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
        String ret = "";

        if (list == null || list.isEmpty())
            return ret;

        ret += list.get(0);
        for (int i=1; i<list.size(); i++)
            ret += ", " + list.get(i);

        return ret;
    }
}