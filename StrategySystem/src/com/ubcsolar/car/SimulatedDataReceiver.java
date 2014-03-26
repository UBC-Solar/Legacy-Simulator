package com.ubcsolar.car;

public class SimulatedDataReceiver extends DataReceiver {

	
	
	private boolean isAccelerating;
	private String name;
	
	public SimulatedDataReceiver(CarController toAdd) {
		super(toAdd);
		lastSpeed = 0;
		isAccelerating = true;
		name = "basic sim";
		// TODO Auto-generated constructor stub
	}
	
	
	@Override 
	public String getName(){
		return name;
	}
	
	@Override
	protected void checkForUpdate(){
	if(lastSpeed == 0){
		isAccelerating = true;
		lastSpeed++;
		myCarController.adviseOfNewCarReport((new CarUpdateNotification(this.lastSpeed)));
	}
	else if(lastSpeed == 100){
		isAccelerating = false;
		lastSpeed --;
		myCarController.adviseOfNewCarReport((new CarUpdateNotification(this.lastSpeed)));
	}
	else{
		if(isAccelerating){
			lastSpeed++;
		}
		else{
			lastSpeed --;
		}
		myCarController.adviseOfNewCarReport((new CarUpdateNotification(this.lastSpeed)));
	}
	}
}
