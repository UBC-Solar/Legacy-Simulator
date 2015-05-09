package com.ubcsolar.weather;

public class METAR {
private final Long rawText;
private final String stationID;
private final Long observationTime;
private final double latitude;
private final double longitude;
private final double tempC;
private final double dewPointC;
private final int windDirection;
private final int windSpeed;
private final double visibilityStatute;
private final double altim;
private final double seaLevelPressure;
private final String flightCategory;
private final double pressureTendency3hr;
private final String metarType;
private final double elevation;


public METAR(Long rawText, String stationID, Long time, double latitude, double longitude, double temp, double dewPoint, int windDir, 
		int windSpeed, double visibility, double altim, double seaLevelPressure, String flightCategory, double pressureTendency, 
		String metarType, double elevation){
	this.rawText = rawText;
	this.stationID = stationID;
	this.observationTime = time;
	this.latitude = latitude;
	this.longitude = longitude;
	this.tempC = temp;
	this.dewPointC = dewPoint;
	this.windDirection = windDir;
	this.windSpeed = windSpeed;
	this.visibilityStatute = visibility;
	this.altim = altim;
	this.seaLevelPressure = seaLevelPressure;
	this.flightCategory = flightCategory;
	this.pressureTendency3hr = pressureTendency;
	this.metarType = metarType;
	this.elevation = elevation; 	
}


public Long getRawText(){
	return rawText;
}

public String getStationID(){
	return stationID;
}

public Long getObsvTime(){
	return observationTime;
}

public double getLatitude(){
	return latitude;
}

public double getLongitude(){
	return longitude;
}

public double temp(){
	return tempC;
}

public double dewPoint(){
	return dewPointC;
}

public int windDirection(){
	return windDirection;
}


public int windSpeed(){
	return windSpeed;
}
	
public double visibility(){
	return visibilityStatute;
}

public double altim(){
	return altim;
}

public double seaLevelPressure(){
	return seaLevelPressure;
}

public String flightCategory(){
	return flightCategory;
}

public double pressureTendency(){
	return pressureTendency3hr;
}

public String metarType(){
	return metarType;
}
public double elevation(){
	return elevation;
}


}
