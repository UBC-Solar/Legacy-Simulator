package com.ubcsolar.common;

import java.util.Map;

import com.github.dvdme.ForecastIOLib.FIOCurrently;
import com.github.dvdme.ForecastIOLib.FIODataBlock;
import com.github.dvdme.ForecastIOLib.FIODataPoint;
import com.github.dvdme.ForecastIOLib.ForecastIO;

public class SimFrame extends DataUnit {
	private final long timeCreated; //the time this frame was created, not the time it represents. 
	private final long representedTime;
	private final FIODataPoint forecast;
	private final TelemDataPacket carStatus;
	private final LocationReport GPSReport;
	
	
	public SimFrame(FIODataPoint forecast, TelemDataPacket carStatus, LocationReport GPSReport, long timeRepresented) {
		this.forecast = forecast;
		this.carStatus = carStatus;
		this.GPSReport = GPSReport;	
		this.representedTime = timeRepresented;
		this.timeCreated = System.currentTimeMillis();
	}
	public SimFrame(FIODataPoint forecast, TelemDataPacket carStatus, LocationReport GPSReport, long timeRepresented, long timeCreated) {
		this.forecast = forecast;
		this.carStatus = carStatus;
		this.GPSReport = GPSReport;
		this.representedTime = timeRepresented;
		this.timeCreated = timeCreated;	
	}

	@Override
	public double getTimeCreated() {
		return this.timeCreated;
	}

	@Override
	public Map<String, ? extends Object> getAllValues() {
		// TODO Auto-generated method stub
		return null;
	}

	public LocationReport getGPSReport() {
		return GPSReport;
	}

	public TelemDataPacket getCarStatus() {
		return carStatus;
	}

	public FIODataPoint getForecast() {
		return forecast;
	}
	public long getRepresentedTime() {
		return representedTime;
	}

}
