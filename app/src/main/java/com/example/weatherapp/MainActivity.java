package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

        //ivSearch onClickListener that brings the user to the LocationActivity when the
        //magnifying glass image is clicked
        ivSearch.setOnClickListener(view -> {
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



    }
}