package com.ubcsolar.car;

import com.ubcsolar.common.Notification;

public class NewCarLoadedNotification extends Notification {

	private String nameOfCar;
	public NewCarLoadedNotification(String name){
		super();
		nameOfCar = name;
		
	}
	public String getNameOfCar(){
		return nameOfCar;
	}
	@Override
	public String getMessage() {
		return (nameOfCar + " is now loaded");
	}

	
	
}
