package com.ubcsolar.sim;

import java.util.Map;

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
//		double distance = prevPoint.calculateDistance(currPoint);
//		double timeIncHr = distance/speed;
//		double timeIncMS = timeIncHr * 3600000; 
//		currTime = prevTime + timeIncMS;
		currWeatherPoint = parent.chooseReport(currWeather,currTime);
		chargeDiff = parent.calculateChargeDiff(prevPoint, currPoint, 
				currWeatherPoint, speed, timeIncHr);
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
