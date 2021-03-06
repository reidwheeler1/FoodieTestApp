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

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuizCard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizCard extends Fragment {

    private static final String TAG = QuizCard.class.getSimpleName();
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;

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
        List<ItemModel> items = new ArrayList<>();
        //Add items here

        addPrototypeItems(items);
        return items;
    }

    //Only intended for use with the prototype app
    private void addPrototypeItems(List<ItemModel> items) {
        items.add(new ItemModel(R.drawable.berns, "Bern's Steak House", "$$$$", "1208 S Howard Ave"));
        items.add(new ItemModel(R.drawable.dunderbak, "Mr. Dunderbak's Biergarten", "$$", "14929 Bruce B Downs Blvd"));
        items.add(new ItemModel(R.drawable.felicitous, "Felicitous (on 51st)", "$", "11706 N 51st St"));
        items.add(new ItemModel(R.drawable.poke, "U Poke Spot", "$", "5001 E Fowler Ave"));
        items.add(new ItemModel(R.drawable.columbia, "Columbia", "$$", "801 Water St #1905"));
        items.add(new ItemModel(R.drawable.cazador, "Cazador Grill", "$$", "10918 N 56th St"));
        items.add(new ItemModel(R.drawable.boba, "BOBACUP", "$", "2732 E Fowler Ave"));
        items.add(new ItemModel(R.drawable.babushka, "Babushka's", "$$", "12639 N 56th St"));
        items.add(new ItemModel(R.drawable.jasmine, "Jasmine Thai", "$$", "13248 Dale Mabry Hwy"));
        items.add(new ItemModel(R.drawable.ulele, "Ulele", "$$$", "1810 N Highland Ave"));
        items.add(new ItemModel(R.drawable.anchor, "The Anchor Bar", "$", "514 N Franklin St"));
        items.add(new ItemModel(R.drawable.queen, "Queen of Sheba", "$$", "11001 N 56th St"));
        items.add(new ItemModel(R.drawable.wood, "Wood Fired Pizza", "$$", "2822 E Bearss Ave"));
        items.add(new ItemModel(R.drawable.oceanp, "Ocean Prime", "$$$$", "2205 N Westshore BLVD"));
        items.add(new ItemModel(R.drawable.bagel, "Bagels Plus", "$", "2706 E Fletcher Ave"));
        items.add(new ItemModel(R.drawable.bun, "Sweet Buns", "$", "2788 E Fowler Ave"));
        items.add(new ItemModel(R.drawable.hat, "Hattricks", "$$", "107 S Franklin St"));
        items.add(new ItemModel(R.drawable.arm, "Armature Works", "$$", "1910 N Ola Ave"));
        items.add(new ItemModel(R.drawable.love, "Loving Hut", "$", "1905 E Fletcher Ave"));
        items.add(new ItemModel(R.drawable.pepper, "Sacred Pepper" , "$$$", "15405 N Dale Mabry Hwy"));



        items.add(new ItemModel(R.drawable.berns, "Bern's Steak House", "$$$$", "1208 S Howard Ave"));
        items.add(new ItemModel(R.drawable.jasmine, "Jasmine Thai", "$$", "13248 Dale Mabry Hwy"));
        items.add(new ItemModel(R.drawable.anchor, "The Anchor Bar", "$", "514 N Franklin St"));

        return;
    }
}