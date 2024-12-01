package com.vadivelan.weather.utils;

import android.util.Log;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.vadivelan.weather.MainActivity;
import cz.msebera.android.httpclient.Header;
import org.json.JSONObject;

public class ApiManager {
    private static final String API_KEY = "4bf8e2f9b6b7f274a0597dc337a62449";
    private double latitude;
    private double longitude;
    private final MainActivity mainActivity;
    private final WeatherDataManager weatherDataManager;
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private int remainingRequests = 0;
    private final Object lock = new Object(); // Ensures thread safety

    public ApiManager(MainActivity mainActivity, WeatherDataManager weatherDataManager) {
        this.mainActivity = mainActivity;
        this.weatherDataManager = weatherDataManager;
    }

    private final JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            try {
                handleApiResponse(response);
                updateRequestStatus(true);
            } catch (Exception e) {
                Log.e("ApiManager", "Error processing response", e);
                mainActivity.showSnackbar("Error occurred", BaseTransientBottomBar.LENGTH_SHORT);
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.e("ApiManager", "API request failed", throwable);
            updateRequestStatus(false);
        }

        private void handleApiResponse(JSONObject response) {
            if (response.has("weather")) {
                weatherDataManager.displayData(response.toString());
            } else if (response.has("city")) {
                weatherDataManager.listData(response.toString());
            } else {
                weatherDataManager.aqiData(response.toString());
            }
        }

        private void updateRequestStatus(boolean success) {
            synchronized (lock) {
                if (success) {
                    remainingRequests--;
                }
            }

            if (remainingRequests == 0) {
                mainActivity.showSnackbar(success ? "Updated!" : "Check Internet connection",
                        success ? BaseTransientBottomBar.LENGTH_SHORT : BaseTransientBottomBar.LENGTH_LONG);
            }
        }
    };

    public void fetchWeatherAndPollutionData(double newLatitude, double newLongitude) {
        latitude = newLatitude;
        longitude = newLongitude;

        mainActivity.showSnackbar("Updating...", BaseTransientBottomBar.LENGTH_INDEFINITE);

        String unit = MainActivity.unit;
        String[] endpoints = { "weather", "air_pollution", "forecast" };
        RequestParams requestParams = new RequestParams();

        synchronized (lock) {
            remainingRequests = endpoints.length;
        }

        for (String endpoint : endpoints) {
            HttpUtils.getByUrl(buildUrl(endpoint, unit), requestParams, jsonHttpResponseHandler);
        }
    }

    private String buildUrl(String endpoint, String unit) {
        StringBuilder urlBuilder = new StringBuilder(BASE_URL).append(endpoint).append("?lat=").append(latitude)
                .append("&lon=").append(longitude).append("&appid=").append(API_KEY);

        if (unit != null && !endpoint.equals("air_pollution")) {
            urlBuilder.append("&units=").append(unit);
        }
        return urlBuilder.toString();
    }
}