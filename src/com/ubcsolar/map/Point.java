package com.ubcsolar.map;

import com.ubcsolar.common.DistanceUnit;

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

/**
 * calculate absolute distance between this point and another. 
 * @param ending - the end point (or start point)
 * @param unit - the unit the distance should be reported in
 * @return the absolute distance between this point and another
 */
public double calculateDistance(Point ending, DistanceUnit unit){
	double kmDistance = Math.abs(
							haversine(this.lat,
									this.lon, 
									ending.getLat(), 
									ending.getLon()));
	
	if(unit == DistanceUnit.FEET){
		return kmDistance * 3280.84;
	}
	else if(unit == DistanceUnit.KILOMETERS){
		return kmDistance;
	}
	else if(unit == DistanceUnit.MILES){
		return kmDistance * 0.621371;
	}

	return -1.0;
}



/**
 * Calculates the distance in km between two lat/long points
 * using the haversine formula.
 * Does not take into account altitude.
 * Code derrived from http://stackoverflow.com/questions/18861728/calculating-distance-between-two-points-represented-by-lat-long-upto-15-feet-acc
 * @param lat1 - first latitude 
 * @param lng1 - first longitude
 * @param lat2 - second latitude
 * @param lng2 - second longitude
 * @return d - the distance (in KM) between two lat/long points.
 */
public static double haversine(
        double lat1, double lng1, double lat2, double lng2) {
    int r = 6371; // average radius of the earth in km
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lng2 - lng1);
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
       Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) 
      * Math.sin(dLon / 2) * Math.sin(dLon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double d = r * c;
    return d;
}







/**
 * returns a String sumaraizing the Point.
 * Form: "name lat,long,elevation"
 */
public String toString(){
	return this.information + " " + this.lat + "," + this.lon + "," + this.elevation;
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
