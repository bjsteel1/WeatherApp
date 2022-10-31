package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button btnLocation;
    Button btnHourly;
    Button btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLocation = findViewById(R.id.btnLocation);
        btnHourly = findViewById(R.id.btnHourly);
        btnSettings = findViewById(R.id.btnSettings);

        // Adding some buttons to navigate through activities when testing //
        btnLocation.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, LocationActivity.class));
        });
        btnHourly.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, HourlyActivity.class));
        });
        btnSettings.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, Settings.class));
        });



    }
}