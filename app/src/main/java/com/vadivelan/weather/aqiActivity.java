package com.vadivelan.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class aqiActivity extends AppCompatActivity {
JSONObject response,mainData;
JSONArray list;
TextView aqiCondition,pm10,pm25,no,no2,o3,co,so2,nh3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_aqi);
		aqiCondition = findViewById(R.id.aqiCondition);
		pm10 = findViewById(R.id.pmTen);
		pm25 = findViewById(R.id.pmTwoFive);
		no = findViewById(R.id.no);
		no2 = findViewById(R.id.noTwo);
		o3 = findViewById(R.id.oThree);
		co = findViewById(R.id.co);
		so2 = findViewById(R.id.soTwo);
		nh3 = findViewById(R.id.nhThree);
		Intent aqiIntent = getIntent();
		try {
			response = new JSONObject(aqiIntent.getStringExtra("response"));
			list = new JSONArray(response.getString("list"));
			mainData = new JSONObject(list.getJSONObject(0).getString("components"));
			int aqi = list.getJSONObject(0).getJSONObject("main").getInt("aqi");
			pm10.setText(String.format(Locale.ENGLISH, "%.1f", mainData.getDouble("pm10")));
			pm25.setText(String.format(Locale.ENGLISH, "%.1f", mainData.getDouble("pm2_5")));
			no.setText(String.format(Locale.ENGLISH, "%.1f", mainData.getDouble("no")));
			no2.setText(String.format(Locale.ENGLISH, "%.1f", mainData.getDouble("no2")));
			o3.setText(String.format(Locale.ENGLISH, "%.1f", mainData.getDouble("o3")));
			co.setText(String.format(Locale.ENGLISH, "%.1f", mainData.getDouble("co")));
			so2.setText(String.format(Locale.ENGLISH, "%.1f", mainData.getDouble("so2")));
			nh3.setText(String.format(Locale.ENGLISH, "%.1f", mainData.getDouble("nh3")));
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
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}