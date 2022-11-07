/**
 * This class represents the Location Activity, and is responsible for accessing the
 *  api.weatherapi.com and loading/adding/deleting locations. The user has the ability
 *  add any valid location, delete any location in the RecyclerView by swiping right, and
 *  resetting the entire list to the applications default list of locations.
 *
 * @author: Jon Maddocks
 */

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
    //Views for activity
    EditText etSearch;
    RecyclerView recLocations;
    ProgressBar pbCircle;
    Button btnAdd;
    ImageView imgBack;
    ImageView imgResetLocs;

    //RequestQueue and adapters
    RequestQueue queue;
    LocationAdapter adapter;

    //SharedPrefs and necessary ArrayLists
    SharedPreferences sharedPreferences;
    ArrayList<Location> arlLocations = new ArrayList<>();
    ArrayList<String> arlDefaultLocations = new ArrayList<>();
    String TAG = "MYTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        sharedPreferences = getSharedPreferences("SharedPrefs", MODE_PRIVATE);

        //Instantiate views
        etSearch = findViewById(R.id.etSearch);
        btnAdd = findViewById(R.id.btnAdd);
        recLocations = findViewById(R.id.recLocations);
        pbCircle = findViewById(R.id.pbCircle);
        imgBack = findViewById(R.id.imgBack);
        imgResetLocs = findViewById(R.id.imgResetLocs);

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
            public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                if(viewHolder instanceof LocationAdapter.LocationViewHolder){
                    if(viewHolder.getLayoutPosition() == 0)
                        return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
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
            insertSingleLocation(etSearch.getText().toString());
            recLocations.scrollToPosition(0);
        });
        //Set OnClick Listener for click event on back image, and send user back to Home page
        imgBack.setOnClickListener(view -> {
            if(loadCheck()){
                //Save set/default location into String
                // Default location is the top item in the RV. Save into String
                SharedPreferences sharedSetLocation = getSharedPreferences("SharedPrefDefault", MODE_PRIVATE);
                SharedPreferences.Editor spSetEditor = sharedSetLocation.edit();
                spSetEditor.putString("default_location", adapter.getItem(0));
                spSetEditor.apply();
                startActivity(new Intent(LocationActivity.this, MainActivity.class));
            }
        });
        //Set OnClick listener for click event on reset image, and delete every item in the
        //  RecyclerView and populate the list with default locations
        imgResetLocs.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Would you like to reset the list to default locations?")
                        .setCancelable(false).setPositiveButton("Yes", (dialogInterface, i) -> {
                            try {
                                //Clear all locations and set the default JSON array of locations
                                //  Get the default data
                                arlLocations.clear();
                                arlDefaultLocations.clear();
                                adapter.notifyDataSetChanged();
                                insertDefault();
                                fetchData();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel());
                AlertDialog alert = builder.create();
                alert.show();
        });
    }
    // Insert a single location into the RecyclerView
    public void insertSingleLocation(String strSetLocation) {
        //Get the URL from string resources
        String url_1 = getResources().getString(R.string.url_part1);
        String url_2 = getResources().getString(R.string.url_part2);
        String url = url_1 + strSetLocation + url_2;
        //Add location to the RV with default data
        Location l = new Location();
        arlLocations.add(1,l);
        adapter.notifyDataSetChanged();
        //Make API request
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
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

                        //Get location condition icon
                        String jImg = response.getJSONObject("current")
                                .getJSONObject("condition")
                                .getString("icon");

                        //Load Location data into the previously create RV item
                        l.loadLocation(strName + ", " + strCountry,
                                dblLatitude, dblLongitude, dblF_Temperature, dblC_Temperature,
                                "https:" + jImg);

                        //Update from position 1
                        adapter.notifyItemInserted(1);
                        adapter.notifyDataSetChanged();
                        etSearch.setText("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    //User entered an invalid location. Prompt error dialog
                    arlLocations.remove(l);
                    adapter.notifyDataSetChanged();
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
                    etSearch.setText("");
                });
        queue.add(request);
    }

    // Insert numerous locations into RV
    public void insertMassLocations(JSONArray jsonArray) throws JSONException {
        //Get the URL from String resources
        String url_1 = getResources().getString(R.string.url_part1);
        String url_2 = getResources().getString(R.string.url_part2);
        //Get one JSON item at a time
        for (int i = 0; i < jsonArray.length(); i++) {
            String url = url_1 + jsonArray.getString(i) + url_2;
            //Add location to the RV with default data
            Location l = new Location();
            arlLocations.add(l);
            adapter.notifyDataSetChanged();
            //Make API request
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

                            //Get location condition icon
                            String jImg = response.getJSONObject("current")
                                    .getJSONObject("condition")
                                    .getString("icon");

                            //Load Location data into the previously create RV item
                            l.loadLocation(strName + ", " + strCountry,
                                    dblLatitude, dblLongitude, dblF_Temperature, dblC_Temperature,
                                    "https:" + jImg);

                            //Update from position 0
                            adapter.notifyItemInserted(0);
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {});
            queue.add(request);
        }
    }

    // Put default locations into JSON Array and set SharedPref
    public void insertDefault() throws JSONException {
        //Populate default locations ArrayList
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Get adapter items and save them into the SharedPref
        saveData();
    }
    // Get JSON Array in SharedPref and insert all locations into RV
    public void fetchData(){
        //Get the JSON Array in the SharedPref and re-insert the locations into the adapter
        SharedPreferences sharedPref = getSharedPreferences("SharedLoc", MODE_PRIVATE);
        try{
            //Set visibility
            pbCircle.setVisibility(View.VISIBLE);
            recLocations.setVisibility(View.GONE);
            String strJSON = sharedPref.getString("jLocations", "EMPTY");
            //If there were no locations found in the JSON Array, populate the array and adapter
            //  with default locations
            if(strJSON.equalsIgnoreCase("EMPTY")){
                insertDefault();
                JSONArray jsonArray = new JSONArray(sharedPref.getString("jLocations", ""));
                insertMassLocations(jsonArray);
            } else {
                //Insert locations into the adapter
                JSONArray jsonArray = new JSONArray(sharedPref.getString("jLocations", ""));
                insertMassLocations(jsonArray);
            }
            pbCircle.setVisibility(View.INVISIBLE);
            recLocations.setVisibility(View.VISIBLE);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    // Ran onStop(), save all items in adapter to SharedPref and save user set location to SharedPref
    public void saveData(){
        //Save locations into JSON Array
        SharedPreferences sharedPref = getSharedPreferences("SharedLoc", MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPref.edit();
        JSONArray jsonArray = new JSONArray();
        try{
            //Get every item in the adapter and save each into the JSON Array
            for(int i =0; i < adapter.getItemCount(); i++){
                jsonArray.put(adapter.getItem(i));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        // All locations save into JSON Array
        spEditor.putString("jLocations", jsonArray.toString());
        spEditor.apply();
    }

    // Check to ensure no API item is still loading. Stop user until all items have been loaded
    public boolean loadCheck(){
        for(int i = 0; i < arlLocations.size(); i++){
            if(arlLocations.get(i).cityName.equalsIgnoreCase("Loading")){
                AlertDialog.Builder errorBuilder = new AlertDialog.Builder(LocationActivity.this);
                errorBuilder.setMessage("Please wait one moment. . .");
                errorBuilder.setTitle("API ALERT!");
                errorBuilder.setCancelable(false);
                errorBuilder.setPositiveButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.cancel();
                });
                AlertDialog ad = errorBuilder.create();
                ad.show();
                return false;
            }
        }
        return true;
    }
}