package com.ubcsolar.common;

import java.util.HashMap;
import java.util.Map;

public class GeoCoord extends DataUnit {
	
	public final static String classCSVHeaderRow ="latitude, longitude, elevation";
	/**
	 * turns the class fields into an entry for a csv file
	 * see returnsEntireTable for info on row versus table
	 * @return the row as a string
	 */
	public String getCSVEntry()
	{
		StringBuilder toPrint= new StringBuilder("");
		toPrint.append(this.getLat()+ ",") ;
		toPrint.append(this.getLon()+ ",") ;
		toPrint.append(this.getElevation()) ;

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

private final double lat;
private final double lon;
private final double elevation; //in meters
private final double timeCreated;

public GeoCoord(double lat, double lon, double elevationInMeters){
	
	if(isLatValid(lat)){     // use lat if it passes
	this.lat = lat;
	}
	else{
		throw new IllegalArgumentException("Illegal latitude");
	}
	if(isLongValid(lon)){
	this.lon = lon;
	}
	else{
		throw new IllegalArgumentException("Illegal longitude");
	}
	this.elevation = elevationInMeters;
	this.timeCreated = System.currentTimeMillis();
	
}


//checks-------------------------------------------------------------------------
private boolean isLatValid(double lat2) { //check to see if latitude is ok to use
	if (lat2<-90 || lat2>90){
	return false;
	}
	else{
		return true;
	}
}
private boolean isLongValid(double lon2) {
	if(lon2<-180 || lon2>180){
	return false;
	}
	else{
		return true;
	}
}
//-------------------------------------------------------------------------------


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
public double calculateDistance(GeoCoord ending){
	double kmDistance = Math.abs(
							haversine(this.lat,
									this.lon, 
									ending.getLat(), 
									ending.getLon()));
	return kmDistance;
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
   // double c = Math.asin(Math.sqrt(a));
    double d = r * c;
   
    return d;
}

/**
 * returns a String summarizing the Point.
 * Form: "name lat,long,elevation"
 */
public String toString(){
	return this.lat + "," + this.lon + "," + this.elevation;
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
