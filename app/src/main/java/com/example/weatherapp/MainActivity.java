package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //create necessary image views for main activity
    ImageView ivLocation;
    ImageView ivSearch;
    ImageView ivClock;
    ImageView ivSettings;

    //create necessary text views for main activity
    TextView tvLocationName;
    TextView tvLocationInfo;
    TextView tvForecast;
    TextView tvTemp;
    TextView tvTempFeels;
    TextView tvTempHighLow;
    TextView tvTempWind;

    //create necessary list views for main activity
    ListView lstForecast;

    ArrayList<Location> arrLocation;
    RequestQueue queue;
    LocationActivity.LocationAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing image views
        ivLocation = findViewById(R.id.ivLocation);
        ivLocation.setImageResource(R.drawable.location);
        ivSearch = findViewById(R.id.ivSearch);
        ivSearch.setImageResource(R.drawable.search);
        ivClock = findViewById(R.id.ivClock);
        ivClock.setImageResource(R.drawable.clock);
        ivSettings = findViewById(R.id.ivSettings);
        ivSettings.setImageResource(R.drawable.settings);


        //initializing text views
        tvLocationName = findViewById(R.id.tvLocationName);
        tvLocationInfo = findViewById(R.id.tvLocationInfo);
        tvForecast = findViewById(R.id.tvForecast);
        tvTemp = findViewById(R.id.tvTemp);
        tvTempFeels = findViewById(R.id.tvTempFeels);
        tvTempHighLow = findViewById(R.id.tvTempHighLow);
        tvTempWind = findViewById(R.id.tvTempWind);

        queue = Volley.newRequestQueue(this);
        lstForecast = findViewById(R.id.lstForecast);
        arrLocation = new ArrayList<>();
//        adapter = new LocationActivity.LocationAdapter(arrLocation, this);
//        lstPerson.setAdapter(adapter);

        //ivSearch onClickListener that brings the user to the LocationActivity when the
        //magnifying glass image is clicked
        ivSearch.setOnClickListener(view -> {
            //start the url with the google search
            String url = "https://www.google.com/search?q=";
            //remove the comma(s) from the location name
            String cityState = tvLocationName.getText().toString().replace(",", "");
            //split the different words into locationArray
            String locationArray[] = cityState.split(" ");
            //loop through each word in the array except the last
            for(int i=0; i<locationArray.length-1; i++){
                //append it to the google url, followed by %20
                url += locationArray[i].toLowerCase(Locale.ROOT) + "%20";
            }
            //only append the last word from the locationArray to the url, without the 20%
            url+= locationArray[locationArray.length-1].toLowerCase(Locale.ROOT);
            //Create a new intent to open the url within google chrome
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            //start the activity
            startActivity(intent);
        });

        //ivClock onClickListener that brings the user to the HourlyActivity when the
        //clock image is clicked
        ivClock.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, HourlyActivity.class));
        });

        //ivSettings onClickListener that brings the user to the SettingsActivity when the
        //settings image is clicked
        ivSettings.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, Settings.class));
        });

    }

    public void fetchData(){
        String url = "https://api.weatherapi.com/v1/forecast.json?key=0d2ee64c9feb4ccc9ff23426222810&q=48601&days=7&aqi=no&alerts=no";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url,null,
                response -> {
                    try {
                            JSONObject jLocation = response.getJSONObject("location");
                            String city = jLocation.getString("name");
                            int lat = jLocation.getInt("lat");
                            int lon = jLocation.getInt("lon");
                            String txtLocationInfo = "Current Location " + lat + " lat " + lon + " lon.";
                            JSONObject jCurrent = response.getJSONObject("current");
                            int temp_f = jCurrent.getInt("temp_f");
                            JSONObject jCondition = response.getJSONObject("condition");
                            int tempfeels_f = jCondition.getInt("feelslike_f");
                            int tempHigh;
                            int tempLow;
                            int windSpeed =  jCondition.getInt("wind_mph");
                            String imageURL = jCondition.getString("icon");


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                });

        //Add request to queue
        queue.add(request);

    }
}