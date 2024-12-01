package com.vadivelan.weather;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.vadivelan.weather.utils.LocationManagerUtil;
import com.vadivelan.weather.utils.PreferenceManager;
import com.vadivelan.weather.utils.WeatherDataManager;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    public static String[] units;
    // long tempMin,tempMax;
//    AlertDialog alert;
    public static Snackbar updating;
    public static String unit;
    public static AlertDialog alert;
    public ImageView weatherIcon, windArrow, refreshBtn, moreBtn;
    public ScrollView scrollView;
    public TextView location, mainTemp, feelsLike, mainCondition, lastUpdate, humidity, pressure, wind, visibility, sunrise, sunset, clouds, aqiCondition, moreDetails;
    public RecyclerView weatherRecyclerView;
    String lat, lon;
    MainActivity mainActivity;
    private PreferenceManager preferenceManager;
    private WeatherDataManager weatherDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupHelpers();
        setupUI();
        checkInitialData();
        alert = new AlertDialog.Builder(this).setMessage("Location service is disabled. Wish you enable it from settings?")
                .setCancelable(true)
                .setPositiveButton("Settings", (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).create();
        PopupMenu popup = new PopupMenu(MainActivity.this, moreBtn);
        popup.getMenuInflater().inflate(R.menu.menu_items, popup.getMenu());
        this.mainActivity = this;

        refreshData();
        popup.setOnMenuItemClickListener(item -> {
            unit = item.getTitle().toString();
            setUnits(unit);
            new LocationManagerUtil(this, weatherDataManager).startLocationUpdates();
            return false;
        });
        refreshBtn.setOnClickListener(view ->
                        new LocationManagerUtil(this, weatherDataManager).startLocationUpdates()
                // For Testing:
                // displayData("{\"coord\":{\"lon\":77.5997,\"lat\":9.4153},\"weather\":[{\"id\":800,\"main\":\"Clouds\",\"description\":\"Clouds\",\"icon\":\"01n\"}],\"base\":\"stations\",\"main\":{\"temp\":23.26,\"feels_like\":23.39,\"temp_min\":23.26,\"temp_max\":23.26,\"pressure\":1017,\"humidity\":67,\"sea_level\":1017,\"grnd_level\":1000},\"visibility\":10000,\"wind\":{\"speed\":1.08,\"deg\":41,\"gust\":4.15},\"clouds\":{\"all\":9},\"dt\":1673281417,\"sys\":{\"country\":\"IN\",\"sunrise\":1673226486,\"sunset\":1673268263},\"timezone\":19800,\"id\":1258916,\"name\":\"Rajapalaiyam\",\"cod\":200}");
        );
        moreBtn.setOnClickListener(view -> popup.show());
        moreDetails.setOnClickListener(view -> {
            try {
                startActivity(new Intent(MainActivity.this, DetailedActivity.class)
                        .putExtra("list", preferenceManager.getString("list", "Loading"))
                        .putExtra("name",
                                new JSONObject(preferenceManager.getString("main", "Loading")).getString("name"))
                        .putExtra("temp", units[0]).putExtra("air", units[1]).putExtra("lat", lat)
                        .putExtra("lon", lon));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private void initializeViews() {
        refreshBtn = findViewById(R.id.refresh_btn);
        moreBtn = findViewById(R.id.more_btn);
        scrollView = findViewById(R.id.background);
        moreDetails = findViewById(R.id.moreDetails);
        lastUpdate = findViewById(R.id.lastUpdate);
        location = findViewById(R.id.location);
        mainTemp = findViewById(R.id.mainTemp);
        feelsLike = findViewById(R.id.feelsLike);
        mainCondition = findViewById(R.id.mainCondition);
        lastUpdate = findViewById(R.id.lastUpdate);
        humidity = findViewById(R.id.humidity);
        pressure = findViewById(R.id.pressure);
        wind = findViewById(R.id.wind);
        visibility = findViewById(R.id.visibility);
        sunrise = findViewById(R.id.sunrise);
        sunset = findViewById(R.id.sunset);
        clouds = findViewById(R.id.cloud);
        aqiCondition = findViewById(R.id.aqiCondition);
        refreshBtn = findViewById(R.id.refresh_btn);
        moreBtn = findViewById(R.id.more_btn);
        weatherIcon = findViewById(R.id.weatherIcon);
        windArrow = findViewById(R.id.windArrow);
        scrollView = findViewById(R.id.background);
        moreDetails = findViewById(R.id.moreDetails);
        weatherRecyclerView = findViewById(R.id.weatherRecyclerView);
        updating = Snackbar.make(scrollView, "Updating...", Snackbar.LENGTH_INDEFINITE);
    }

    private void setupHelpers() {
        preferenceManager = new PreferenceManager(this);
        weatherDataManager = new WeatherDataManager(this, this, preferenceManager);
    }

    private void setupUI() {
        refreshBtn.setOnClickListener(v -> refreshData());
        moreDetails.setOnClickListener(v -> openDetailedActivity());
    }

    private void checkInitialData() {
        unit = preferenceManager.getString("unit", "Metric");
        setUnits(unit);
        if (!(preferenceManager.getString("main", "Loading").equals("Loading")))
            weatherDataManager.displayData(preferenceManager.getString("main", "Loading"));
        if (!(preferenceManager.getString("aqi", "Loading").equals("Loading")))
            weatherDataManager.aqiData(preferenceManager.getString("aqi", "Loading"));
        if (!(preferenceManager.getString("list", "Loading").equals("Loading")))
            weatherDataManager.listData(preferenceManager.getString("list", "Loading"));
    }

    private void refreshData() {
        new LocationManagerUtil(this, weatherDataManager).startLocationUpdates();
    }

    private void openDetailedActivity() {
        startActivity(new Intent(this, DetailedActivity.class).putExtra("data",
                preferenceManager.getString(PreferenceManager.WEATHER_KEY, null)));
    }

    public boolean checkPermissions() {
        // Check if location permissions are granted
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        // If permissions are not granted, show rationale or direct user to settings
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Show an explanation to the user why the permission is needed
            new AlertDialog.Builder(this).setTitle("Permission Required")
                    .setMessage("Location permission is required for the app to function properly.").setCancelable(true)
                    .setPositiveButton("Grant Permission", (dialog, which) ->
                            // Request permission again
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1)
                    ).setNegativeButton("Exit", (dialog, which) ->
                            // Close the app if permission is denied
                            finishAffinity()
                    ).create().show();
        } else {
            // Direct user to app settings if permission is permanently denied
            new AlertDialog.Builder(this).setTitle("Permission Required")
                    .setMessage("Location permission is permanently denied. Please enable it in app settings.")
                    .setCancelable(true).setPositiveButton("Open Settings", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }).setNegativeButton("Exit", (dialog, which) ->
                            // Close the app if permission is denied
                            finishAffinity()
                    ).create().show();
        }

        return ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void setUnits(String unit) {
        switch (unit) {
            case "Imperial":
                units = new String[]{"℉", "mi/hr"};
                preferenceManager.saveString("unit", "Imperial");
                break;
            case "Standard":
                units = new String[]{"°K", "m/s"};
                preferenceManager.saveString("unit", "Standard");
                break;
            default: //Metric unit
                units = new String[]{"℃", "m/s"};
                preferenceManager.saveString("unit", "Metric");
                break;
        }
        Log.d("MainActivity unit var", unit);
    }

    public void showSnackbar(String message, int duration) {
        updating = Snackbar.make(scrollView, message, duration);
        updating.show();
    }
}