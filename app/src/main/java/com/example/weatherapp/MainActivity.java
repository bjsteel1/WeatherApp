package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
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
    ImageView ivPickLocation;
    ImageView ivPic;

    //create necessary text views for main activity
    TextView tvLocationName;
    TextView tvLocationInfo;
    TextView tvForecast;
    TextView tvImgDesc;
    TextView tvTemp;
    TextView tvTempFeels;
    TextView tvTempHighLow;
    TextView tvTempWind;

    //create necessary list views for main activity
    ListView lstForecast;

    ProgressBar pbLoading;

    ArrayList<Location> arrLocation;
    RequestQueue queue;
    LocationAdapter adapter;
    String defaultLocation, tempScale, city, txtLocationInfo, condition, imageURL;
    int forecastDays, temp, tempFeels, tempHigh, tempLow, windSpeed;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get shared prefs
        sharedPreferences = getSharedPreferences("SharedPrefs",MODE_PRIVATE);
        tempScale = sharedPreferences.getString("Unit","F");
        forecastDays = Integer.parseInt(sharedPreferences.getString("Day","7"));

        SharedPreferences sharedSetLocation = getSharedPreferences("SharedPrefDefault", MODE_PRIVATE);
        defaultLocation = sharedSetLocation.getString("default_location","Saginaw, USA");

        //initializing image views
        ivLocation = findViewById(R.id.ivLocation);
        ivLocation.setImageResource(R.drawable.location);
        ivSearch = findViewById(R.id.ivSearch);
        ivSearch.setImageResource(R.drawable.search);
        ivClock = findViewById(R.id.ivClock);
        ivClock.setImageResource(R.drawable.clock);
        ivSettings = findViewById(R.id.ivSettings);
        ivSettings.setImageResource(R.drawable.settings);
        ivPickLocation = findViewById(R.id.ivPickLocation);
        ivPic = findViewById(R.id.ivPic);

        //initializing text views
        tvLocationName = findViewById(R.id.tvLocationName);
        tvLocationInfo = findViewById(R.id.tvLocationInfo);
        tvForecast = findViewById(R.id.tvForecast);
        tvImgDesc = findViewById(R.id.tvImgDesc);
        tvTemp = findViewById(R.id.tvTemp);
        tvTempFeels = findViewById(R.id.tvTempFeels);
        tvTempHighLow = findViewById(R.id.tvTempHighLow);
        tvTempWind = findViewById(R.id.tvTempWind);

        tvForecast.setText(forecastDays + "-Day Forecast");

        pbLoading = findViewById(R.id.pbLoading);

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

        //ivPickLocation onClickListener that brings the user to the LocationActivity when the
        //globe image or location name is clicked
        tvLocationName.setOnClickListener(view->{
            startActivity(new Intent(MainActivity.this, LocationActivity.class));
        });
        ivPickLocation.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, LocationActivity.class));
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

        // Fetch the data & populate the views
        fetchData(defaultLocation);
    }

    // Call API and load in all weather data to variables
    private void fetchData(String location){
        // Display progress bar until loading is complete
        setAllVisibility(View.INVISIBLE);
        pbLoading.setVisibility(View.VISIBLE);

        String url = getResources().getString(R.string.url_part1) + location + "&days=" + forecastDays + "&aqi=no&alerts=no";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url,null,
                response -> {
                    try {
                        JSONObject jLocation = response.getJSONObject("location");
                        String strCountry = jLocation.getString("country");
                        if (strCountry.equalsIgnoreCase("United States of America"))
                            strCountry = "USA";
                        city = jLocation.getString("name") + ", " + strCountry;
                        double lat = jLocation.getDouble("lat");
                        double lon = jLocation.getDouble("lon");
                        txtLocationInfo = "Current Location: " + lat + "°N, " + lon + " °W";
                        JSONObject jCurrent = response.getJSONObject("current");
                        temp = jCurrent.getInt("temp_" + tempScale.toLowerCase());
                        JSONObject jCondition = jCurrent.getJSONObject("condition");
                        tempFeels = jCurrent.getInt("feelslike_" + tempScale.toLowerCase());
                        condition = jCondition.getString("text");
                        JSONObject jDay = response.getJSONObject("forecast").getJSONArray("forecastday").getJSONObject(0).getJSONObject("day");
                        tempHigh = jDay.getInt("maxtemp_" + tempScale.toLowerCase());
                        tempLow = jDay.getInt("mintemp_" + tempScale.toLowerCase());
                        windSpeed =  jCurrent.getInt("wind_mph");
                        imageURL = "https:" + jCondition.getString("icon");
                        populateViews();

                        // Set listview to display forecast
                        ForecastAdapter adapter = new ForecastAdapter(response.getJSONObject("forecast").getJSONArray("forecastday"),
                                this, tempScale);
                        lstForecast.setAdapter(adapter);
                    } catch (JSONException e) {
                        Log.d("WeatherApp", e.getMessage());
                        e.printStackTrace();
                    }
                },
                error -> {
                });

        //Add request to queue
        queue.add(request);
    }

    // Populate views w/ weather data
    private void populateViews(){
        tvLocationName.setText(city);
        tvLocationInfo.setText(txtLocationInfo);
        tvImgDesc.setText(condition);
        tvTemp.setText(temp + "\u00B0 " + tempScale);
        tvTempFeels.setText("Feels like: " + tempFeels + "\u00B0 " + tempScale);
        tvTempHighLow.setText("H: " + tempHigh + "\u00B0 " + tempScale + "     L: " + tempLow + "\u00B0 " + tempScale);
        tvTempWind.setText("Windspeed: " + windSpeed + " mph");
        Picasso.get().load(imageURL).into(ivPic);

        setAllVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.INVISIBLE);
    }

    // Method to quick toggle visibility of all views that need to be loaded from fetchData
    private void setAllVisibility(int visibility){
        tvLocationName.setVisibility(visibility);
        tvLocationInfo.setVisibility(visibility);
        tvImgDesc.setVisibility(visibility);
        tvTemp.setVisibility(visibility);
        tvTempFeels.setVisibility(visibility);
        tvTempHighLow.setVisibility(visibility);
        tvTempWind.setVisibility(visibility);
        lstForecast.setVisibility(visibility);
    }
}