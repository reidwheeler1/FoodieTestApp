package com.example.testapp;

import android.app.Service;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;

public class GPS extends Service {
    Geocoder geocoder;
    String postalcode;
    List<Address> addresses;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                super.onLocationResult(locationResult);
                Log.d("mylog","Lat is: "+locationResult.getLastLocation().getLatitude() + " , " + "Lng is: " +
                        locationResult.getLastLocation().getLongitude());
                try {
                    addresses = geocoder.getFromLocation(locationResult.getLastLocation().getLatitude() , locationResult.getLastLocation().getLongitude() ,1);
                    postalcode = addresses.get(0).getPostalCode();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent("ACT_LOC");
                intent.putExtra("latitude",locationResult.getLastLocation().getLatitude());
                intent.putExtra("longitude",locationResult.getLastLocation().getLongitude());
                intent.putExtra("postalcode",postalcode);
                sendBroadcast(intent);
            }
        };

        //initialize geocoder
        geocoder = new Geocoder(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        requestLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    private void requestLocation(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
    }

}