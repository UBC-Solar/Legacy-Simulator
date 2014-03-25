package com.ubcsolar.car;

import java.util.Timer;
import java.util.TimerTask;

public class DataReceiver implements Runnable {

	private CarController myCarController;

	private int speed; //in km/h
	private boolean isAccelerating;
	private Timer myTimer;
	
	public DataReceiver(CarController toAdd){
		myCarController = toAdd;
		speed = 0;
		isAccelerating = true;
	}
	
	@Override
	public void run() {

		myTimer = new Timer();
		myTimer.schedule(new TimerTask() {
				@Override
				public void run() {
						getNewSpeed();
					
				}
			}, 0, 200);
		}
		
	

	private int getNewSpeed(){
		if(speed == 0){
			isAccelerating = true;
			speed++;
			myCarController.notify(new CarUpdateNotification(this.speed));
			return speed; 
		}
		else if(speed == 100){
			isAccelerating = false;
			speed --;
			myCarController.notify(new CarUpdateNotification(this.speed));
			return speed;
		}
		else{
			if(isAccelerating){
				speed++;
			}
			else{
				speed --;
			}
			myCarController.notify(new CarUpdateNotification(this.speed));
			return speed;
		}
			
		
	}
	
}
