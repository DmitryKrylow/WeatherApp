package com.yadro.weatherapp.models;

public class Weather {
    private double latitude;
    private double longitude;
    private Daily daily;

    private Current current;

    public Current getCurrent() {
        return current;
    }
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Daily getDaily() {
        return daily;
    }

    public String getWeatherNow(){
        return current.toString();
    }
}
