package com.vadivelan.weather.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.vadivelan.weather.MainActivity;
import com.vadivelan.weather.R;
import com.vadivelan.weather.adapter.SmallWeatherAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static androidx.core.content.ContextCompat.startActivity;
import static com.vadivelan.weather.MainActivity.units;
import static com.vadivelan.weather.MainActivity.updating;

public class WeatherDataManager {
    private final Context context;
    private final PreferenceManager preferenceManager;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH);
    private final MainActivity mainActivity;
    DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH);
    public final static DateFormat timeOnlyFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

    final Map<String, Integer> weatherBackgroundMap = new HashMap<>();


    public WeatherDataManager(Context context, MainActivity mainActivity,PreferenceManager preferenceManager) {
        this.context = context;
        this.preferenceManager = preferenceManager;
        this.mainActivity = mainActivity;
        weatherBackgroundMap.put("Thunderstorm",R.drawable.thunderstorm);
        weatherBackgroundMap.put("Drizzle",R.drawable.drizzle);
        weatherBackgroundMap.put("Rain",R.drawable.rain);
        weatherBackgroundMap.put("Snow",R.drawable.snow);
        weatherBackgroundMap.put("Clear",R.drawable.clear);
        weatherBackgroundMap.put("Clouds",R.drawable.clouds);
    }

    public void displayData(String response) {
        try {
            preferenceManager.saveString("main", response);
            JSONObject weatherData = new JSONObject(response);
            Log.d("Weather",weatherData.toString());
            JSONObject main = new JSONObject(weatherData.getString("main"));
            JSONObject windData = new JSONObject(weatherData.getString("wind"));
            JSONObject sysData = new JSONObject(weatherData.getString("sys"));
            JSONObject cloudData = new JSONObject(weatherData.getString("clouds"));
            /*
             * tempMin = Math.round(main.getDouble("temp_min")); tempMax =
             * Math.round(main.getDouble("temp_max"));
             */
            JSONArray weather = new JSONArray(weatherData.getString("weather"));
            String lastUpdate = String.format("Last update: %s",
                    formatter.format(new Date(((long) weatherData.getDouble("dt")) * 1000)));
            Log.d("lastUpdate", lastUpdate);
            mainActivity.lastUpdate.setText(lastUpdate);

            mainActivity.sunrise.setText(
                    String.format("%s", timeOnlyFormat.format(new Date(((long) sysData.getDouble("sunrise")) * 1000))));
            mainActivity.sunset.setText(
                    String.format("%s", timeOnlyFormat.format(new Date(((long) sysData.getDouble("sunset")) * 1000))));
            String lat = "" + new JSONObject(weatherData.getString("coord")).getDouble("lat");
            String lon = "" + new JSONObject(weatherData.getString("coord")).getDouble("lon");
            if (weatherData.getString("name").isEmpty())
                mainActivity.location.setText(String.format("%s, %s", lat, lon));
            else
                mainActivity.location.setText(weatherData.getString("name"));
            // min_max.setText(String.format("Max: %s%s/Min: %s%s", tempMax,
            // units[0],tempMin,units[0]));
            mainActivity.mainTemp.setText(String.format("%s%s", Math.round(main.getDouble("temp")), units[0]));
            mainActivity.feelsLike.setText(String.format("Feels like: %s%s", Math.round(main.getDouble("feels_like")), units[0]));
            mainActivity.mainCondition.setText(
                    String.format("%s", weather.getJSONObject(0).getString("description").substring(0, 1).toUpperCase()
                            + weather.getJSONObject(0).getString("description").substring(1)));
            mainActivity.humidity.setText(String.format(Locale.ENGLISH, "%d%%", main.getInt("humidity")));
            mainActivity.pressure.setText(String.format("%s hPa", main.getLong("pressure")));
            mainActivity.wind.setText(String.format("%s %s", windData.getDouble("speed"), units[1]));
            mainActivity.visibility.setText(String.format("%s km", (weatherData.getDouble("visibility")) / 1000));
            mainActivity.clouds.setText(String.format(Locale.ENGLISH, "%d%%", cloudData.getInt("all")));
            mainActivity.weatherIcon.setImageResource(context.getResources().getIdentifier(
                    "i" + weather.getJSONObject(0).getString("icon"), "drawable",
                    context.getPackageName()));
            mainActivity.windArrow.setRotation(windData.getLong("deg") + 180);
            String mainWeather = weather.getJSONObject(0).getString("main");
            Log.i("Main Weather", mainWeather);

            Integer backgroundResource = null;
            backgroundResource = weatherBackgroundMap.getOrDefault(mainWeather, R.drawable.atmosphere);
            mainActivity.scrollView.setBackgroundResource(backgroundResource);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void aqiData(String response) {
        try {
            preferenceManager.saveString("aqi", response);
            JSONObject pollutionData = new JSONObject(response);
            JSONArray list = new JSONArray(pollutionData.getString("list"));
            int aqi = list.getJSONObject(0).getJSONObject("main").getInt("aqi");

            int aqiTextResId = R.string.good;
            int aqiColorResId = R.string.good;
            switch (aqi) {
                case 1:
                    aqiTextResId = R.string.good;
                    aqiColorResId = R.color.good;
                    break;
                case 2:
                    aqiTextResId = R.string.fair;
                    aqiColorResId = R.color.fair;
                    break;
                case 3:
                    aqiTextResId = R.string.moderate;
                    aqiColorResId = R.color.moderate;
                    break;
                case 4:
                    aqiTextResId = R.string.poor;
                    aqiColorResId = R.color.poor;
                    break;
                case 5:
                    aqiTextResId = R.string.very_poor;
                    aqiColorResId = R.color.very_poor;
                    break;
            }

            mainActivity.aqiCondition.setText(aqiTextResId);
            mainActivity.aqiCondition.setTextColor(ContextCompat.getColor(context, aqiColorResId));

            mainActivity.aqiCondition.setOnClickListener(view -> {
                Intent aqiActivity = new Intent(context, com.vadivelan.weather.aqiActivity.class);
                aqiActivity.putExtra("response", response);
                startActivity(context, aqiActivity, null);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listData(String response) {
        try {
            preferenceManager.saveString("list", response);
            JSONObject listData = new JSONObject(response);
            JSONArray list = new JSONArray(listData.getString("list"));
            JSONArray firstEightElements = new JSONArray();
            int limit = Math.min(8, list.length());
            for (int i = 0; i < limit; i++) {
                firstEightElements.put(list.get(i));
            }
            mainActivity.weatherRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            mainActivity.weatherRecyclerView.setAdapter(new SmallWeatherAdapter(context, firstEightElements));
            updating.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}