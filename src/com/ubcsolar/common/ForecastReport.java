package com.ubcsolar.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.dvdme.ForecastIOLib.ForecastIO;

public class ForecastReport extends DataUnit {
	
	private static String classCSVHeaderRow;
	/**
	 * turns the class fields into an entry for a csv file
	 * see returnsEntireTable for info on row versus table
	 * @return the row as a string
	 */
	public String getCSVEntry()
	{
		return null;
	}
	
	/**
	 * gets the column headings as a csv row
	 * @return the row as a string
	 */
	public String getCSVHeaderRow()
	{
		return classCSVHeaderRow;
	}
	
	/**
	 * if the CSV output is multiline rather than a single line
	 * @return 
	 */
	public boolean returnsEntireTable ()
	{
		return false;
	}

	
	private final List<ForecastIO> forecasts;
	private final long timeCreated;
	private final String routeForecastsWereCreatedFor;

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
