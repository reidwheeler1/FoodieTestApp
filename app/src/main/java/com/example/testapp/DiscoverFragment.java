package com.example.testapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

public class DiscoverFragment extends Fragment {
    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        return inflater.inflate(R.layout.fragment_discover, parent, false);
    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);
        ImageView image1 = view.findViewById(R.id.imageView);
        ImageView image2 = view.findViewById(R.id.imageView2);
        ImageView image3 = view.findViewById(R.id.imageView3);
        ImageView image4 = view.findViewById(R.id.imageView21);
        ImageView image5 = view.findViewById(R.id.imageView22);
        ImageView image6 = view.findViewById(R.id.imageView23);
        ImageView image7 = view.findViewById(R.id.imageView31);
        ImageView image8 = view.findViewById(R.id.imageView32);
        ImageView image9 = view.findViewById(R.id.imageView33);
        image1.setImageResource(R.drawable.res1);
        image2.setImageResource(R.drawable.res2);
        image3.setImageResource(R.drawable.res3);
        image4.setImageResource(R.drawable.res4);
        image5.setImageResource(R.drawable.res5);
        image6.setImageResource(R.drawable.res6);
        image7.setImageResource(R.drawable.res7);
        image8.setImageResource(R.drawable.res8);
        image9.setImageResource(R.drawable.res9);

    }
}