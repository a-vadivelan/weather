package com.vadivelan.weather;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailedActivity extends AppCompatActivity {
	List<DataClass> dataList;
	DateFormat dateAndTime = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ENGLISH);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detailed);
		RecyclerView detailed = findViewById(R.id.detailed);
		TextView location = findViewById(R.id.location);
		if(getIntent().getStringExtra("name").equals(""))
			location.setText(String.format("%s, %s",getIntent().getStringExtra("lat"),getIntent().getStringExtra("lon")));
		else
			location.setText(getIntent().getStringExtra("name"));
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
		detailed.setLayoutManager(linearLayoutManager);
		try {
				dataList = new ArrayList<>();
				JSONArray weatherData,detailedData = new JSONArray(new JSONObject(getIntent().getStringExtra("list")).getString("list"));
				JSONObject mainData,windData;
				for(int i = 0;i<40;i++) {
					mainData = new JSONObject(detailedData.getJSONObject(i).getString("main"));
					weatherData = new JSONArray(detailedData.getJSONObject(i).getString("weather"));
					windData = new JSONObject(detailedData.getJSONObject(i).getString("wind"));
					dataList.add(new DataClass(dateAndTime.format(new Date(((long)detailedData.getJSONObject(i).getDouble("dt"))*1000)),weatherData.getJSONObject(0).getString("description"),windData.getDouble("speed"),windData.getLong("deg"),mainData.getInt("humidity"),mainData.getLong("pressure"),weatherData.getJSONObject(0).getString("icon"),mainData.getDouble("temp")));
				}
				detailed.setAdapter(new DetailedAdapter(dataList,getIntent().getStringExtra("temp"), getIntent().getStringExtra("air")));

			} catch(Exception e){
				e.printStackTrace();
			}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
