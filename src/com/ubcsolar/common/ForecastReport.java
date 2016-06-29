package com.ubcsolar.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.dvdme.ForecastIOLib.FIODataBlock;
import com.github.dvdme.ForecastIOLib.ForecastIO;

public class ForecastReport extends DataUnit {
	

	public final static String classCSVHeaderRow ="pointNum" + "," + WeatherPrinter.getCSVHeaderRowForForecastIO();
	
	/**
	 * turns the class fields into an entry for a csv file
	 * see returnsEntireTable for info on row versus table
	 * @return the row as a string
	 */
	public String getCSVEntry()
	{
		StringBuilder toReturn =new StringBuilder( "");
		int entryNum = 0;
		double tempDistance=0;
		GeoCoord lastPoint = null;
		double runningTotalDistance = 0;
		
		System.out.println("size of forecast:"+forecasts.size());

			if (forecasts.size() != 0){
				
				for ( ForecastIO forecast : forecasts){
					toReturn.append(entryNum +",");
					toReturn.append(WeatherPrinter.getCSVEntryForForecastIO(forecast));
					toReturn.append("\r\n");
					entryNum++;
				}
			}
			
		return toReturn.toString();
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
	public boolean returnsEntireTable (){
		return true;
	}
	
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
