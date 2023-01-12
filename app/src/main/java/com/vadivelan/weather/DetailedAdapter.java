package com.vadivelan.weather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class DetailedAdapter extends RecyclerView.Adapter<DetailedAdapter.ViewHolder> {
	List<DataClass> dataClassList;
	String tempUnit,windUnit;
	public DetailedAdapter(List<DataClass> list,String tempUnit,String windUnit){
		this.dataClassList = list;
		this.tempUnit = tempUnit;
		this.windUnit = windUnit;
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.detailed_layout,viewGroup,false);
		return new ViewHolder(v,tempUnit,windUnit);
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
		viewHolder.setData(dataClassList.get(i).getTime(),dataClassList.get(i).getCondition(),dataClassList.get(i).getSpeed(),dataClassList.get(i).getDeg(),dataClassList.get(i).getHumidity(),dataClassList.get(i).getPressure(),dataClassList.get(i).getIcon(),dataClassList.get(i).getTemp());
	}

	@Override
	public int getItemCount() {
		return dataClassList.size();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder{
		View view;
		String tempUnit,windUnit;
		public ViewHolder(@NonNull View itemView,String tempUnit,String windUnit) {
			super(itemView);
			this.view = itemView;
			this.tempUnit = tempUnit;
			this.windUnit = windUnit;
		}

		public void setData(String time, String condition, Double speed, Long deg, Integer humidity, Long pressure, String icon, Double temp){
			((TextView)view.findViewById(R.id.time)).setText(time);
			((TextView)view.findViewById(R.id.mainCondition)).setText(condition);
			((TextView)view.findViewById(R.id.wind)).setText(String.format("Wind: %s %s",speed,windUnit));
			view.findViewById(R.id.windArrow).setRotation(deg+180);
			((TextView)view.findViewById(R.id.humidity)).setText(String.format(Locale.ENGLISH,"Humidity: %d%%",humidity));
			((TextView)view.findViewById(R.id.pressure)).setText(String.format(Locale.ENGLISH,"Pressure: %d hPa",pressure));
			((TextView)view.findViewById(R.id.mainTemp)).setText(String.format(Locale.ENGLISH,"%d%s",Math.round(temp),tempUnit));
			((ImageView) view.findViewById(R.id.icon)).setImageResource(view.findViewById(R.id.icon).getContext().getResources().getIdentifier("i"+icon,"drawable",view.findViewById(R.id.icon).getContext().getPackageName()));
		}
	}
}
