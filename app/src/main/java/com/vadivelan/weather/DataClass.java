package com.vadivelan.weather;

public class DataClass {
	private Double speed,temp;
	private String time, condition,icon;
	private Long deg, pressure;
	private Integer humidity;
	public DataClass(String time, String condition, Double speed, Long deg, Integer humidity, Long pressure, String icon, Double temp) {
		this.time = time;
		this.condition = condition.substring(0,1).toUpperCase()+condition.substring(1);
		this.speed = speed;
		this.deg = deg;
		this.humidity = humidity;
		this.pressure = pressure;
		this.icon = icon;
		this.temp = temp;
	}

	public String getTime() {
		return time;
	}

	public Double getSpeed() {
		return speed;
	}

	public Double getTemp() {
		return temp;
	}

	public String getCondition() {
		return condition;
	}

	public String getIcon() {
		return icon;
	}

	public Long getDeg() {
		return deg;
	}

	public Long getPressure() {
		return pressure;
	}

	public Integer getHumidity() {
		return humidity;
	}
}
