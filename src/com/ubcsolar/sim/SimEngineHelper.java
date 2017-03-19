package com.ubcsolar.sim;

import java.util.Map;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.github.dvdme.ForecastIOLib.FIODataPoint;
import com.github.dvdme.ForecastIOLib.ForecastIO;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.SimFrame;

public class SimEngineHelper extends Thread{
	private SimFrame result;
	private GeoCoord currPoint;
	private long currTime;
	private double speed;
	private long prevTime;
	private double timeIncMS;
	private GeoCoord prevPoint;
	private ForecastIO currWeather;
	private double chargeDiff;
	private SimEngine parent;
	private double timeIncHr;
	private FIODataPoint currWeatherPoint;
	
	
	public SimEngineHelper(double speed, GeoCoord prevPoint, GeoCoord currPoint, 
			long currTime, ForecastIO currWeather, SimEngine parent, double timeIncHr){
		
		this.currPoint = currPoint;
		this.prevPoint = prevPoint;
		this.speed = speed;
		this.currWeather = currWeather;
		this.currTime = currTime;
		this.parent = parent;
		this.timeIncHr = timeIncHr;
		
	}
	
	@Override
	public void run() {
		currWeatherPoint = parent.chooseReport(currWeather,currTime);
		JsonObject dailyData = (JsonObject)((JsonArray)currWeather.getDaily().get("data")).get(0);
		long sunriseTime = Long.parseLong(dailyData.get("sunriseTime").toString());
		long sunsetTime = Long.parseLong(dailyData.get("sunsetTime").toString());
		double latitude = currPoint.getLat();
		chargeDiff = parent.calculateChargeDiff(prevPoint, currPoint, 
				currWeatherPoint, speed, timeIncHr, sunriseTime, sunsetTime, latitude, currTime);
		Thread.yield();
	}
	
	public double getChargeDiff(){
		return chargeDiff;
	}
	
	public long getNewTime(){
		return currTime;
	}
	
	public GeoCoord getCurrPoint(){
		return currPoint;
	}
	
	public double getSpeed(){
		return speed;
	}
	
	public FIODataPoint getWeatherPoint(){
		return currWeatherPoint;
	}
	
	

}
