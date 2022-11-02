package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
    RadioButton rdbC;
    RadioButton rdb14Day;
    RadioButton rdb24Hour;
    SharedPreferences sharedPreferences;
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
        rdbC = findViewById(R.id.rdbC);
        rdb14Day = findViewById(R.id.rdb14Day);
        rdb24Hour = findViewById(R.id.rdb24Hour);
        //handle for shared preferences and editor
        sharedPreferences = getSharedPreferences("SharedPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //On Click Listener for Save Image View
        //Used for saving settings then returning to home screen
        ivSave.setOnClickListener(view -> {
            if(rdgTemp.getCheckedRadioButtonId() == rdbC.getId()){
                editor.putString("Unit","C");
                Log.d("MyTag",sharedPreferences.getString("Unit",""));
            }else{
                editor.putString("Unit","F");
                Log.d("MyTag","flags");
            }
            if(rdgDay.getCheckedRadioButtonId() == rdb14Day.getId()){
                editor.putString("Day","14");
                Log.d("MyTag",sharedPreferences.getString("Day","w"));
            }else{
                editor.putString("Day","7");
            }
            if(rdgHour.getCheckedRadioButtonId() == rdb24Hour.getId()){
                Log.d("MyTag",sharedPreferences.getString("Time","w"));
                editor.putString("Time","24");
            }else{
                editor.putString("Time","12");
            }
            editor.commit();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        });
        //On Click Listener for Cancel Image View
        //Used for canceling current changes to settings then returning to home screen
        //Displays Dialogue Box and makes the user confirm they want to return
        ivCancel.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setTitle("Warning")
                    .setMessage("Are you sure you want to return to home screen? You have unsaved changes.")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("No",null)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
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
        //call loadSharedPref method to populate radiobuttons from shared preferences file
        loadSharedPref();
    }
    //method to read shared preferences files "SharedPref" and load in radio buttons based on values
    //default radio buttons are F 7Day and 12Hour
    public void loadSharedPref(){
        switch (sharedPreferences.getString("Unit","")){
            case"F":
                rdgTemp.check(rdbF.getId());
                break;
            case"C":
                rdgTemp.check(rdbC.getId());
                break;
        }
        switch (sharedPreferences.getString("Day","")){
            case"7":
                rdgDay.check(rdb7Day.getId());
                break;
            case"14":
                rdgDay.check(rdb14Day.getId());
                break;
        }
        switch (sharedPreferences.getString("Time","")){
            case"12":
                rdgHour.check(rdb12Hour.getId());
                break;
            case"24":
                rdgHour.check(rdb24Hour.getId());
                break;
        }
    }
}