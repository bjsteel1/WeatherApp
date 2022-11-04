package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class LocationActivity extends AppCompatActivity {
    String url_1 = "https://api.weatherapi.com/v1/forecast.json?key=0d2ee64c9feb4ccc9ff23426222810&q=";
    String url_2 = "&days=7&aqi=no&alerts=no";
    boolean flag = false;

    EditText etSearch;
    RecyclerView recLocations;
    ProgressBar pbCircle;
    Button btnAdd;
    ImageView imgBack;

    RequestQueue queue;
    LocationAdapter adapter;
    SharedPreferences sharedPreferences;
    ArrayList<Location> arlLocations = new ArrayList<>();
    ArrayList<String> arlDefaultLocations = new ArrayList<>();
    String TAG = "MYTAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        if(!flag){
            sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);

            //Instantiate controls
            etSearch = findViewById(R.id.etSearch);
            btnAdd = findViewById(R.id.btnAdd);
            recLocations = findViewById(R.id.recLocations);
            pbCircle = findViewById(R.id.pbCircle);
            imgBack = findViewById(R.id.imgBack);

            Log.d(TAG, "onCreate: ARL: " + arlDefaultLocations.size());
            if(arlDefaultLocations.size() == 0){
                //Add default locations
                arlDefaultLocations.add("Detroit");
                arlDefaultLocations.add("New-York-City");
                arlDefaultLocations.add("Chicago");
                arlDefaultLocations.add("Houston");
                arlDefaultLocations.add("Phoenix");
                arlDefaultLocations.add("Philadelphia");
                arlDefaultLocations.add("San-Antonio");
                arlDefaultLocations.add("San-Diego");
                arlDefaultLocations.add("Dallas");
                arlDefaultLocations.add("San-Jose");
                arlDefaultLocations.add("Paris");
                arlDefaultLocations.add("London");
                arlDefaultLocations.add("Bangkok");
                arlDefaultLocations.add("Hong-Kong");
                arlDefaultLocations.add("Dubai");
                arlDefaultLocations.add("Singapore");
                arlDefaultLocations.add("Rome");
                arlDefaultLocations.add("Tokyo");
                arlDefaultLocations.add("Seoul");
                arlDefaultLocations.add("Sydney");
            }

            //Fetch data and add into RecyclerView
            if(adapter == null){
                adapter = new LocationAdapter(arlLocations, this);
                recLocations.setAdapter(adapter);
                recLocations.setLayoutManager(new LinearLayoutManager(this));
                queue = Volley.newRequestQueue(this);

                insertSingleLocation("Saginaw");
                insertDefaultLocations();
            }


            //Swipe item controls
            ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    arlLocations.remove(viewHolder.getLayoutPosition());
                    adapter.notifyItemChanged(viewHolder.getLayoutPosition());
                    adapter.notifyDataSetChanged();
                }
            });
            helper.attachToRecyclerView(recLocations);

            //Add location that the user set in the EditText
            btnAdd.setOnClickListener(view -> {
                insertSingleLocation(etSearch.getText().toString());
            });
            //Set OnClick Listener for click event on Image, and send user back to Home page
            imgBack.setOnClickListener(view -> {
                startActivity(new Intent(LocationActivity.this, MainActivity.class));
            });
            flag = true;
        }
    }


    public void insertDefaultLocations() {
        Log.d(TAG, "insertDefaultLocations: " + adapter.getItemCount());
        for (int i = 0; i < arlDefaultLocations.size(); i++) {
            String url = url_1 + arlDefaultLocations.get(i) + url_2;
            pbCircle.setVisibility(View.VISIBLE);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            //Get location name & country
                            JSONObject jLocation = response.getJSONObject("location");
                            String strName = jLocation.getString("name");
                            String strCountry = jLocation.getString("country");
                            if (strCountry.equalsIgnoreCase("United States of America"))
                                strCountry = "USA";

                            //Get location longitude & latitude
                            double dblLatitude = jLocation.getDouble("lat");
                            double dblLongitude = jLocation.getDouble("lon");

                            //Get location temperature for Fahrenheit & Celsius
                            JSONObject jTemperature = response.getJSONObject("current");
                            double dblF_Temperature = jTemperature.getDouble("temp_f");
                            double dblC_Temperature = jTemperature.getDouble("temp_c");

                            //Get location forecast icon
                            String jImg = response.getJSONObject("forecast")
                                    .getJSONArray("forecastday")
                                    .getJSONObject(0)
                                    .getJSONObject("day")
                                    .getJSONObject("condition")
                                    .getString("icon");

                            //Create location
                            Location l = new Location(strName + ", " + strCountry,
                                    dblLatitude, dblLongitude, dblF_Temperature, dblC_Temperature,
                                    "https:" + jImg);
                            arlLocations.add(l);

                            adapter.notifyDataSetChanged();
                            pbCircle.setVisibility(View.INVISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {

                    });
            queue.add(request);
        }
    }

    public void insertSingleLocation(String strSetLocation) {
        String url = url_1 + strSetLocation + url_2;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    pbCircle.setVisibility(View.VISIBLE);
                    try {
                        //Get location name & country
                        JSONObject jLocation = response.getJSONObject("location");
                        String strName = jLocation.getString("name");
                        String strCountry = jLocation.getString("country");
                        if (strCountry.equalsIgnoreCase("United States of America")
                                || strCountry.equalsIgnoreCase("USA United States of America"))
                            strCountry = "USA";

                        //Get location longitude & latitude
                        double dblLatitude = jLocation.getDouble("lat");
                        double dblLongitude = jLocation.getDouble("lon");

                        //Get location temperature for Fahrenheit & Celsius
                        JSONObject jTemperature = response.getJSONObject("current");
                        double dblF_Temperature = jTemperature.getDouble("temp_f");
                        double dblC_Temperature = jTemperature.getDouble("temp_c");

                        //Get location forecast icon
                        String jImg = response.getJSONObject("forecast")
                                .getJSONArray("forecastday")
                                .getJSONObject(0)
                                .getJSONObject("day")
                                .getJSONObject("condition")
                                .getString("icon");

                        //Create location
                        Location l = new Location(strName + ", " + strCountry,
                                dblLatitude, dblLongitude, dblF_Temperature, dblC_Temperature,
                                "https:" + jImg);
                        arlLocations.add(0, l);

                        adapter.notifyDataSetChanged();
                        pbCircle.setVisibility(View.INVISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    //If the user entered an invalid location, prompt an error dialog box
                    AlertDialog.Builder errorBuilder = new AlertDialog.Builder(LocationActivity.this);
                    errorBuilder.setMessage("'" + etSearch.getText().toString() + "' was NOT found!");
                    errorBuilder.setTitle("ALERT!");
                    errorBuilder.setCancelable(false);
                    errorBuilder.setPositiveButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> {
                        dialog.cancel();
                    });

                    AlertDialog ad = errorBuilder.create();
                    ad.show();
                });
        queue.add(request);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: " + adapter.getItemCount());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: " + adapter.getItemCount());
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: " + adapter.getItemCount());
        adapter.notifyDataSetChanged();
    }
}