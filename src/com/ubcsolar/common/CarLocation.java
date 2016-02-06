package com.ubcsolar.common;

import java.util.Map;

public class CarLocation extends DataUnit {

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
	public CarLocation(GeoCoord location, String carName, String source, double timeCreated) {
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
	public CarLocation(GeoCoord location, String carName, String source) {
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
