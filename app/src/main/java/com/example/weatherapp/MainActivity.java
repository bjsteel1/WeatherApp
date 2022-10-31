package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    //create necessary views for main activity - Ivy
    ImageView ivLocation;
    ImageView ivSearch;
    ImageView ivClock;
    ImageView ivSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing views
        ivLocation = findViewById(R.id.ivLocation);
        ivLocation.setImageResource(R.drawable.location);
        ivSearch = findViewById(R.id.ivSearch);
        ivSearch.setImageResource(R.drawable.search);
        ivSearch = findViewById(R.id.ivSearch);
        ivClock = findViewById(R.id.ivClock);
        ivClock.setImageResource(R.drawable.clock);
        ivSettings = findViewById(R.id.ivSettings);
        ivSettings.setImageResource(R.drawable.settings);

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