package com.ubcsolar.car;

import com.ubcsolar.common.Notification;

public class CarUpdateNotification extends Notification {

	private final int carSpeed;
	
	public CarUpdateNotification(int carSpeed){
		super();
		this.carSpeed = carSpeed;
	}
	public int getNewCarSpeed(){
		return carSpeed;
	}
	
}
