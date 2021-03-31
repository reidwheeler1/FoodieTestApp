package com.example.testapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    public static String postalcode;
    public static String filename = "CSV_likes";
    public static String likes = "";
    public static List<ItemModel> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        //GPS check permissions

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // request permission
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this, "This app will not work without location!", Toast.LENGTH_LONG).show();
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        startService();
                    }
                }
                else {
                    startService();
                }
            } else {
                //req location permission
                startService();

            }
        } else {
            // Start locations service
            startService();
        }

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Log.d("mylog from main", String.valueOf(MainActivity.postalcode));

        getIntent().getAction().equals("ACT_LOC");

        // activity stuff

        setContentView(R.layout.activity_main);
        startSavingLikes();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav_bar);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

                if (savedInstanceState == null) {
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .add(R.id.fragment_container_view, ProfileFragment.class, null)
                            .commit();
                }

    }



    @Override
    protected void onStart() {
        super.onStart();

        try {
            readLikes();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            writeLikes();
        } catch (IOException e) {
            e.printStackTrace();
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
              //  Toast.makeText(MainActivity.this, "Lat: " + latitude + ", longi is: " + longitude + " postalcode: " + postalcode, Toast.LENGTH_LONG).show();
            }

        }
    }

    public static String getPostalcode(){

        return postalcode;
    }


    /*
    save likes
     */
    // create file if it doesn't exist
    private void startSavingLikes(){
        File filePref = new File(getFilesDir(), filename);

    }
// likes store in a CSV with each Item model parameter in the order of assignment in the oobject class
    public void writeLikes() throws IOException {

        FileOutputStream fos = openFileOutput(filename, Context.MODE_APPEND);

            fos.write(likes.getBytes());

            Log.i("mylog","likes text: " + likes );
            likes = "";

        fos.close();

    }

//read in likes file if it exists and load them into project
    private void readLikes() throws FileNotFoundException {
        FileInputStream fis = openFileInput(filename);
        String[] itemContent;
        Scanner scanner = new Scanner(fis);
        while (scanner.hasNextLine()) {
            String content = scanner.nextLine();
            itemContent = content.split(",");
            itemContent[4] = itemContent[4].trim();
            ItemModel itemModel = new ItemModel(itemContent[0],itemContent[1],itemContent[2],itemContent[3],itemContent[4]);
            items.add(itemModel);
            Log.i("mylog","file content: " + items.get(items.size()-1).getName());
        }

        scanner.close();
    }

}