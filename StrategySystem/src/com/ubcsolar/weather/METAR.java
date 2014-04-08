package com.ubcsolar.weather;

public class METAR {
//private final Long rawText;
private final String stationID;
//private final Long observationTime;
private final Long time;
/*
private final latitude;
private final longitude;


*/
private final int windSpeed;
public METAR(Long time, String airID, int windSpeed){
	this.time = time;
	this.stationID = airID;
	this.windSpeed = windSpeed;
}
public int getWindSpeed() {
	return windSpeed;
}
public String getAirportID() {
	return stationID;
}
public Long getTime() {
	return time;
}




}
