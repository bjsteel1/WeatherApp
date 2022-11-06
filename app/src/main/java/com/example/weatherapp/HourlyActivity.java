package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HourlyActivity extends AppCompatActivity {
    //declaring my variables
    ImageView ivhome;
    TextView txtLocation;
    TextView txtMainTemp;
    TextView txtCoord;
    RecyclerView rycHourly;
    ArrayList<HourlyForecast> forecasts;
    RequestQueue queue;
    String time;
    Float Temp;
    String Weather;
    HourlyAdapter adapter;
    String city,tempScale,format,defaultLocation;
    int temp;
    SharedPreferences sharedPreferences;
    SimpleDateFormat TwentyFormat = new SimpleDateFormat("HH:mm");
    SimpleDateFormat TwelveFormat = new SimpleDateFormat("hh:mm a");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //shared prefs for settings
        sharedPreferences = getSharedPreferences("SharedPrefs",MODE_PRIVATE);
        //sets up the unit temp and time
        tempScale = sharedPreferences.getString("Unit","f");
        format = sharedPreferences.getString("Time","12");
        tempScale=tempScale.toLowerCase();
        setContentView(R.layout.activity_hourly);
        queue = Volley.newRequestQueue(this);
        forecasts = new ArrayList<>();
        //setting up views
        txtLocation = findViewById(R.id.txtLocation);
        txtMainTemp = findViewById(R.id.txtMainTemp);
        txtCoord = findViewById(R.id.txtCoord);
        rycHourly = findViewById(R.id.rycHourly);
        ImageView ivHome = findViewById(R.id.ivHome);
        //shared pref for location
        SharedPreferences sharedSetLocation = getSharedPreferences("SharedPrefDefault", MODE_PRIVATE);
        defaultLocation = sharedSetLocation.getString("default_location","Saginaw, USA");
        //calls api
        fetchData(defaultLocation);

        //goes back home
        ivHome.setOnClickListener(view->{
            startActivity(new Intent(HourlyActivity.this, MainActivity.class));
        });

    }
    public void fetchData(String location){
        //creates the adapter
        adapter = new HourlyAdapter(forecasts,this);
        //url
        String url = getResources().getString(R.string.url_part1)+ location + "&aqi=no&alerts=no";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null,
                response->{
                    try {
                        //getting all JSON objects and arrays
                        JSONObject jsonForecast = response.getJSONObject("forecast");
                        JSONArray jsonDay = jsonForecast.getJSONArray("forecastday");
                        JSONArray jsonHourly = jsonDay.getJSONObject(0).getJSONArray("hour");
                        JSONObject jLocation = response.getJSONObject("location");
                        JSONObject jCurrent = response.getJSONObject("current");

                        temp = jCurrent.getInt("temp_"+tempScale);
                        city = jLocation.getString("name") + ", " + jLocation.getString("region");
                        double lat = jLocation.getDouble("lat");
                        double lon = jLocation.getDouble("lon");
                        //for each hour in the day create a new forcast

                        for(int i = 0;i<jsonHourly.length();i++){
                            JSONObject jCondition = jsonHourly.getJSONObject(i).getJSONObject("condition");
                            Weather = jCondition.getString("icon");
                            Temp = Float.valueOf(jsonHourly.getJSONObject(i).getString("temp_"+tempScale));
                            time = jsonHourly.getJSONObject(i).getString("time").substring(11);
                            //this just sets up the time
                            if(format.equals("12")){
                                try {
                                    Date date = TwentyFormat.parse(time);
                                    time = TwelveFormat.format(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            if(format.equals("24")){
                                try {
                                    Date date = TwentyFormat.parse(time);
                                    time = TwentyFormat.format(date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            HourlyForecast forecast0 = new HourlyForecast(Weather,Temp,time);
                            forecasts.add(forecast0);
                        }
                        //sets data for non recycler view and calls the adapter with data
                        txtLocation.setText(city);
                        txtCoord.setText(lat + "°N, " + lon + " °W");
                        txtMainTemp.setText(temp+"°");
                        rycHourly.setAdapter(adapter);
                        rycHourly.setLayoutManager(new LinearLayoutManager(this));
                        adapter.notifyDataSetChanged();



                    } catch (JSONException e) {

                        e.printStackTrace();
                    }


                },error->{

        });

        queue.add(request);
    }
}
//my class for hourly forcase
class HourlyForecast{
    String weather;
    Float temperature;
    String time;

    public HourlyForecast(String weather, Float temperature, String time) {
        this.weather = weather;
        this.temperature = temperature;
        this.time = time;
    }

}
//standard adapter code from class
    class HourlyAdapter extends RecyclerView.Adapter<HourlyAdapter.HourlyViewHolder>{
    ArrayList<HourlyForecast> forecasts;
    Context context;


    public HourlyAdapter(ArrayList<HourlyForecast> forecasts, Context context) {
        this.forecasts = forecasts;
        this.context = context;
    }

    @NonNull
    @Override
    public HourlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_hourly_item,parent,false);
        return new HourlyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyAdapter.HourlyViewHolder holder, int position) {
        HourlyForecast forecast = forecasts.get(position);
        holder.txtTemperture.setText(Math.round(forecast.temperature)+"°");
        holder.txtTime.setText(forecast.time+"");

        //loads the image
        Picasso.get().load("https:"+forecast.weather).into(holder.ivWeatherType);
        Picasso.get().setLoggingEnabled(true);
    }

    @Override
    public int getItemCount() {
        return forecasts.size();
    }


    class HourlyViewHolder extends RecyclerView.ViewHolder{
        //sets up view holder
        ImageView ivWeatherType;
        TextView txtTime;
        TextView txtTemperture;


        public HourlyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivWeatherType = itemView.findViewById(R.id.ivWeatherType);
            ivWeatherType.setMinimumHeight(200);
            ivWeatherType.setMinimumHeight(200);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtTemperture = itemView.findViewById(R.id.txtTemperture);

        }
    }



}


