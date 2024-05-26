package com.yadro.weatherapp.models;

import java.util.Date;
import java.util.List;

public class Current {
    private String time;
    private String weather_code;
    private String temperature_2m;
    private Integer is_day;

    @Override
    public String toString(){
        return temperature_2m + " °C";
    }

    public String getWeather_code() {
        return weather_code;
    }

    public Integer getIs_day() {
        return is_day;
    }
}
