package com.yadro.weatherapp.models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Daily {

    private List<String> time;
    private List<Integer> weather_code;
    private List<Double> temperature_2m_max;
    private List<Double> temperature_2m_min;

    public Daily() {
    }

    public List<String> getTime() {
        return time;
    }

    public String getTimeOnPos(int pos){
        String date = time.get(pos);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt = sdf.parse(date);
            DateFormat dateFormat = new SimpleDateFormat("EE");
            String dayOfWeek = dateFormat.format(dt);
            return dayOfWeek + "\n" + date;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Integer> getWeather_code() {
        return weather_code;
    }

    public List<Double> getTemperature_2m_max() {
        return temperature_2m_max;
    }

    public List<Double> getTemperature_2m_min() {
        return temperature_2m_min;
    }

    public String getMaxMinOnPos(int pos){
        return String.format("%.1f °C\n%.1f °C", temperature_2m_max.get(pos), temperature_2m_min.get(pos));
    }

}
