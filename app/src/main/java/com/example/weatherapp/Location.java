/**
 * Location class.
 * @author: Jon Maddocks
 */
package com.example.weatherapp;

import java.util.ArrayList;
import java.util.Objects;

public class Location {
    String cityName;
    double latitude;
    double longitude;
    double f_temperature;
    double c_temperature;
    String picURL;

    //Default constructor. Set Location to default elements
    public Location(){
        cityName = "Loading";
        latitude = 0;
        longitude = 0;
        f_temperature = 0;
        c_temperature = 0;
        picURL = "https://www.freeiconspng.com/thumbs/load-icon-png/load-icon-png-8.png";
    }

    public Location(String cityName, double latitude, double longitude, double f_temperature, double c_temperature, String picURL) {
        this.cityName = cityName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.f_temperature = f_temperature;
        this.c_temperature = c_temperature;
        this.picURL = picURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Double.compare(location.latitude, latitude) == 0 && Double.compare(location.longitude, longitude) == 0 && Double.compare(location.f_temperature, f_temperature) == 0 && Double.compare(location.c_temperature, c_temperature) == 0 && Objects.equals(cityName, location.cityName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cityName, latitude, longitude, f_temperature, c_temperature);
    }
    //Set location data into location
    public void loadLocation(String cityName, double latitude, double longitude, double f_temperature, double c_temperature, String picURL){
        this.cityName = cityName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.f_temperature = f_temperature;
        this.c_temperature = c_temperature;
        this.picURL = picURL;
    }
}
