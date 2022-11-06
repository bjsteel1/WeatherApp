package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
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
    String city,tempScale,format;
    int temp;
    SharedPreferences sharedPreferences;
    SimpleDateFormat TwentyFormat = new SimpleDateFormat("HH:mm");
    SimpleDateFormat TwelveFormat = new SimpleDateFormat("hh:mm a");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("SharedPrefs",MODE_PRIVATE);
        tempScale = sharedPreferences.getString("Unit","f");
        format = sharedPreferences.getString("Time","12");
        tempScale=tempScale.toLowerCase();
        setContentView(R.layout.activity_hourly);
        queue = Volley.newRequestQueue(this);
        forecasts = new ArrayList<>();
        fetchData();
        txtLocation = findViewById(R.id.txtLocation);
        txtMainTemp = findViewById(R.id.txtMainTemp);
        txtCoord = findViewById(R.id.txtCoord);
        rycHourly = findViewById(R.id.rycHourly);





    }
    public void fetchData(){
        adapter = new HourlyAdapter(forecasts,this);
        String location = "Saginaw,Mi";
        String url = "https://api.weatherapi.com/v1/forecast.json?key=0d2ee64c9feb4ccc9ff23426222810&q=" + location + "&aqi=no&alerts=no";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null,
                response->{
                    try {
                        JSONObject jsonForecast = response.getJSONObject("forecast");
                        JSONArray jsonDay = jsonForecast.getJSONArray("forecastday");
                        JSONArray jsonHourly = jsonDay.getJSONObject(0).getJSONArray("hour");
                        JSONObject jLocation = response.getJSONObject("location");
                        JSONObject jCurrent = response.getJSONObject("current");
                        temp = jCurrent.getInt("temp_"+tempScale);
                        city = jLocation.getString("name") + ", " + jLocation.getString("region");
                        double lat = jLocation.getDouble("lat");
                        double lon = jLocation.getDouble("lon");
                        for(int i = 0;i<jsonHourly.length();i++){
                            JSONObject jCondition = jsonHourly.getJSONObject(i).getJSONObject("condition");
                            Weather = jCondition.getString("icon");
                            Temp = Float.valueOf(jsonHourly.getJSONObject(i).getString("temp_"+tempScale));
                            time = jsonHourly.getJSONObject(i).getString("time").substring(11);
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

        Picasso.get().load("https:"+forecast.weather).into(holder.ivWeatherType);
        Picasso.get().setLoggingEnabled(true);
    }

    @Override
    public int getItemCount() {
        return forecasts.size();
    }


    class HourlyViewHolder extends RecyclerView.ViewHolder{
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


