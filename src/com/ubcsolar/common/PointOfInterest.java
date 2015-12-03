package com.ubcsolar.common;

public class PointOfInterest {
	private final GeoCoord location;
	private final String title;
	private final String description;
	
	public PointOfInterest(GeoCoord location, String name, String description) {
		this.location = location;
		this.title = name;
		this.description = description;
	}
	
	
	public PointOfInterest(double lattitude, double longitude, double altitude, String name, String description){
		location = new GeoCoord(lattitude, longitude, altitude);
		title = name;
		this.description = description;
	}


	public GeoCoord getLocation() {
		return location;
	}


	public String getName() {
		return title;
	}


	public String getDescription() {
		return description;
	}
	

	
	
}
