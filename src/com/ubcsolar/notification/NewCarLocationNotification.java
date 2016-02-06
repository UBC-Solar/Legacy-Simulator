package com.ubcsolar.notification;

import com.ubcsolar.common.CarLocation;

public class NewCarLocationNotification extends Notification {
	private final CarLocation theLocationOfCar;
	
	public NewCarLocationNotification(CarLocation locationOfCar) {
		super();
		this.theLocationOfCar = locationOfCar;
		
	}

	@Override
	public String getMessage() {
		String message = this.theLocationOfCar.getLocation() + " at " + this.theLocationOfCar.getTimeCreated() + " (" + this.theLocationOfCar.getCarName() + ").";
		return message;
	}

}
