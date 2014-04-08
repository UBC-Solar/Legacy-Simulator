package com.ubcsolar.weather;

public class METAR {
private final Long time;
private final String airportID;
private final int windSpeed;
public METAR(Long time, String airID, int windSpeed){
	this.time = time;
	this.airportID = airID;
	this.windSpeed = windSpeed;
}
public int getWindSpeed() {
	return windSpeed;
}
public String getAirportID() {
	return airportID;
}
public Long getTime() {
	return time;
}




}
