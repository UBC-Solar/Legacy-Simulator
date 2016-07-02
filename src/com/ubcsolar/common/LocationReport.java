package com.ubcsolar.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

public class LocationReport extends DataUnit {
	
	private DateFormat actualDateFormat = new SimpleDateFormat("HH:mm:ss.SSS"); //time format. ss = seconds, SSS = ms
	//couldn't manage to format milliseconds in a way that Excel can handle as time
	//so just generated a second column to be able to graph it properly. 
	private DateFormat excelDateFormat = new SimpleDateFormat("HH:mm:ss"); //time format. ss = seconds, SSS = ms
	
	
	public final static String classCSVHeaderRow= "RealTime, ExcelTime, Car, Source, latitude, longitude, elevation";
	/**
	 * turns the class fields into an entry for a csv file
	 * see returnsEntireTable for info on row versus table
	 * @return the row as a string
	 */
	public String getCSVEntry()
	{
		GeoCoord locToAdd = this.getLocation();
		StringBuilder toPrint = new StringBuilder("");
		toPrint.append(actualDateFormat.format(this.getTimeCreated()) + ",");
		toPrint.append(excelDateFormat.format(this.getTimeCreated()) + ",");
		toPrint.append(this.getCarName() + ",");
		toPrint.append(this.getSource() + ",");
		toPrint.append(locToAdd.getLat() + ",");
		toPrint.append(locToAdd.getLon() + ",");
		toPrint.append(locToAdd.getElevation());
		return toPrint.toString();
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
