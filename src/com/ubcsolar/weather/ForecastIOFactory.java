package com.ubcsolar.weather;

import com.ubcsolar.common.GeoCoord;

public class ForecastIOFactory {
	private double latitude = 0;
	private double longitude = 0;
	private String timezone = "AMERICA";
	private int offset = 0;
	private String summary = "TEST";
	private String icon = "default";
	private int time = 0;
	private double precipIntensity = 0;
	private double precipProbability = 0;
	private String precipType = "rain";
	private double temperature = 0;
	private double apparentTemperature = 0;
	private double dewPoint = 0;
	private double humidity = 0;
	private double windSpeed = 0;
	private double windBearing = 0;
	private double visibility = 0;
	private double cloudCover = 0;
	private double pressure = 0;
	private double ozone = 0;
	
	public ForecastIOFactory location(GeoCoord location){
		this.latitude = location.getLat();
		this.longitude = location.getLon();
		return this;
	}
	
	public ForecastIOFactory temperature(double temperature){
		this.temperature = temperature;
		this.apparentTemperature = temperature;
		return this;
	}
	
	public ForecastIOFactory cloudCover(double cloudCover){
		this.cloudCover = cloudCover;
		return this;
	}
	
	public ForecastIOFactory dewPoint(double dewPoint){
		this.dewPoint = dewPoint;
		return this;
	}
	
	public ForecastIOFactory humidity(double humidity){
		this.humidity = humidity;
		return this;
	}
	
	public ForecastIOFactory windSpeed(double windSpeed){
		this.windSpeed = windSpeed;
		return this;
	}
	
	public ForecastIOFactory windBearing(double windBearing){
		this.windBearing = windBearing;
		return this;
	}
	
	public ForecastIOFactory precipProb(double precipitationProbability){
		this.precipProbability = precipitationProbability;
		return this;
	}
	
	public ForecastIOFactory precipType(String precipType){
		this.precipType = precipType;
		return this;
	}
	
	
}
