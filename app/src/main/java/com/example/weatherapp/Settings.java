package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class Settings extends AppCompatActivity {
    //UI Controls
    ImageView ivSave;
    ImageView ivCancel;
    Button btnReset;
    RadioGroup rdgHour;
    RadioGroup rdgDay;
    RadioGroup rdgTemp;
    RadioButton rdbF;
    RadioButton rdb7Day;
    RadioButton rdb12Hour;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //handle for all ui controls;
        ivSave = findViewById(R.id.ivSave);
        ivCancel = findViewById(R.id.ivCancel);
        btnReset = findViewById(R.id.btnReset);
        rdgHour = findViewById(R.id.rdgHour);
        rdgDay = findViewById(R.id.rdgDay);
        rdgTemp = findViewById(R.id.rdgTemp);
        rdbF = findViewById(R.id.rdbF);
        rdb7Day = findViewById(R.id.rdb7Day);
        rdb12Hour = findViewById(R.id.rdb12Hour);
        //On Click Listener for Save Image View
        //Used for saving settings then returning to home screen
        ivSave.setOnClickListener(view -> {

        });
        //On Click Listener for Cancel Image View
        //Used for canceling current changes to settings then returning to home screen
        ivCancel.setOnClickListener(view -> {

        });
        //On Click Listener for Reset Button
        //Used for returning all settings to default values
        btnReset.setOnClickListener(view -> {
            rdgTemp.clearCheck();
            rdgDay.clearCheck();
            rdgHour.clearCheck();
            rdgTemp.check(rdbF.getId());
            rdgDay.check(rdb7Day.getId());
            rdgHour.check(rdb12Hour.getId());
        });
    }
}