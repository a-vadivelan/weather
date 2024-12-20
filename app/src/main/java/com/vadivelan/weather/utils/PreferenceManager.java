package com.vadivelan.weather.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static final String data = "data";
    public static final String WEATHER_KEY = "weather_data";

    private final SharedPreferences sharedPreferences;

    public PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(data, Context.MODE_PRIVATE);
    }

    public void saveString(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }
}