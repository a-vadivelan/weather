package com.vadivelan.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.LocationManager;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class Location{
	FusedLocationProviderClient fusedLocationProviderClient;
	MainActivity mainActivity;
	RequestParams rp;
	JsonHttpResponseHandler jsonHttpResponseHandler;
	public static double latitude,longitude;

	public void getStart(MainActivity  mainActivity){
		Log.d("Location","Get Start");
		this.mainActivity = mainActivity;
		fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mainActivity);
		getLastLocation();
	}
	@SuppressLint("MissingPermission")
	public void getLastLocation(){
		Log.d("Location","Get Last Location");
		if(mainActivity.checkPermissions()){
			Log.d("Location","Location permission provided");
			if(isLocationEnabled()){
				Log.d("Location","Location enabled");
				mainActivity.updating.show();
				requestNewLocationData();
			} else {
				mainActivity.alert.show();
				Toast.makeText(mainActivity,"After enabling Location service, press Refresh button to work",Toast.LENGTH_LONG).show();
			}
		} else {
			Log.d("Location","Location permission not provided");
		}
	}
	@SuppressLint("MissingPermission")
	private void requestNewLocationData(){
		Log.d("Location","Request new location data");
		LocationRequest locationRequest = LocationRequest.create();
		locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY).setInterval(5).setFastestInterval(0).setNumUpdates(1);
		fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mainActivity);
		fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
		Log.d("Location","New location requested");
	}
	private final LocationCallback locationCallback = new LocationCallback(){
		@Override
		public void onLocationResult(@NonNull LocationResult locationResult) {
			android.location.Location lastLocation = locationResult.getLastLocation();
			Log.d("Location","OnLocationResult");
			assert lastLocation != null;
			Log.i("Testing","Latitude: "+lastLocation.getLatitude()+" Longitude: "+lastLocation.getLongitude());
			latitude = lastLocation.getLatitude();
			longitude = lastLocation.getLongitude();
			getData();
		}
	};
	private boolean isLocationEnabled(){
		LocationManager locationManager = (LocationManager) mainActivity.getSystemService(Context.LOCATION_SERVICE);
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}
	public void getData(){
		Log.d("Location", "location != null");
		Log.i("Testing Latitude", latitude + "");
		Log.i("Testing Longitude", longitude + "");
		String baseUrl = "http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&units=" + MainActivity.unit + "&appid=4bf8e2f9b6b7f274a0597dc337a62449";
		Log.d("Location unit var",MainActivity.unit);
		String pollutionUrl = "http://api.openweathermap.org/data/2.5/air_pollution?lat=" + latitude + "&lon=" + longitude + "&appid=4bf8e2f9b6b7f274a0597dc337a62449";
		String listUrl = "http://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longitude + "&units=" + MainActivity.unit + "&appid=4bf8e2f9b6b7f274a0597dc337a62449";
		Toast.makeText(mainActivity, String.format("Latitude: %s\nLongitude: %s", latitude, longitude), Toast.LENGTH_LONG).show();
		rp = new RequestParams();
		jsonHttpResponseHandler = new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				try {
				JSONObject serverResponse = new JSONObject(response.toString());
				if(serverResponse.has("weather"))
					mainActivity.displayData(response.toString());
				if(serverResponse.has("city"))
					mainActivity.listData(response.toString());
				else
					mainActivity.aqiData(response.toString());
				} catch (Exception e){
					e.printStackTrace();
				}
				Log.d("Testing", "Response:\n" + response.toString());
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
				super.onFailure(statusCode, headers, throwable, errorResponse);
				Log.d("Testing", "ErrorResponse:\n" + errorResponse);
				Toast.makeText(mainActivity, "Check internet connection", Toast.LENGTH_LONG).show();
			}
		};
		HttpUtils.getByUrl(baseUrl, rp, jsonHttpResponseHandler);
		HttpUtils.getByUrl(pollutionUrl, rp, jsonHttpResponseHandler);
		HttpUtils.getByUrl(listUrl, rp, jsonHttpResponseHandler);
		mainActivity.updating.dismiss();
	}
}
