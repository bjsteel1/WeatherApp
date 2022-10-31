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

import java.util.ArrayList;

public class LocationActivity extends AppCompatActivity {
    EditText etSearch;
    Button btnAdd;
    RecyclerView recLocations;
    String TAG = "MYTAG";

    ArrayList<Location> arlLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        arlLocations = new ArrayList<>();

        etSearch = findViewById(R.id.etSearch);
        btnAdd = findViewById(R.id.btnAdd);
        recLocations = findViewById(R.id.recLocations);

        for(int i =0; i < 20; i++){
            arlLocations.add(new Location("Mayville " + i, 10,10,10,10));
        }
        LocationAdapter adapter = new LocationAdapter(arlLocations, this);
        recLocations.setAdapter(adapter);
        recLocations.setLayoutManager(new LinearLayoutManager(this));


        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
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