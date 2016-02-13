package com.ubcsolar.notification;

import com.ubcsolar.common.DataUnit;
import com.ubcsolar.common.LocationReport;

public class NewLocationReportNotification extends NewDataUnitNotification {
	private final LocationReport theLocationOfCar;
	
	public NewLocationReportNotification(LocationReport locationOfCar) {
		super();
		this.theLocationOfCar = locationOfCar;
		
	}
	
	public LocationReport getCarLocation(){
		return theLocationOfCar;
	}

	@Override
	public String getMessage() {
		String message = this.theLocationOfCar.getCarName() + " location reported at: " + this.theLocationOfCar.getLocation();
		return message;
	}

	@Override
	public DataUnit getDataUnit() {
		return theLocationOfCar;
	}

}
