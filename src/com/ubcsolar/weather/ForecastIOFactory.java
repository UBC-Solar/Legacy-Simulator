package com.ubcsolar.weather;

import com.ubcsolar.Main.GlobalValues;
import com.ubcsolar.common.GeoCoord;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.github.dvdme.ForecastIOLib.ForecastIO;

public class ForecastIOFactory {
	
	//changeable by user
	private double latitude = 0;
	private double longitude = 0;
	private double precipProbability = 0;
	private String precipType = "rain";
	private double temperature = 0;
	private double apparentTemperature = 0;
	private double dewPoint = 0;
	private double humidity = 0;
	private double windSpeed = 0;
	private double windBearing = 0;
	private double cloudCover = 0;
	
	//fixed
	private String timezone = "AMERICA";
	private int offset = -6;
	private String summary = "TEST";
	private String icon = "cloudy";
	private int time = 0;
	private double precipIntensity = 0;
	private double visibility = 0;
	private double pressure = 0;
	private double ozone = 0;
	
	private final int NUM_HOURS_NEEDED = 4;
	
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
	
	public ForecastIO build(){
		JsonObject forecastInfo = new JsonObject();
		forecastInfo.add("latitude", latitude);
		forecastInfo.add("longitude", longitude);
		forecastInfo.add("timezone", timezone);
		forecastInfo.add("offset", offset);
		
		JsonObject hourlyForecast = new JsonObject();
		hourlyForecast.add("summary", summary);
		hourlyForecast.add("icon", icon);
		
		JsonArray dataArray = new JsonArray();
		
		JsonObject dataArrayEntry = new JsonObject();
		dataArrayEntry.add("time", time);
		dataArrayEntry.add("summary", summary);
		dataArrayEntry.add("icon", icon);
		dataArrayEntry.add("precipIntensity", precipIntensity);
		dataArrayEntry.add("precipProbability", precipProbability);
		dataArrayEntry.add("precipType", precipType);
		dataArrayEntry.add("temperature", temperature);
		dataArrayEntry.add("apparentTemperature", apparentTemperature);
		dataArrayEntry.add("dewPoint", dewPoint);
		dataArrayEntry.add("humidity", humidity);
		dataArrayEntry.add("windSpeed", windSpeed);
		dataArrayEntry.add("windBearing", windBearing);
		dataArrayEntry.add("visibility", visibility);
		dataArrayEntry.add("cloudCover", cloudCover);
		dataArrayEntry.add("pressure", pressure);
		dataArrayEntry.add("ozone", ozone);
		
		for(int i = 0; i < NUM_HOURS_NEEDED; i++){
			dataArray.add(dataArrayEntry);
		}
		//TODO: decide whether need to change the time in entries in the dataArray
		
		hourlyForecast.add("data", dataArray);
		
		forecastInfo.add("hourly", hourlyForecast);
		
		ForecastIO forecast = new ForecastIO(GlobalValues.WEATHER_KEY);
		forecast.getForecast(forecastInfo);
		
		return forecast;
		
	}
	
}
