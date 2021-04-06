package com.example.testapp;

import android.media.Image;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Adapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ProfileFragment extends Fragment {
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    ExpandableListView expandableListView;
    ArrayList<String> listGroup = new ArrayList<>();
    HashMap<String,ArrayList<String>> listChild = new HashMap<>();
    profileAdapter adapter;
    public static boolean deleting = false;
    private RelativeLayout progressBar;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment // Inflate the layout for this fragment

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, parent, false);
        progressBar = root.findViewById(R.id.loadingPanel);
        init(root);
        return root;


    }


    private void init(View root) {

        ExpandableListView expandableListView = root.findViewById(R.id.expandableListView);

        for (int g = 0 ; g<= 2; g++)
        {
            //add list values
           if (g == 0  ){
                listGroup.add("Likes");
                //init array list
                ArrayList<String> arrayList = new ArrayList<>();
                //loop
                for (int c= 0; c< MainActivity.items.size() ; c++){
                    if (!arrayList.contains(MainActivity.items.get(c).getName())){
                        arrayList.add( MainActivity.items.get(c).getName() ) ;
                    }

                }
                listChild.put(listGroup.get(g),arrayList);
            }


        }

        adapter = new profileAdapter(listGroup,listChild);
        expandableListView.setAdapter(adapter);
    }



}