package com.ubcsolar.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.dvdme.ForecastIOLib.ForecastIO;

public class ForecastReport extends DataUnit {
	
	private final List<ForecastIO> forecasts;
	private final long timeCreated;
	private final String routeForecastsWereCreatedFor;

	/**
	 * 
	 * @param forecasts
	 * @param routeName the name of the route, or NULL if it was deleted.
	 * @param timeCreated
	 */
	public ForecastReport(List<ForecastIO> forecasts, String routeName, long timeCreated) {
		this.forecasts = new ArrayList<ForecastIO>(forecasts);
		this.timeCreated = timeCreated;
		this.routeForecastsWereCreatedFor = routeName;
	}
	
	public ForecastReport(List<ForecastIO> forecasts, String routeName) {
		this.forecasts = new ArrayList<ForecastIO>(forecasts);
		this.timeCreated = System.currentTimeMillis();
		this.routeForecastsWereCreatedFor = routeName;
	}


	@Override
	public double getTimeCreated() {	
		return timeCreated;
	}

	@Override
	public Map<String, ? extends Object> getAllValues() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRouteNameForecastsWereCreatedFor() {
		return routeForecastsWereCreatedFor;
	}

	public List<ForecastIO> getForecasts() {
		return forecasts;
	}

}
