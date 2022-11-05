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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class LocationActivity extends AppCompatActivity {
    String url_1 = "https://api.weatherapi.com/v1/forecast.json?key=0d2ee64c9feb4ccc9ff23426222810&q=";
    String url_2 = "&days=7&aqi=no&alerts=no";

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
        sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);

        //Instantiate controls
        etSearch = findViewById(R.id.etSearch);
        btnAdd = findViewById(R.id.btnAdd);
        recLocations = findViewById(R.id.recLocations);
        pbCircle = findViewById(R.id.pbCircle);
        imgBack = findViewById(R.id.imgBack);

        //Fetch data and add into RecyclerView
        adapter = new LocationAdapter(arlLocations, this);
        recLocations.setAdapter(adapter);
        recLocations.setLayoutManager(new LinearLayoutManager(this));
        queue = Volley.newRequestQueue(this);

        //Get data for the RecyclerView
        fetchData();

        //Swipe item controls
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                arlLocations.remove(viewHolder.getLayoutPosition());
                adapter.notifyItemRemoved(viewHolder.getLayoutPosition());
                adapter.notifyDataSetChanged();
            }
        });
        helper.attachToRecyclerView(recLocations);

        //Add location that the user set in the EditText
        btnAdd.setOnClickListener(view -> {
            insertSingleLocation(etSearch.getText().toString(), 1);
            etSearch.setText("");
        });
        //Set OnClick Listener for click event on Image, and send user back to Home page
        imgBack.setOnClickListener(view -> {
            startActivity(new Intent(LocationActivity.this, MainActivity.class));
        });
    }

    public void insertSingleLocation(String strSetLocation, int index) {
        String url = url_1 + strSetLocation + url_2;
        Log.d(TAG, "insertSingleLocation: " + strSetLocation);
        Location l = new Location();
        arlLocations.add(l);
        adapter.notifyDataSetChanged();
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
                        l.loadLocation(strName + ", " + strCountry,
                                dblLatitude, dblLongitude, dblF_Temperature, dblC_Temperature,
                                "https:" + jImg);

                       /* if(index == 1){
                            arlLocations.add(1, l);
                            adapter.notifyItemInserted(1);
                        } else {
                            if(arlLocations.size() == 0){
                                arlLocations.add(0, l);
                                adapter.notifyItemInserted(0);
                            } else {
                                arlLocations.add(l);
                                adapter.notifyItemInserted(1);
                            }
                        }
                        */

                        arlLocations.add(1, l);
                        adapter.notifyItemInserted(1);
                        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
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

    public void insertMassLocations(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            String url = url_1 + jsonArray.getString(i) + url_2;
            Log.d(TAG, "insertMassLocations: " + jsonArray.getString(i));
            pbCircle.setVisibility(View.VISIBLE);
            Location l = new Location();
            arlLocations.add(l);
            adapter.notifyDataSetChanged();
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
                            l.loadLocation(strName + ", " + strCountry,
                                    dblLatitude, dblLongitude, dblF_Temperature, dblC_Temperature,
                                    "https:" + jImg);

                            //Update from position 0
                            adapter.notifyItemInserted(0);
                            adapter.notifyItemRangeChanged(0, adapter.getItemCount());
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

    public void insertDefault() throws JSONException {
        //Populate default locations to view
        arlDefaultLocations.add("Saginaw");
        arlDefaultLocations.add("Detroit");
        arlDefaultLocations.add("New York City");
        arlDefaultLocations.add("Chicago");
        arlDefaultLocations.add("Houston");
        arlDefaultLocations.add("Phoenix");
        arlDefaultLocations.add("Philadelphia");
        arlDefaultLocations.add("San Antonio");
        arlDefaultLocations.add("San Diego");
        arlDefaultLocations.add("Dallas");
        arlDefaultLocations.add("San Jose");
        arlDefaultLocations.add("Paris");
        arlDefaultLocations.add("London");
        arlDefaultLocations.add("Bangkok");
        arlDefaultLocations.add("HongKong");
        arlDefaultLocations.add("Dubai");
        arlDefaultLocations.add("Singapore");
        arlDefaultLocations.add("Rome");
        arlDefaultLocations.add("Tokyo");
        arlDefaultLocations.add("Seoul");
        arlDefaultLocations.add("Sydney");

        //SharedPref for default locations
        SharedPreferences sharedPref = getSharedPreferences("SharedLoc", MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();

        //Set default locations into JSON Array
        JSONArray jsonArray = new JSONArray();
        try{
            for(int i =0; i < arlDefaultLocations.size(); i++){
                jsonArray.put(arlDefaultLocations.get(i));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        //Set the locations into the SharedPref
        //Insert default locations into the adapter
        spEditor.putString("jLocations", jsonArray.toString());
        spEditor.apply();
     //   insertMassLocations(jsonArray);
        Log.d(TAG, "insertDefault: " + jsonArray);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Get adapter items and save them into the SharedPref
        saveData();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void fetchData(){
        //Get the JSON Array in the SharedPref and re-insert the locations into the adapter
        SharedPreferences sharedPref = getSharedPreferences("SharedLoc", MODE_PRIVATE);
        SharedPreferences sharedSetLocation = getSharedPreferences("SharedPrefDefault", MODE_PRIVATE);

        try{
            String strJSON = sharedPref.getString("jLocations", "EMPTY");
            String strDefault = sharedSetLocation.getString("default_location", "");
            //If there were no locations found in the JSON Array, populate the array and adapter
            //  with default locations
            if(strJSON.equalsIgnoreCase("EMPTY")){
                insertDefault();
                JSONArray jsonArray = new JSONArray(sharedPref.getString("jLocations", ""));
            /*    for(int i = 0; i < jsonArray.length(); i++){
                    insertSingleLocation(jsonArray.getString(i), 0);
                }
             */
                insertMassLocations(jsonArray);
                Log.d(TAG, "fetchData Default: " + jsonArray);
            } else {
                //Insert locations into the adapter
                JSONArray jsonArray = new JSONArray(sharedPref.getString("jLocations", ""));
                insertMassLocations(jsonArray);
                Log.d(TAG, "fetchData: " + jsonArray);
            }
            Log.d(TAG, "Default Location: " + strDefault);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void saveData(){
        //Save locations into JSON Array
        SharedPreferences sharedPref = getSharedPreferences("SharedLoc", MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        //Save top/default location into String
        SharedPreferences sharedSetLocation = getSharedPreferences("SharedPrefDefault", MODE_PRIVATE);
        SharedPreferences.Editor spSetEditor = sharedSetLocation.edit();

        JSONArray jsonArray = new JSONArray();
        try{
            //Get every item in the adapter and save each into the JSON Array
            for(int i =0; i < adapter.getItemCount(); i++){
                jsonArray.put(adapter.getItem(i));
            }
            spSetEditor.putString("default_location", adapter.getItem(0));
        } catch (Exception e){
            e.printStackTrace();
        }
        spEditor.putString("jLocations", jsonArray.toString());
        spEditor.apply();
        spSetEditor.apply();
        Log.d(TAG, "saveData: " + jsonArray);
    }
}