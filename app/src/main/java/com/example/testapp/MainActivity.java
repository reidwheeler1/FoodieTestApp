package com.example.testapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.LocationServices;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    public static String postalcode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //GPS check permissions
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // request permission
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                //req location permission
                startService();

            }
        } else {
            // Start locations service
            startService();
        }

        Log.d("mylog from main", String.valueOf(MainActivity.postalcode));

        getIntent().getAction().equals("ACT_LOC");


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

    /*
GPS functions
 */

    void startService(){

        LocationBroadcastReciever reciever = new LocationBroadcastReciever();
        IntentFilter filter = new IntentFilter("ACT_LOC");
        registerReceiver(reciever,filter);
        Intent intent = new Intent(MainActivity.this, GPS.class);

        startService(intent);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startService();
                }else{
                    Toast.makeText(this, "Give me permissions", Toast.LENGTH_LONG).show();
                }
        }
    }

    public class LocationBroadcastReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("ACT_LOC")) {
                double latitude = intent.getDoubleExtra("latitude", 0f);
                double longitude = intent.getDoubleExtra("longitude", 0f);
                postalcode = intent.getStringExtra("postalcode");
                Toast.makeText(MainActivity.this, "Lat: " + latitude + ", longi is: " + longitude + " postalcode: " + postalcode, Toast.LENGTH_LONG).show();
            }


        }
    }

    public static String getPostalcode(){

        return postalcode;
    }

}

