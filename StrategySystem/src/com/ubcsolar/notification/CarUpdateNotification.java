/**
 * this class is used as a snapshot of the car situation. It will hold the latest
 * data reported. 
 */

package com.ubcsolar.notification;


public class CarUpdateNotification extends Notification {

	private final int carSpeed; //the speed of the car at the time of notification
	
	public CarUpdateNotification(int carSpeed){
		//may need to add things here as we figure out what information exactly we're getting from the car
		super();
		this.carSpeed = carSpeed;
	}
	
	/**
	 * return carSpeed
	 * @return the speed of car
	 */
	public int getNewCarSpeed(){
		return carSpeed;
	}
	
	
	/**
	 * turns the information in this notification into a sentance. 
	 */
	@Override
	public String getMessage() {
		return "Car is now travelling at: " + carSpeed + "Km/h";
	}
	
}
