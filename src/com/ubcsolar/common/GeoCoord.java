package com.ubcsolar.common;

import java.util.HashMap;
import java.util.Map;

public class GeoCoord extends DataUnit {
private final double lat;
private final double lon;
private final double elevation; //in meters
private final double timeCreated;

public GeoCoord(double lat, double lon, double elevationInMeters){
	this.lat = lat;
	this.lon = lon;
	this.elevation = elevationInMeters;
	this.timeCreated = System.currentTimeMillis();
}

public GeoCoord(double lat, double lon, double elevationInMeters, double timeCreated){
	this.lat = lat;
	this.lon = lon;
	this.elevation = elevationInMeters;
	this.timeCreated = timeCreated;
}


/**
 *
 * @param toCheckAgainst - the point to compare to
 * returns true if the lats, longs, and elevations are the same (within a delta)
 * 
 * @return
 */
@Override
public boolean equals(Object toCheck){
	
	if(!(toCheck instanceof GeoCoord)){
		return false;
	}
	
	GeoCoord toCheckAgainst;
	try{
		toCheckAgainst = (GeoCoord) toCheck;
	}catch(ClassCastException e){
		return false; //if it didn't cast, obviously not equal. 
	}
	
	
	double delta = 0.000000000000001;
	double minDelta = delta*-1;
	double latDiff = toCheckAgainst.getLat() - lat;
	double lonDiff = toCheckAgainst.getLon() - lon;
	double elevationDiff = toCheckAgainst.getElevation() - elevation;
	
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
public double calculateDistance(GeoCoord ending, DistanceUnit unit){
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
 * returns a String summarizing the Point.
 * Form: "name lat,long,elevation"
 */
public String toString(){
	return this.lat + "," + this.lon + "," + this.elevation + "(m)";
}

public double getLat(){
	return lat;
}

public double getLon(){
	return lon;
}

/**
 * returns in meters by default
 * @return
 */
public double getElevation(){
	return elevation;
}

public double getElevation(DistanceUnit unit){
	switch(unit){
		case METERS: return this.elevation;
		case FEET: return this.convertToFeet(this.elevation);
		case KILOMETERS: return this.elevation/1000;
		case MILES: return this.elevation/1609.34;
		default: return -1;
	}
}

private double convertToFeet(double meters) {
	return meters*3.28084;
}

@Override
public double getTimeCreated() {
	return this.timeCreated;
}

@Override
public Map<String, ? extends Object> getAllValues() {
	HashMap<String, Object> keyValues = new HashMap<String, Object>();
	keyValues.put("Time Created", this.timeCreated);
	keyValues.put("Latitude", this.lat);
	keyValues.put("Longitude", this.lon);
	keyValues.put("Elevation", this.elevation);
	
	return keyValues;
}


	
	
}
