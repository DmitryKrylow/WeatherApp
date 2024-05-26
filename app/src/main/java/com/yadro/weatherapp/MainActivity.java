package com.yadro.weatherapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {
    private WeatherApp weatherApp = new WeatherApp();
    private TextView weatherNow_TV;
    private ListView weatherList_LV;
    private GifImageView background;
    private Button updateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }
    public void init(){

        getAccess();

        weatherNow_TV = findViewById(R.id.weatherNow);
        weatherList_LV = findViewById(R.id.listWeather);
        background = findViewById(R.id.background);
        updateBtn = findViewById(R.id.update_btn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }else {
                    showWeather();
                }
            }
        });

        if (checkPermission()) {
            weatherNow_TV.setText("Сервис не доступен");
            updateBtn.setClickable(true);
            updateBtn.setVisibility(View.VISIBLE);
        }
        else
            showWeather();
    }

    private boolean checkPermission(){
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    private void getAccess(){
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false);
                        }
                );

        locationPermissionRequest.launch(new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        });
    }

    private void showWeather(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                weatherApp.getLocation(getApplicationContext());
                weatherApp.getWeather(getApplicationContext());

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if(weatherApp.getWeather() == null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            weatherNow_TV.setText("Сервис не доступен");
                            weatherNow_TV.setTextColor(Color.RED);
                            updateBtn.setClickable(true);
                            updateBtn.setVisibility(View.VISIBLE);
                        }
                    });
                    return;
                }

                if (weatherApp.getWeather().getCurrent() == null || weatherApp.getWeather().getDaily() == null || weatherApp.getAddress() == null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            weatherNow_TV.setText("Сервис не доступен");
                            weatherNow_TV.setTextColor(Color.RED);
                            updateBtn.setClickable(true);
                            updateBtn.setVisibility(View.VISIBLE);
                        }
                    });
                    return;
                }

                updateBtn.setClickable(false);
                updateBtn.setVisibility(View.INVISIBLE);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weatherNow_TV.setText(weatherApp.showWeatherNow());
                        weatherNow_TV.setTextColor(Color.WHITE);
                        ArrayAdapter ad = weatherApp.makeAdapter(getApplicationContext());
                        weatherList_LV.setAdapter(ad);
                        ad.notifyDataSetChanged();
                        background.setImageResource(weatherApp.getWeatherCode());
                    }
                });
            }
        }).start();
    }

    @Override
    public void onStart(){
        super.onStart();
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onStop(){
        super.onStop();
    }

}