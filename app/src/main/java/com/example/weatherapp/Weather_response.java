package com.example.weatherapp;

import java.util.List;

public class Weather_response {
    public Main main;
    public List<Weather> weather;
    public Wind wind;
    public Sys sys;
    public long dt;

    public static class Main {
        public double temp;

        public int humidity;
        public int pressure;
    }

    public static class Weather {
        public String main;
        public String description;
    }

    public static class Wind {
        public double speed;
    }

    public static class Sys {
        public long sunrise;
        public long sunset;
    }
}
