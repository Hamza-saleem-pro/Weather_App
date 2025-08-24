package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private SearchView searchView;
    private ImageView searchIcon;
    private TextView cityName, temp, weather, condition;
    private TextView humidity, pressure, wind, sunrise, sunset, date;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String lastSearchedCity = "Lahore"; // default city
    private static final String API_KEY = "882d6b2ca0fc2bdac09acdc4270c7d4e";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.searchView);
        searchIcon = findViewById(R.id.searchIcon);
        cityName = findViewById(R.id.city_name);
        temp = findViewById(R.id.temp);
        weather = findViewById(R.id.weather);
        condition = findViewById(R.id.condition);
        humidity = findViewById(R.id.humidity);
        pressure = findViewById(R.id.pressure);
        wind = findViewById(R.id.wind_speed);
        sunrise = findViewById(R.id.sunrise);
        sunset = findViewById(R.id.sunset);
        date = findViewById(R.id.date);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh);

        searchIcon.setOnClickListener(v -> {
            String query = searchView.getQuery().toString().trim();
            if (!query.isEmpty()) {
                lastSearchedCity = query;
                fetchWeather(query);
            } else {
                Toast.makeText(MainActivity.this, "Enter a city name", Toast.LENGTH_SHORT).show();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchWeather(lastSearchedCity);
            swipeRefreshLayout.setRefreshing(false);
        });

        fetchWeather(lastSearchedCity);
    }

    private void fetchWeather(String city) {
        Weather_api api = RetrofitClient.getClient().create(Weather_api.class);
        Call<Weather_response> call = api.getWeather(city, API_KEY, "metric");

        call.enqueue(new Callback<Weather_response>() {
            @Override
            public void onResponse(Call<Weather_response> call, Response<Weather_response> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Weather_response data = response.body();

                    cityName.setText(city);

                    temp.setText(Math.round(data.main.temp) + "°C");

                    humidity.setText("Humidity: " + data.main.humidity + "%");
                    pressure.setText("Pressure: " + data.main.pressure + " hPa");
                    wind.setText("Wind: " + data.wind.speed + " m/s");

                    if (data.weather != null && !data.weather.isEmpty()) {
                        String mainCondition = data.weather.get(0).main;
                        String description = data.weather.get(0).description;
                        weather.setText(description);
                        condition.setText(mainCondition);

                        ConstraintLayout mainLayout = findViewById(R.id.main);
                        LottieAnimationView lottieAnimationView = findViewById(R.id.lottieAnimationView);

                        switch (mainCondition) {
                            case "Clear":
                            case "Sunny":
                                mainLayout.setBackgroundResource(R.drawable.sunny_background);
                                lottieAnimationView.setAnimation(R.raw.sun);
                                break;

                            case "Clouds":
                            case "Overcast":
                            case "Mist":
                            case "Foggy":
                                mainLayout.setBackgroundResource(R.drawable.colud_background);
                                lottieAnimationView.setAnimation(R.raw.cloud);
                                break;

                            case "Rain":
                            case "Drizzle":
                            case "Showers":
                                mainLayout.setBackgroundResource(R.drawable.rain_background);
                                lottieAnimationView.setAnimation(R.raw.rain);
                                break;

                            case "Snow":
                            case "Blizzard":
                                mainLayout.setBackgroundResource(R.drawable.snow_background);
                                lottieAnimationView.setAnimation(R.raw.snow);
                                break;

                            default:
                                mainLayout.setBackgroundResource(R.drawable.sunny_background); // fallback
                                lottieAnimationView.setAnimation(R.raw.sun);
                                break;
                        }
                        lottieAnimationView.playAnimation(); // play animation
                    }

                    sunrise.setText("Sunrise: " + formatTime(data.sys.sunrise));
                    sunset.setText("Sunset: " + formatTime(data.sys.sunset));
                    date.setText(formatDate(data.dt));

                } else {
                    Toast.makeText(MainActivity.this, "City not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Weather_response> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatTime(long unixSeconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date(unixSeconds * 1000));
    }

    private String formatDate(long unixSeconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date(unixSeconds * 1000));
    }
}
