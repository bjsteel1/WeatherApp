package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

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

        String f_temp = location.f_temperature + " \u00B0" + "F";
        String c_temp = location.c_temperature + "\u00B0" + "C";

        holder.txtCity.setText(location.cityName);
        Picasso.get().load(location.picURL).into(holder.imgForecast);

        //Requires if statement later to determine if the settings are set to F or C
        holder.txtTemperature.setText(f_temp);
    }

    @Override
    public int getItemCount() {
        return arlLocations.size();
    }

    class LocationViewHolder extends RecyclerView.ViewHolder{

        TextView txtCity;
        TextView txtTemperature;
        ImageView imgForecast;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCity = itemView.findViewById(R.id.txtCity);
            txtTemperature = itemView.findViewById(R.id.txtTemperature);
            imgForecast = itemView.findViewById(R.id.imgForecast);
        }
    }
}
