package com.ubcsolar.weather;

import com.ubcsolar.common.Notification;

public class NewWeatherLoadedNotification extends Notification {
private final METAR newMETAR;

public NewWeatherLoadedNotification(METAR toSend){
	super();
	newMETAR=toSend;
}
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return null;
	}
	public METAR getNewMETAR() {
		return newMETAR;
	}

	
	
}
