package com.vadivelan.weather.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.location.*;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.vadivelan.weather.MainActivity;

public class Location {
    private static final String LOCATION_TAG = "LocationManagerUtil";
    private static final int LOCATION_UPDATE_INTERVAL = 3600000; // 1 hour
    private final FusedLocationProviderClient locationClient;
    private final MainActivity mainActivity;
    private final ApiManager apiManager;
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            android.location.Location lastLocation = locationResult.getLastLocation();
            if (lastLocation != null) {
                double latitude = lastLocation.getLatitude();
                double longitude = lastLocation.getLongitude();
                Log.i(LOCATION_TAG, "Latitude: " + latitude + ", Longitude: " + longitude);
                apiManager.fetchWeatherAndPollutionData(latitude, longitude);
            } else {
                mainActivity.showSnackbar("Failed to fetch location", BaseTransientBottomBar.LENGTH_LONG);
            }
        }
    };

    public LocationManagerUtil(MainActivity mainActivity, WeatherDataManager weatherDataManager) {
        this.mainActivity = mainActivity;
        this.apiManager = new ApiManager(mainActivity, weatherDataManager);
        this.locationClient = LocationServices.getFusedLocationProviderClient(mainActivity);
    }

    public void startLocationUpdates() {
        if (mainActivity.checkPermissions()) {
            if (isLocationEnabled()) {
                requestNewLocationData();
            } else {
                showLocationError();
            }
        } else {
            Log.d(LOCATION_TAG, "Location permissions not granted");
        }
    }

    private void showLocationError() {
        mainActivity.showSnackbar("Location not enabled",BaseTransientBottomBar.LENGTH_LONG);
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        mainActivity.showSnackbar("Fetching Location...", BaseTransientBottomBar.LENGTH_INDEFINITE);
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,LOCATION_UPDATE_INTERVAL)
                .setMaxUpdates(1).build();

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }
}