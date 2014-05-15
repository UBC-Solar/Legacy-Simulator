package com.ubcsolar.map;

public class Point {
private double lat;
private double lon;
private double elevation; //in meters
private String information;

public Point(double lat, double lon, double elevationInMeters){
	this.lat = lat;
	this.lon = lon;
	this.elevation = elevationInMeters;
}

public Point(int lat, int lon, int elevationInMeters){
	this.lat = lat;
	this.lon = lon;
	this.elevation = elevationInMeters;
}

public Point(int lat, double lon, double elevationInMeters){
	this.lat = lat;
	this.lon = lon;
	this.elevation = elevationInMeters;
}

public Point(double lat, int lon, double elevationInMeters){
	this.lat = lat;
	this.lon = lon;
	this.elevation = elevationInMeters;
}

public Point(double lat, double lon, int elevationInMeters){
	this.lat = lat;
	this.lon = lon;
	this.elevation = elevationInMeters;
}

public Point(int lat, double lon, int elevationInMeters){
	this.lat = lat;
	this.lon = lon;
	this.elevation = elevationInMeters;
}


public Point(int lat, int lon, double elevationInMeters){
	this.lat = lat;
	this.lon = lon;
	this.elevation = elevationInMeters;
}

public Point(double lat, int lon, int elevationInMeters){
	this.lat = lat;
	this.lon = lon;
	this.elevation = elevationInMeters;
}

public Point(int lat, int lon, int elevationInMeters, String note){
	this.lat = lat;
	this.lon = lon;
	this.elevation = elevationInMeters;
	information = note;
}

public Point(int lat, double lon, double elevationInMeters, String note){
	this.lat = lat;
	this.lon = lon;
	this.elevation = elevationInMeters;
	information = note;
}

public Point(double lat, int lon, double elevationInMeters, String note){
	this.lat = lat;
	this.lon = lon;
	this.elevation = elevationInMeters;
	information = note;
}

public Point(double lat, double lon, int elevationInMeters, String note){
	this.lat = lat;
	this.lon = lon;
	this.elevation = elevationInMeters;
	information = note;
}

public Point(int lat, double lon, int elevationInMeters, String note){
	this.lat = lat;
	this.lon = lon;
	this.elevation = elevationInMeters;
	information = note;
}


public Point(int lat, int lon, double elevationInMeters, String note){
	this.lat = lat;
	this.lon = lon;
	this.elevation = elevationInMeters;
	information = note;
}

public Point(double lat, int lon, int elevationInMeters, String note){
	this.lat = lat;
	this.lon = lon;
	this.elevation = elevationInMeters;
	information = note;
}


public Point(double lat, double lon, double elevationInMeters, String note){
	this.lat = lat;
	this.lon = lon;
	this.elevation = elevationInMeters;
	information = note;
}


/**
 * @override
 * @param toCheck - the point to compare to
 * returns true if the lats, longs, and elevations are the same (within a delta)
 * 
 * @return
 */
public boolean equals(Point toCheck){

	double delta = 0.000000000000001;
	double minDelta = delta*-1;
	double latDiff = toCheck.getLat() - lat;
	double lonDiff = toCheck.getLon() - lon;
	double elevationDiff = toCheck.getElevationInMeters() - elevation;
	
	if(
			latDiff<delta &&
			latDiff>minDelta &&
			lonDiff<delta &&
			lonDiff>minDelta &&
			elevationDiff<delta &&
			elevationDiff>minDelta){
		return true;
	}
	else{
		return false;
	}
	
}












public String getInformation(){
	return information;
}

public void setInformation(String newInfo){
	information = newInfo;
}

public double getLat(){
	return lat;
}

public double getLon(){
	return lon;
}

public double getElevationInMeters(){
	return elevation;
}

public double getElevationInFeet(){
	return convertToFeet(elevation);
}

private double convertToFeet(double meters) {
	return meters*3.28084;
}


	
	
}
