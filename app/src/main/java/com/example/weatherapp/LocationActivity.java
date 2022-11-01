package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
    EditText etSearch;
    Button btnAdd;
    RecyclerView recLocations;
    String TAG = "MYTAG";

    ArrayList<Location> arlLocations;
    ArrayList<String> arlDefaultLocations;
    RequestQueue queue;
    LocationAdapter adapter;
    ProgressBar pbCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        //Instantiate ArrayLists
        arlLocations = new ArrayList<>();
        arlDefaultLocations = new ArrayList<>();
        //Instantiate controls
        etSearch = findViewById(R.id.etSearch);
        btnAdd = findViewById(R.id.btnAdd);
        recLocations = findViewById(R.id.recLocations);
        pbCircle = findViewById(R.id.pbCircle);

        //Add default locations
        //  ---- Add persistence so this doesn't have to load each time ---- //
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

        //Fetch data and add into RecyclerView
        queue = Volley.newRequestQueue(this);
        fetchData();
        adapter = new LocationAdapter(arlLocations, this);
        recLocations.setAdapter(adapter);
        recLocations.setLayoutManager(new LinearLayoutManager(this));


        //Swipe item controls
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                arlLocations.remove(viewHolder.getLayoutPosition());
                adapter.notifyDataSetChanged();
            }
        });
        helper.attachToRecyclerView(recLocations);


        //Add location that the user set in the EditText
        btnAdd.setOnClickListener(view -> {
            insertLocation(etSearch.getText().toString());
        });
    }


    public void fetchData() {
        //   String url = "https://api.weatherapi.com/v1/forecast.json?key=0d2ee64c9feb4ccc9ff23426222810&q=London&days=7&aqi=no&alerts=no";
        String url_1 = "https://api.weatherapi.com/v1/forecast.json?key=0d2ee64c9feb4ccc9ff23426222810&q=";
        String url_2 = "&days=7&aqi=no&alerts=no";

        //Insert's user set location
        //Currently pointed at Saginaw, MI. Needs to get intent object from other activities

        for (int i = 0; i < arlDefaultLocations.size(); i++) {
            String url = url_1 + arlDefaultLocations.get(i) + url_2;
            pbCircle.setVisibility(View.VISIBLE);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {
                        try {
                            JSONObject jLocation = response.getJSONObject("location");
                            String strName = jLocation.getString("name");
                            String strCountry = jLocation.getString("country");
                            if (strCountry.equalsIgnoreCase("United States of America"))
                                strCountry = "USA";

                            double dblLatitude = jLocation.getDouble("lat");
                            double dblLongitude = jLocation.getDouble("lon");

                            JSONObject jTemperature = response.getJSONObject("current");
                            double dblF_Temperature = jTemperature.getDouble("temp_f");
                            double dblC_Temperature = jTemperature.getDouble("temp_c");

                            Location l = new Location(strName + ", " + strCountry,
                                    dblLatitude, dblLongitude, dblF_Temperature, dblC_Temperature);
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

    public void insertLocation(String strSetLocation) {
        String url_1 = "https://api.weatherapi.com/v1/forecast.json?key=0d2ee64c9feb4ccc9ff23426222810&q=";
        String url_2 = "&days=7&aqi=no&alerts=no";

        //Get intent object from other activity?
        //for now, default to Saginaw

        String url = url_1 + strSetLocation + url_2;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    pbCircle.setVisibility(View.VISIBLE);
                    try {
                        JSONObject jLocation = response.getJSONObject("location");
                        String strName = jLocation.getString("name");
                        String strCountry = jLocation.getString("country");
                        if (strCountry.equalsIgnoreCase("United States of America")
                                || strCountry.equalsIgnoreCase("USA United States of America"))
                            strCountry = "USA";

                        double dblLatitude = jLocation.getDouble("lat");
                        double dblLongitude = jLocation.getDouble("lon");

                        JSONObject jTemperature = response.getJSONObject("current");
                        double dblF_Temperature = jTemperature.getDouble("temp_f");
                        double dblC_Temperature = jTemperature.getDouble("temp_c");

                        Location l = new Location(strName + ", " + strCountry,
                                dblLatitude, dblLongitude, dblF_Temperature, dblC_Temperature);
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
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}