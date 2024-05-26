package com.yadro.weatherapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.yadro.weatherapp.models.Weather;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MediaType;

public class WeatherApp {
    private String url = "";
    private double latitude;
    private double longitude;
    private String address;
    private Weather w = new Weather();
    private OkHttpClient mClient = new OkHttpClient();

    private LocationManager location;

    public void prepare(Context context){

        location = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);


        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        };

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (location != null) {
            if(location.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER) != null) {
                latitude = location.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER).getLatitude();
                longitude = location.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER).getLongitude();
            }
        }

    }
    public void getLocation(Context context){
        prepare(context);

        Request request = new Request.Builder().url(String.format(Locale.US,"https://nominatim.openstreetmap.org/reverse?format=json&lat=%f&lon=%f",latitude, longitude)).build();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    mClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            address = "Ошибка определения локации";
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            String json = response.body().string();
                            address = new Gson().fromJson(json, JsonElement.class).getAsJsonObject().get("name").getAsString();
                        }
                    });
                }
            }).start();

    }
    public void getWeather(Context context) {

        prepare(context);

        new Thread(new Runnable() {
            @Override
            public void run() {
                url = String.format(Locale.US, "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&current=temperature_2m,is_day,weather_code&daily=weather_code,temperature_2m_max,temperature_2m_min&timezone=auto&forecast_days=10", latitude, longitude);
                Request httpGet = new Request.Builder().url(url).build();
                Call call = mClient.newCall(httpGet);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        w = null;
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        Gson gson = new GsonBuilder().create();
                        String json = response.body().string();
                        w = gson.fromJson(json, Weather.class);
                    }
                });
            }
        }).start();
    }

    public String showWeatherNow(){
        return address + "\n" + w.getWeatherNow();
    }
    public ArrayAdapter makeAdapter(Context context){

        ArrayAdapter ad = new ArrayAdapter(context, R.layout.view_adapter, android.R.id.text1, w.getDaily().getTemperature_2m_max())
        {
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                ImageView icon = view.findViewById(R.id.icon_weather);
                text1.setText(w.getDaily().getTimeOnPos(position));
                text2.setText(w.getDaily().getMaxMinOnPos(position));
                icon.setImageResource(getWeatherCode(w.getDaily().getWeather_code().get(position)));
                return view;
            }
        };

        return ad;
    }

    public Weather getWeather() {
        return w;
    }

    public String getAddress() {
        return address;
    }

    public int getWeatherCode(int code){
        int resID = 0;
        if (code == 0){
            if (w.getCurrent().getIs_day() == 1){
                resID = R.drawable.clear_icon;
            }else{
                resID = R.drawable.clear_night_icon;
            }
        }else if(code >= 1 && code <= 3){
            resID = R.drawable.cloudly_icon;
        }else if(code >= 45 && code <= 48){
            resID = R.drawable.fog_icon;
        }else if(code >= 51 && code <= 82){
            resID = R.drawable.rain_icon;
        }else if(code >= 71 && code <= 86){
            resID = R.drawable.snow_icon;
        }else{
            resID = R.drawable.thundershtorm_icon;
        }
        return resID;
    }
    public int getWeatherCode(){
        int resID = 0;
        int code = Integer.parseInt(w.getCurrent().getWeather_code());

        if (code == 0){
            if (w.getCurrent().getIs_day() == 1){
                resID = R.drawable.clear_sky_day;
            }else{
                resID = R.drawable.clear_sky_night;
            }
        }else if(code >= 1 && code <= 3){
            resID = R.drawable.cloudly;
        }else if(code >= 45 && code <= 48){
            resID = R.drawable.fog_icon;
        }else if(code >= 51 && code <= 82){
            resID = R.drawable.rain;
        }else if(code >= 71 && code <= 86){
            resID = R.drawable.snow;
        }else{
            resID = R.drawable.thundershtorm;
        }
        return resID;
    }
}
