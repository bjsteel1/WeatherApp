/**
 * RecyclerView.Adapter class for Location.
 * @author: Jon Maddocks
 */
package com.example.weatherapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

        //Getting strange errors when using the parameter, position, when trying to get the index
        //  of the current item being clicked. Using a redundant int variable seems to fix it
        int p = position;
        holder.txtLayout.setOnLongClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Would you like to set " + holder.txtCity.getText() + " as the default location?")
                            .setCancelable(false).setPositiveButton("Yes", (dialogInterface, i) -> {
                                arlLocations.remove(p);
                                arlLocations.add(0, location);
                                notifyDataSetChanged();
                            }).setNegativeButton("No", (dialogInterface, i) -> dialogInterface.cancel());
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        });
    }
    //Return total number of locations
    @Override
    public int getItemCount() {
        return arlLocations.size();
    }
    //Return item at certain index
    public String getItem(int position){
        return arlLocations.get(position).cityName;
    }

    class LocationViewHolder extends RecyclerView.ViewHolder{
        TextView txtCity;
        TextView txtTemperature;
        TextView txtLayout;
        ImageView imgSetLocation;
        ImageView imgForecast;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCity = itemView.findViewById(R.id.txtCity);
            txtTemperature = itemView.findViewById(R.id.txtTemperature);
            txtLayout = itemView.findViewById(R.id.txtLayout);
            imgSetLocation = itemView.findViewById(R.id.imgSetLocation);
            imgForecast = itemView.findViewById(R.id.imgForecast);
        }
    }
}
