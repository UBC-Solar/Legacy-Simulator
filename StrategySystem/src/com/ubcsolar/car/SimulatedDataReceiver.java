/**
 * this class provides simulated notifications. 
 * Currently very simple and not very realistic
 */
package com.ubcsolar.car;

import com.ubcsolar.notification.CarUpdateNotification;

public class SimulatedDataReceiver extends DataReceiver {

	int speed;
	
	private boolean isAccelerating;
	private String name;
	
	public SimulatedDataReceiver(DataProcessor myProcessor, CarController toAdd) {
		super(toAdd, myProcessor);
		speed = 0; 
		isAccelerating = true;
		name = "basic sim";
		// TODO Auto-generated constructor stub
	}
	
	
	@Override 
	public String getName(){
		return name;
	}
	
	/**
	 * generated a new speed. Accelerates up to 100, then slows back down to 0 linerally. 
	 */
	@Override
	protected void checkForUpdate(){
	if(speed == 0){
		isAccelerating = true;
		speed++;
		myCarController.adviseOfNewCarReport((new CarUpdateNotification(this.speed)));
	}
	else if(speed == 100){
		isAccelerating = false;
		speed --;
		myCarController.adviseOfNewCarReport((new CarUpdateNotification(this.speed)));
	}
	else{
		if(isAccelerating){
			speed++;
		}
		else{
			speed --;
		}
		myCarController.adviseOfNewCarReport((new CarUpdateNotification(this.speed)));
	}
	}
}
