package com.example.jqzhang.locationtest;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private TextView positionTextView;
    private LocationManager locationManager;
    private String provider;

    public static final int SHOW_LOCATION = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        positionTextView = (TextView)findViewById(R.id.location_text_view);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        List<String> providerList = locationManager.getProviders(true);

        if (providerList.contains(LocationManager.GPS_PROVIDER)){
            provider = LocationManager.GPS_PROVIDER;
        }else if (providerList.contains(LocationManager.NETWORK_PROVIDER)){
            provider = LocationManager.NETWORK_PROVIDER;
        }else {
            Toast.makeText(this, "NO provider", Toast.LENGTH_SHORT).show();
            return;
        }

        try{
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null){
                showLocation(location);
            }
            locationManager.requestLocationUpdates(provider, 500, 1, locationListener);

        }catch (SecurityException e){
            e.printStackTrace();
        }

    }

    protected void onDestroy(){
        super.onDestroy();
        try{
            if (locationManager != null) {
                // 关闭程序时将监听器移除
                locationManager.removeUpdates(locationListener);
            }
        }catch (SecurityException e){
            e.printStackTrace();
        }

    }
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            showLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void showLocation(final Location location){

//        String currentPosition = "latitude is " + location.getLatitude() + "\n" + "longitude is " + location.getLongitude();
//        positionTextView.setText(currentPosition);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    StringBuilder uri = new StringBuilder();
                    uri.append("http://maps.googleapis.com/maps/api/geocode/json?latlng=");
                    uri.append(location.getLatitude());
                    uri.append(",");
                    uri.append(location.getLongitude());
                    uri.append("&sensor=false");

                    URL url = new URL(uri.toString());
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();

                    InputStream inStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
                    StringBuffer buffer = new StringBuffer();
                    String line = "";
                    while ((line = reader.readLine()) != null){
                        buffer.append(line);
                    }
                    String result = buffer.toString();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
