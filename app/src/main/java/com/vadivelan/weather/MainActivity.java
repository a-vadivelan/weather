package com.vadivelan.weather;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{
AlertDialog.Builder dialogBuilder;
AlertDialog alert;
TextView location,main_temp,feels_like,main_condition,last_update,humidity,pressure,wind,visibility,sunrise,sunset,clouds,aqi;//min_max,
ImageView weather_icon,windArrow;
JSONArray weather;
static String unit;
String[] units;
Location getLocation;
MainActivity mainActivity;
SharedPreferences sharedPreferences;
SharedPreferences.Editor preferenceEditor;
long tempMin,tempMax;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setMessage("Loading...").setCancelable(false).create();
		alert = dialogBuilder.show();
		location = findViewById(R.id.location);
		last_update = findViewById(R.id.lastUpdate);
		//min_max = findViewById(R.id.minMax);
		main_temp = findViewById(R.id.mainTemp);
		feels_like = findViewById(R.id.feelsLike);
		main_condition = findViewById(R.id.mainCondition);
		humidity = findViewById(R.id.humidity);
		pressure = findViewById(R.id.pressure);
		wind = findViewById(R.id.wind);
		visibility = findViewById(R.id.visibility);
		sunrise = findViewById(R.id.sunrise);
		sunset = findViewById(R.id.sunset);
		clouds = findViewById(R.id.cloud);
		aqi = findViewById(R.id.aqi);
		ImageView refresh_btn = findViewById(R.id.refresh_btn);
		ImageView more_btn = findViewById(R.id.more_btn);
		weather_icon = findViewById(R.id.weatherIcon);
		windArrow = findViewById(R.id.windArrow);
		PopupMenu popup = new PopupMenu(MainActivity.this,more_btn);
		popup.getMenuInflater().inflate(R.menu.menu_items,popup.getMenu());
		this.mainActivity = this;
		sharedPreferences = getSharedPreferences("data",Context.MODE_PRIVATE);
		unit = sharedPreferences.getString("unit","Imperial");
		preferenceEditor = sharedPreferences.edit();
		setUnits(unit);
		getLocation = new Location();
		getLocation.getStart(this);
		popup.setOnMenuItemClickListener(item -> {
			unit = item.getTitle().toString();
			setUnits(unit);
			alert = dialogBuilder.show();
			getLocation = new Location();
			getLocation.getStart(mainActivity);
			alert.dismiss();
			return false;
		});
		refresh_btn.setOnClickListener(view -> {
			alert = dialogBuilder.show();
			getLocation = new Location();
			getLocation.getStart(mainActivity);
			alert.dismiss();
		});
		more_btn.setOnClickListener(view -> popup.show());

	}
	public boolean checkPermissions(){
		if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
			return true;
		} else{
			if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)){
				new AlertDialog.Builder(this)
					.setTitle("Permission")
					.setMessage("You need to give Location permission to app work well")
					.setCancelable(true)
					.setPositiveButton("OK",(dialog,which)->ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1))
					.setNegativeButton("Cancel",(DialogInterface dialog,int which)->dialog.dismiss()).create().show();
			} else{
				Toast.makeText(MainActivity.this,"Please give location permission",Toast.LENGTH_LONG).show();
				Intent openSetting = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				openSetting.setData(Uri.parse("package:"+MainActivity.this.getPackageName()));
				startActivity(openSetting);
			}
		}
		return ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
	}
	public void displayData(JSONObject response){
		try{
			JSONObject weatherData = new JSONObject(response.toString());
			JSONObject main = new JSONObject(weatherData.getString("main"));
			JSONObject windData = new JSONObject(weatherData.getString("wind"));
			JSONObject sysData = new JSONObject(weatherData.getString("sys"));
			JSONObject cloudData = new JSONObject(weatherData.getString("clouds"));
			tempMin = Math.round(main.getDouble("temp_min"));
			tempMax = Math.round(main.getDouble("temp_max"));
			JSONArray weather = new JSONArray(weatherData.getString("weather"));
			DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH);
			DateFormat TimeOnlyFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
			last_update.setText(String.format("Last update: %s",formatter.format(new Date(((long)weatherData.getDouble("dt"))*1000))));
			sunrise.setText(String.format("%s",TimeOnlyFormat.format(new Date(((long)sysData.getDouble("sunrise"))*1000))));
			sunset.setText(String.format("%s",TimeOnlyFormat.format(new Date(((long)sysData.getDouble("sunset"))*1000))));
			location.setText(weatherData.getString("name"));
			//min_max.setText(String.format("Max: %s℉/Min: %s℉", tempMax, tempMin));
			main_temp.setText(String.format("%s%s", Math.round(main.getDouble("temp")),units[0]));
			feels_like.setText(String.format("Feels like: %s%s", Math.round(main.getDouble("feels_like")),units[0]));
			main_condition.setText(String.format("%s", weather.getJSONObject(0).getString("description").substring(0,1).toUpperCase()+weather.getJSONObject(0).getString("description").substring(1)));
			humidity.setText(String.format(Locale.ENGLISH,"%d%%", main.getInt("humidity")));
			pressure.setText(String.format("%s hPa",main.getLong("pressure")));
			wind.setText(String.format("%s %s",windData.getDouble("speed"),units[1]));
			visibility.setText(String.format("%s km",(weatherData.getDouble("visibility"))/1000));
			clouds.setText(String.format(Locale.ENGLISH,"%d%%",cloudData.getInt("all")));
			weather_icon.setImageResource(weather_icon.getContext().getResources().getIdentifier("i"+weather.getJSONObject(0).getString("icon"),"drawable",weather_icon.getContext().getPackageName()));
			windArrow.setRotation(windData.getLong("deg"));
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	public void aqiData(JSONObject response){
		try {
			JSONObject pollutionData = new JSONObject(response.toString());
			JSONArray list = new JSONArray(pollutionData.getString("list"));
			JSONObject mainData = new JSONObject(list.getJSONObject(0).getString("components"));
			int aqiMeasure = (int) mainData.getDouble("pm2_5");
			aqi.setText(String.valueOf(aqiMeasure));
			if(0 <= aqiMeasure && aqiMeasure <= 50)
				aqi.setTextColor(this.getResources().getColor(R.color.excellent));
			else if(51 <= aqiMeasure && aqiMeasure <= 100)
				aqi.setTextColor(this.getResources().getColor(R.color.fair));
			else if(101 <= aqiMeasure && aqiMeasure <= 200)
				aqi.setTextColor(this.getResources().getColor(R.color.poor));
			else if(201 <= aqiMeasure && aqiMeasure <= 300)
				aqi.setTextColor(this.getResources().getColor(R.color.unhealthy));
			else if(301 <= aqiMeasure && aqiMeasure <= 400)
				aqi.setTextColor(this.getResources().getColor(R.color.very_unhealthy));
			else if(401 <= aqiMeasure)
				aqi.setTextColor(this.getResources().getColor(R.color.dangerous));
			aqi.setOnClickListener(view -> {
				Intent aqiActivity = new Intent(MainActivity.this,aqiActivity.class);
				aqiActivity.putExtra("response",response.toString());
				startActivity(aqiActivity);
			});
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	public void setUnits(String unit){
		switch (unit) {
			case "Imperial":
				units = new String[]{"℉", "mi/hr"};
				preferenceEditor.putString("unit","Imperial");
				break;
			case "Metric":
				units = new String[]{"℃", "m/s"};
				preferenceEditor.putString("unit","Metric");
				break;
			case "Standard":
				units = new String[]{"°K", "m/s"};
				preferenceEditor.putString("unit","Standard");
				break;
		}
		Log.d("MainActivity unit var",unit);
		preferenceEditor.apply();
	}
	@Override
	protected void onDestroy() {
		preferenceEditor.commit();
		super.onDestroy();
	}
}