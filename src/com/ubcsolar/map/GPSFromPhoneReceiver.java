package com.ubcsolar.map;

import com.ubcsolar.common.CarLocation;
import com.ubcsolar.common.GeoCoord;

public class GPSFromPhoneReceiver {
	private final MapController parent;
	private final String carName;
	private final String source; 
	
	/**
	 * 
	 * @param parent - the object to notify when a GPS event comes in
	 * @param carName - the name of the car that this even represents (in case we ever do multiple cars at once)
	 * @param source - where did it come from? GPS, Phone, manual entry via the UI?
	 */
	public GPSFromPhoneReceiver(MapController parent, String carName, String source) {
		this.parent = parent;
		this.carName = carName;
		this.source = source;
	}
	
	
	public void GPSEventJustCameIn(String someInformation){
		// GPS coordinate randomly picked (in the mid-east states)
		//CarLocation(GeoCoord location, String carName, String source, double timeCreated)
		double lat = 39.9277;
		double lon = -83.684;
		double elevation = 327.203;
		GeoCoord location = new GeoCoord(lat,lon,elevation);
		parent.recordNewCarLocation(new CarLocation(location, this.carName, this.source, System.currentTimeMillis()));
	}
	

}
