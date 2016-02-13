package com.ubcsolar.notification;

import com.ubcsolar.common.LocationReport;

public class NewCarLocationNotification extends Notification {
	private final LocationReport theLocationOfCar;
	
	public NewCarLocationNotification(LocationReport locationOfCar) {
		super();
		this.theLocationOfCar = locationOfCar;
		
	}
	
	public LocationReport getCarLocation(){
		return theLocationOfCar;
	}

	@Override
	public String getMessage() {
		String message = this.theLocationOfCar.getLocation() + " at " + this.theLocationOfCar.getTimeCreated() + " (" + this.theLocationOfCar.getCarName() + ").";
		return message;
	}

}
