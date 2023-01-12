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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{
TextView location,main_temp,feels_like,main_condition,last_update,humidity,pressure,wind,visibility,sunrise,sunset,clouds,aqiCondition,moreDetails;//min_max;
ImageView weather_icon,windArrow,refresh_btn,more_btn;
static String unit;
String[] units;
String lat,lon;
MainActivity mainActivity;
SharedPreferences sharedPreferences;
SharedPreferences.Editor preferenceEditor;
//long tempMin,tempMax;
ScrollView scrollView;
Snackbar updating;
AlertDialog alert;
DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH);
DateFormat TimeOnlyFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
		aqiCondition = findViewById(R.id.aqi);
		refresh_btn = findViewById(R.id.refresh_btn);
		more_btn = findViewById(R.id.more_btn);
		weather_icon = findViewById(R.id.weatherIcon);
		windArrow = findViewById(R.id.windArrow);
		scrollView = findViewById(R.id.background);
		moreDetails = findViewById(R.id.moreDetails);
		alert = new AlertDialog.Builder(this).setMessage("Location service is disabled. Wish you enable it from settings?")
				.setCancelable(true)
				.setPositiveButton("Settings",(dialog, which)-> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
				.setNegativeButton("Cancel",(dialog,which)->dialog.dismiss()).create();
		PopupMenu popup = new PopupMenu(MainActivity.this,more_btn);
		popup.getMenuInflater().inflate(R.menu.menu_items,popup.getMenu());
		this.mainActivity = this;
		updating = Snackbar.make(scrollView,"Updating...",Snackbar.LENGTH_INDEFINITE);
		sharedPreferences = getSharedPreferences("data",Context.MODE_PRIVATE);
		unit = sharedPreferences.getString("unit","Imperial");
		preferenceEditor = sharedPreferences.edit();
		setUnits(unit);
		if(!(sharedPreferences.getString("main","Loading").equals("Loading")))
			displayData(sharedPreferences.getString("main","Loading"));
		if(!(sharedPreferences.getString("aqi","Loading").equals("Loading")))
			aqiData(sharedPreferences.getString("aqi","Loading"));
		if(!(sharedPreferences.getString("list","Loading").equals("Loading")))
			listData(sharedPreferences.getString("list","Loading"));
		new Location().getStart(this);
		popup.setOnMenuItemClickListener(item -> {
			unit = item.getTitle().toString();
			setUnits(unit);
			new Location().getStart(mainActivity);
			return false;
		});
		refresh_btn.setOnClickListener(view -> {
			new Location().getStart(mainActivity);
			//For Testing: displayData("{\"coord\":{\"lon\":77.5997,\"lat\":9.4153},\"weather\":[{\"id\":800,\"main\":\"Clouds\",\"description\":\"Clouds\",\"icon\":\"01n\"}],\"base\":\"stations\",\"main\":{\"temp\":23.26,\"feels_like\":23.39,\"temp_min\":23.26,\"temp_max\":23.26,\"pressure\":1017,\"humidity\":67,\"sea_level\":1017,\"grnd_level\":1000},\"visibility\":10000,\"wind\":{\"speed\":1.08,\"deg\":41,\"gust\":4.15},\"clouds\":{\"all\":9},\"dt\":1673281417,\"sys\":{\"country\":\"IN\",\"sunrise\":1673226486,\"sunset\":1673268263},\"timezone\":19800,\"id\":1258916,\"name\":\"Rajapalaiyam\",\"cod\":200}");
		});
		more_btn.setOnClickListener(view -> popup.show());
		moreDetails.setOnClickListener(view ->{
			try{
				startActivity(new Intent(MainActivity.this,DetailedActivity.class).putExtra("list",sharedPreferences.getString("list","Loading")).putExtra("name",new JSONObject(sharedPreferences.getString("main","Loading")).getString("name")).putExtra("temp",units[0]).putExtra("air",units[1]).putExtra("lat",lat).putExtra("lon",lon));
			} catch (Exception e){
				e.printStackTrace();
			}
		});

	}
	public boolean checkPermissions(){
		if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
			return true;
		} else{
			if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)){
				new AlertDialog.Builder(this)
					.setTitle("Permission")
					.setMessage("You need to give Location permission to app work properly.")
					.setCancelable(true)
					.setPositiveButton("OK",(dialog,which)-> {
						ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
						Toast.makeText(this,"After granting location permission, press Refresh button to work",Toast.LENGTH_LONG).show();
					})
					.setNegativeButton("Exit",(DialogInterface dialog,int which)-> {
						finishAffinity();
						System.exit(0);
					}).create().show();
			} else{
				new AlertDialog.Builder(this)
					.setTitle("Permission")
					.setMessage("You need to give Location permission to app work properly.")
					.setCancelable(true)
					.setPositiveButton("OK",(dialog,which)-> {
						startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + MainActivity.this.getPackageName())));
						Toast.makeText(this,"After granting location permission, press Refresh button to work",Toast.LENGTH_LONG).show();
					})
					.setNegativeButton("Exit",(DialogInterface dialog,int which)-> {
						finishAffinity();
						System.exit(0);
					}).create().show();
			}
		}
		return ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
	}
	public void displayData(String response){
		try{
			preferenceEditor.putString("main",response);
			preferenceEditor.apply();
			JSONObject weatherData = new JSONObject(response);
			JSONObject main = new JSONObject(weatherData.getString("main"));
			JSONObject windData = new JSONObject(weatherData.getString("wind"));
			JSONObject sysData = new JSONObject(weatherData.getString("sys"));
			JSONObject cloudData = new JSONObject(weatherData.getString("clouds"));
			/*tempMin = Math.round(main.getDouble("temp_min"));
			tempMax = Math.round(main.getDouble("temp_max"));*/
			JSONArray weather = new JSONArray(weatherData.getString("weather"));
			last_update.setText(String.format("Last update: %s",formatter.format(new Date(((long)weatherData.getDouble("dt"))*1000))));
			sunrise.setText(String.format("%s",TimeOnlyFormat.format(new Date(((long)sysData.getDouble("sunrise"))*1000))));
			sunset.setText(String.format("%s",TimeOnlyFormat.format(new Date(((long)sysData.getDouble("sunset"))*1000))));
			lat = ""+new JSONObject(weatherData.getString("coord")).getDouble("lat");
			lon = ""+new JSONObject(weatherData.getString("coord")).getDouble("lon");
			if(weatherData.getString("name").equals(""))
				location.setText(String.format("%s, %s",lat,lon));
			else
				location.setText(weatherData.getString("name"));
			//min_max.setText(String.format("Max: %s%s/Min: %s%s", tempMax, units[0],tempMin,units[0]));
			main_temp.setText(String.format("%s%s", Math.round(main.getDouble("temp")),units[0]));
			feels_like.setText(String.format("Feels like: %s%s", Math.round(main.getDouble("feels_like")),units[0]));
			main_condition.setText(String.format("%s", weather.getJSONObject(0).getString("description").substring(0,1).toUpperCase()+weather.getJSONObject(0).getString("description").substring(1)));
			humidity.setText(String.format(Locale.ENGLISH,"%d%%", main.getInt("humidity")));
			pressure.setText(String.format("%s hPa",main.getLong("pressure")));
			wind.setText(String.format("%s %s",windData.getDouble("speed"),units[1]));
			visibility.setText(String.format("%s km",(weatherData.getDouble("visibility"))/1000));
			clouds.setText(String.format(Locale.ENGLISH,"%d%%",cloudData.getInt("all")));
			weather_icon.setImageResource(weather_icon.getContext().getResources().getIdentifier("i"+weather.getJSONObject(0).getString("icon"),"drawable",weather_icon.getContext().getPackageName()));
			windArrow.setRotation(windData.getLong("deg")+180);
			if(weather.getJSONObject(0).getString("main").contains("Thunderstorm"))
				scrollView.setBackgroundResource(R.drawable.thunderstorm);
			else if(weather.getJSONObject(0).getString("main").contains("Drizzle"))
				scrollView.setBackgroundResource(R.drawable.drizzle);
			else if(weather.getJSONObject(0).getString("main").contains("Rain"))
				scrollView.setBackgroundResource(R.drawable.rain);
			else if(weather.getJSONObject(0).getString("main").contains("Snow"))
				scrollView.setBackgroundResource(R.drawable.snow);
			else if(weather.getJSONObject(0).getString("main").contains("Clear"))
				scrollView.setBackgroundResource(R.drawable.clear);
			else if(weather.getJSONObject(0).getString("main").contains("Clouds"))
				scrollView.setBackgroundResource(R.drawable.clouds);
			else
				scrollView.setBackgroundResource(R.drawable.atmosphere);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	public void aqiData(String response){
		try {
			preferenceEditor.putString("aqi",response);
			preferenceEditor.apply();
			JSONObject pollutionData = new JSONObject(response);
			JSONArray list = new JSONArray(pollutionData.getString("list"));
			int aqi = list.getJSONObject(0).getJSONObject("main").getInt("aqi");
			if (aqi == 1) {
				aqiCondition.setText(R.string.good);
				aqiCondition.setTextColor(this.getResources().getColor(R.color.good));
			} else if (aqi == 2) {
				aqiCondition.setText(R.string.fair);
				aqiCondition.setTextColor(this.getResources().getColor(R.color.fair));
			} else if (aqi == 3) {
				aqiCondition.setText(R.string.moderate);
				aqiCondition.setTextColor(this.getResources().getColor(R.color.moderate));
			} else if (aqi == 4) {
				aqiCondition.setText(R.string.poor);
				aqiCondition.setTextColor(this.getResources().getColor(R.color.poor));
			} else if (aqi == 5) {
				aqiCondition.setText(R.string.very_poor);
				aqiCondition.setTextColor(this.getResources().getColor(R.color.very_poor));
			}
			aqiCondition.setOnClickListener(view -> {
				Intent aqiActivity = new Intent(MainActivity.this,aqiActivity.class);
				aqiActivity.putExtra("response",response);
				startActivity(aqiActivity);
			});
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	public void listData(String response){
		try{
			preferenceEditor.putString("list",response);
			preferenceEditor.apply();
			JSONObject listData = new JSONObject(response);
			JSONArray list = new JSONArray(listData.getString("list"));
			int[] times_id = {R.id.time1,R.id.time2,R.id.time3,R.id.time4,R.id.time5,R.id.time6,R.id.time7,R.id.time8};
			int[] icons_id = {R.id.icon1,R.id.icon2,R.id.icon3,R.id.icon4,R.id.icon5,R.id.icon6,R.id.icon7,R.id.icon8};
			int[] temp_id = {R.id.temp1,R.id.temp2,R.id.temp3,R.id.temp4,R.id.temp5,R.id.temp6,R.id.temp7,R.id.temp8};
			for(int i=0;i<8;i++){
				((TextView) findViewById(times_id[i])).setText(String.format("%s",TimeOnlyFormat.format(new Date(((long)list.getJSONObject(i).getDouble("dt"))*1000))));
				((TextView) findViewById(temp_id[i])).setText(String.format("%s%s", Math.round(new JSONObject(list.getJSONObject(i).getString("main")).getDouble("temp")),units[0]));
				((ImageView) findViewById(icons_id[i])).setImageResource(findViewById(icons_id[i]).getContext().getResources().getIdentifier("i"+new JSONArray(list.getJSONObject(i).getString("weather")).getJSONObject(0).getString("icon"),"drawable",findViewById(icons_id[i]).getContext().getPackageName()));
			}
			updating.dismiss();
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