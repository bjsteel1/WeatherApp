package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {
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
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        Location location = arlLocations.get(position);

        holder.txtCity.setText(location.cityName);
        holder.txtTemperature.setText(location.f_temperature + "");
    }

    @Override
    public int getItemCount() {
        return arlLocations.size();
    }

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
