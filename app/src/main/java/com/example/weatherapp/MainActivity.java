package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    Button btnLocation;
    Button btnHourly;
    ImageView ivSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLocation = findViewById(R.id.btnLocation);
        btnHourly = findViewById(R.id.btnHourly);
        ivSettings = findViewById(R.id.ivSettings);
        ivSettings.setImageResource(R.drawable.settings);

        // Adding some buttons to navigate through activities when testing //
        btnLocation.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, LocationActivity.class));
        });
        btnHourly.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, HourlyActivity.class));
        });
        ivSettings.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, Settings.class));
        });



    }
}