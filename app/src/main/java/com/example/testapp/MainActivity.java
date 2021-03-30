package com.example.testapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    public static String filename = "CSV_likes";
    public static String likes = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startSavingLikes();

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

    @Override
    protected void onStop() {
        super.onStop();
        try {
            writeLikes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            readLikes();
        } catch (FileNotFoundException e) {
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


    private void startSavingLikes(){
       // File directory;
       // directory = getFilesDir();
       // File[] files = directory.listFiles();
        File filePref = new File(getFilesDir(), filename);


    }

    public void writeLikes() throws IOException {

        FileOutputStream fos = openFileOutput(filename, Context.MODE_APPEND);

            fos.write(likes.getBytes());

            Log.i("mylog","likes text: " + likes );

            likes = "";


        fos.close();

    }



    private void readLikes() throws FileNotFoundException {
        FileInputStream fis = openFileInput(filename);
        Scanner scanner = new Scanner(fis);
        while (scanner.hasNextLine()) {
            String content = scanner.nextLine();
            Log.i("mylog","file content: " + content);
        }
        //scanner.useDelimiter(",");
        //String content = scanner.next();

        scanner.close();
    }





}