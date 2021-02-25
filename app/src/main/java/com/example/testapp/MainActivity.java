package com.example.testapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_bar);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            /*If fragment requires some initial data, arguments can be passed to fragment
            * by providing a Bundle in the call to FragmentTransaction.add(), as shown below
            * https://developer.android.com/guide/fragments/create */
            /*Bundle bundle = new Bundle();
            * bundle.putInt("some_int", 0);
            * //Then, replace 'null' final argument of .add(...) with 'bundle'*/
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, QuizCard.class, null)
                    .commit();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch(item.getItemId()) {
                case R.id.discover:
                    selectedFragment = new DiscoverFragment();
                    break;
                case R.id.quiz:
                    selectedFragment = new QuizCard();
                    break;
                case R.id.profile:
                    selectedFragment = new ProfileFragment();
                    break;
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_view, selectedFragment)
                    .commit();
            return true;
        }
    };
}