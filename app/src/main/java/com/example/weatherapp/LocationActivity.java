package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        arlLocations = new ArrayList<>();

        etSearch = findViewById(R.id.etSearch);
        btnAdd = findViewById(R.id.btnAdd);
        recLocations = findViewById(R.id.recLocations);

        arlDefaultLocations = new ArrayList<>();
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

        queue = Volley.newRequestQueue(this);
        fetchData();


        adapter = new LocationAdapter(arlLocations, this);
        recLocations.setAdapter(adapter);
        recLocations.setLayoutManager(new LinearLayoutManager(this));


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
    }

    public void fetchData(){
     //   String url = "https://api.weatherapi.com/v1/forecast.json?key=0d2ee64c9feb4ccc9ff23426222810&q=London&days=7&aqi=no&alerts=no";
        String URL_1 ="https://api.weatherapi.com/v1/forecast.json?key=0d2ee64c9feb4ccc9ff23426222810&q=";
        String URL_City = "";
        String URL_2 = "&days=7&aqi=no&alerts=no";

        String url = URL_1 + arlDefaultLocations.get(0) + URL_2;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject jLocation = response.getJSONObject("location");
                        String strName = jLocation.getString("name");
                        String strCountry = jLocation.getString("country");

                        JSONObject jTemperature = response.getJSONObject("current");
                        double dblTemperature = jTemperature.getDouble("temp_f");

                        Location l = new Location(strName + ", " + strCountry, 10, 10, dblTemperature, 10);
                        arlLocations.add(l);

                        adapter.notifyDataSetChanged();
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                },
                error -> {

                });
        queue.add(request);

    }


    class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {
        ArrayList<Location> arlLocations;
        Context context;

        public LocationAdapter(ArrayList<Location> arlLocations, Context context) {
            this.arlLocations = arlLocations;
            this.context = context;
        }

        @NonNull
        @Override
        public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_location_item, parent, false);
            Log.d(TAG, "onCreateViewHolder: CALLED");
            return new LocationViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
            Log.d(TAG, "onBindViewHolder: CALLED");
            Location location = arlLocations.get(position);

            holder.txtCity.setText(location.cityName);
            holder.txtTemperature.setText(location.f_temperature + "");
        }

        @Override
        public int getItemCount() {
            return arlLocations.size();
        }

        // ------------------------------------------------------------------------- //
        class LocationViewHolder extends RecyclerView.ViewHolder{

            TextView txtCity;
            TextView txtTemperature;
            public LocationViewHolder(@NonNull View itemView) {
                super(itemView);
                txtCity = itemView.findViewById(R.id.txtCity);
                txtTemperature = itemView.findViewById(R.id.txtTemperature);
            }
        }
    }
}