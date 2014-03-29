/**
 * Used to announce that a new car has been loaded so that 
 * all UI elements can update the name of the car, reset 
 * any counters, and update pointers. 
 * 
 */

package com.ubcsolar.notification;


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
		return ("Car \"" + nameOfCar + "\" is now loaded");
	}

	
	
}
