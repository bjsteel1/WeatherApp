package com.example.weatherapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.Date;

public class ForecastAdapter extends BaseAdapter {
    JSONArray forecastArray;
    Context context;
    String tempScale;

    public ForecastAdapter(JSONArray forecastArray, Context context, String tempScale){
        this.forecastArray = forecastArray;
        this.context = context;
        this.tempScale = tempScale;
    }

    @Override
    public int getCount() {
        return forecastArray.length();
    }

    @Override
    public Object getItem(int i) {
        try {
            return forecastArray.get(i);
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        JSONObject forecastDay = (JSONObject)getItem(i);
        if (view==null)
            view = LayoutInflater.from(context).inflate(R.layout.layout_forecast_item,viewGroup,false);

        TextView tvDate = view.findViewById(R.id.tvDate);
        TextView tvTemp = view.findViewById(R.id.tvForecastTemp);
        ImageView ivIcon = view.findViewById(R.id.ivForecastIcon);

        try{
            LocalDate date = LocalDate.parse(forecastDay.getString("date"));
            String dateString = date.getDayOfWeek().toString().substring(0,3).toLowerCase() + " "
                    + date.getMonth().toString().substring(0,3).toLowerCase() + " "
                    + date.getDayOfMonth();
            // Capitalize the date string
            dateString = dateString.substring(0,1).toUpperCase() + dateString.substring(1,4) + dateString.substring(4,5).toUpperCase()  + dateString.substring(5);
            double avgTemp = forecastDay.getJSONObject("day").getDouble("avgtemp_" + tempScale.toLowerCase());
            String iconURL = "https:" + forecastDay.getJSONObject("day").getJSONObject("condition").getString("icon");

            tvDate.setText(dateString);
            tvTemp.setText(avgTemp + "\u00B0 " + tempScale);
            Picasso.get().load(iconURL).into(ivIcon);
        } catch (Exception e){
            tvDate.setText("Could not get forecast");
        }

        return view;
    }
}
