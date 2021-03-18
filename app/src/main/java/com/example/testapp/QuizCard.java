package com.example.testapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
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
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;

    // Okhttp Client
    private final OkHttpClient client = new OkHttpClient();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
        init(root);
        return root;
    }

    private void init(View root) {
        CardStackView cardStackView = root.findViewById(R.id.card_stack_view);
        manager = new CardStackLayoutManager(getContext(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);
                //Toasts display the message at the bottom of the screen, but linger around too long for this purpose
/*                if (direction == Direction.Right) {
                    Toast.makeText(getContext(), "Direction Right", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Left) {
                    Toast.makeText(getContext(), "Direction Left", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Top) {
                    Toast.makeText(getContext(), "Direction Top", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Bottom) {
                    Toast.makeText(getContext(), "Direction Bottom", Toast.LENGTH_SHORT).show();
                }*/

                //If getItemCount - <number> == 0, paginate card stack
                //TODO: Look into changing this logic (potential for errors)
                if (manager.getTopPosition() == adapter.getItemCount() - 3) {
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
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(2);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);

        manager.setDirections(Direction.HORIZONTAL); //Other options: VERTICAL, FREEDOM
        manager.setCanScrollHorizontal(true); //Enables card to move on horizontal axis
        //manager.setCanScrollVertical(true); //Enables card to move on vertical axis, regardless of set Direction

        // Adding Stackable Cards
        manager.setStackFrom(StackFrom.Top);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(4.0f);

        //SwipeableMethod determines whether buttons are used (Automatic) or touch-and-drag (Manual)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);

        manager.setOverlayInterpolator(new LinearInterpolator());
        adapter = new CardStackAdapter(addList());
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
    }

    //When current card stack is near end, integrate new items into stack
    private void paginate() {
        //See: https://developer.android.com/reference/android/support/v7/util/DiffUtil
        List<ItemModel> old_items = adapter.getItems();
        List<ItemModel> new_items = new ArrayList<>(addList());
        CardStackCallback callback = new CardStackCallback(old_items, new_items);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        adapter.setItems(new_items);
        result.dispatchUpdatesTo(adapter);
    }

    private List<ItemModel> addList() {
        Log.i("addList()","Calling addList");
        List<ItemModel> items = new ArrayList<>();
        //Add items here

        String yelpAPIKey = "ON2gpPfKlpMDaoU6OTZy-ES-ibzcfONKyS6VoTTdiVNjrN4rZ60Q3JUN-Lz_DKZtHDMfT6-MBhsTFrukQ-dTppuVw8wvuuUS6OufEsSleuD182x8fUiTYoZHt80uYHYx";
        //      Searches businesses with location query of zip code 33620 (USF Zip)
        String url = "https://api.yelp.com/v3/businesses/search?location=33620";
        final String[] jsonResponse = new String[1];
        Thread t =  new Thread(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url(url)
                        .header("Authorization","Bearer " + yelpAPIKey)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    Headers responseHeaders = response.headers();
                    for (int i = 0; i < responseHeaders.size(); i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }
                    //Log.i("TEST",response.body().string());

                    jsonResponse[0] = response.body().string();
                    Log.i("TEST",jsonResponse[0]);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();

        try {
            t.join();
        } catch (Exception e){
            Log.e("ERROR",e.toString());
        }

        addPrototypeItems(jsonResponse[0],items);
        return items;
    }

    //Only intended for use with the prototype app
    private void addPrototypeItems(String jsonResponse, List<ItemModel> items) {
        Log.i("addPrototypeItems","Calling Prototype Items");
        try {
            Log.i("addPrototypeItems - RESPONSE",jsonResponse);
            JSONObject response = new JSONObject(jsonResponse);
            JSONArray jsonArray = response.getJSONArray("businesses");


            for(int i = 0; i < 10; i++){
                String name = jsonArray.getJSONObject(i).getString("name");
                String price = jsonArray.getJSONObject(i).getString("price");
                String location = jsonArray.getJSONObject(i).getString("review_count");
                String image_url = jsonArray.getJSONObject(i).getString("image_url");
                items.add(new ItemModel(image_url,name,price,location));
            }

        } catch (JSONException e){
            e.printStackTrace();
        }
    }
}