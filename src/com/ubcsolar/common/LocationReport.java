package com.ubcsolar.common;

import java.util.Map;

public class LocationReport extends DataUnit {
	
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


	private final double timeCreated;
	private final GeoCoord location;
	private final String carName;
	private final String source;
	
	/**
	 * 
	 * @param location - the GPS coordinates reported
	 * @param carName - the car name
	 * @param source - where they came from (phone? Manually entered? Telemetry data?)
	 * @param timeCreated - time of report
	 */
	public LocationReport(GeoCoord location, String carName, String source, double timeCreated) {
		this.timeCreated = timeCreated;
		this.location = location;
		this.carName = carName;
		this.source = source;
				
	}
	
	/**
	 * 
	 * @param location - the GPS coordinates reported
	 * @param carName - the car name
	 * @param source - where they came from (phone? Manually entered? Telemetry data?)
	 * @param timeCreated - time of report
	 */
	public LocationReport(GeoCoord location, String carName, String source) {
		this(location, carName, source, System.currentTimeMillis());
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

	public String getSource() {
		return source;
	}

	public String getCarName() {
		return carName;
	}

	public GeoCoord getLocation() {
		return location;
	}


}
