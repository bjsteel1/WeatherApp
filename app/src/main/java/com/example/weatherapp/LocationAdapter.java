package com.example.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

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
        SharedPreferences sharedPreferences = context.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE);

        //Get SharedPrefs for the temperature unit
        String strUnit = sharedPreferences.getString("Unit", "F");
        String f_temp = location.f_temperature + " \u00B0" + "F";
        String c_temp = location.c_temperature + "\u00B0" + "C";

        //For top position, mark as the set location with an image
        if(position == 0)
            holder.imgSetLocation.setVisibility(View.VISIBLE);
        else
            holder.imgSetLocation.setVisibility(View.GONE);

        //Set location name and forecast image
        holder.txtCity.setText(location.cityName);
        Picasso.get().load(location.picURL).into(holder.imgForecast);

        //Set the temperature unit from which the user set
        if(strUnit.equalsIgnoreCase("F"))
            holder.txtTemperature.setText(f_temp);
        else
            holder.txtTemperature.setText(c_temp);

        holder.txtCity.setOnClickListener(view -> {
            arlLocations.remove(position);
            arlLocations.add(0, location);
            notifyDataSetChanged();
        });
    }



    @Override
    public int getItemCount() {
        return arlLocations.size();
    }

    class LocationViewHolder extends RecyclerView.ViewHolder{
        TextView txtCity;
        TextView txtTemperature;
        ImageView imgSetLocation;
        ImageView imgForecast;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCity = itemView.findViewById(R.id.txtCity);
            txtTemperature = itemView.findViewById(R.id.txtTemperature);
            imgSetLocation = itemView.findViewById(R.id.imgSetLocation);
            imgForecast = itemView.findViewById(R.id.imgForecast);
        }


    }
}
