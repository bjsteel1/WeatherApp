package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    //create necessary views for main activity - Ivy
    Button btnLocation;
    ImageView ivClock;
    ImageView ivSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing views
        btnLocation = findViewById(R.id.btnLocation);
        ivClock = findViewById(R.id.ivClock);
        ivClock.setImageResource(R.drawable.clock);
        ivSettings = findViewById(R.id.ivSettings);
        ivSettings.setImageResource(R.drawable.settings);

        //ivLocation onClickListener that brings the user to the LocationActivity when the
        //magnifying glass image is clicked
        btnLocation.setOnClickListener(view -> {
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