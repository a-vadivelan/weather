package com.vadivelan.weather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.vadivelan.weather.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import static com.vadivelan.weather.MainActivity.units;
import static com.vadivelan.weather.utils.WeatherDataManager.timeOnlyFormat;

public class SmallWeatherAdapter extends RecyclerView.Adapter<SmallWeatherAdapter.WeatherViewHolder> {
    private JSONArray weatherData;
    private Context context;

    public SmallWeatherAdapter(Context context, JSONArray weatherData) {
        this.context = context;
        this.weatherData = weatherData;
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.small_detail_layout, parent, false);
        return new WeatherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        try {
            String time =
                    String.format("%s",
                            timeOnlyFormat.format(new Date(((long) weatherData.getJSONObject(position).getDouble("dt")) * 1000)));
            String temp =
            String.format("%s%s",
                    Math.round(new JSONObject(weatherData.getJSONObject(position).getString("main")).getDouble("temp")),
                    units[0]);
            holder.timeTextView.setText(time);
            holder.tempTextView.setText(temp);
            holder.iconImageView
                    .setImageResource(context.getResources().getIdentifier(
                            "i" + new JSONArray(weatherData.getJSONObject(position).getString("weather")).getJSONObject(0)
                                    .getString("icon"),
                            "drawable",context.getPackageName()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherData.length();
    }

    static class WeatherViewHolder extends RecyclerView.ViewHolder {
        TextView timeTextView;
        ImageView iconImageView;
        TextView tempTextView;

        WeatherViewHolder(View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.time);
            iconImageView = itemView.findViewById(R.id.icon);
            tempTextView = itemView.findViewById(R.id.temp);
        }
    }
}