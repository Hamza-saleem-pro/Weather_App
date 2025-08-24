package com.example.weatherapp;

import com.example.weatherapp.Weather_response;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Weather_api {
    @GET("data/2.5/weather")
    Call<Weather_response> getWeather(
            @Query("q") String city,
            @Query("appid") String apiKey,
            @Query("units") String units
    );
}
