package com.ubcsolar.car;

import java.util.Timer;
import java.util.TimerTask;

public class DataReceiver implements Runnable {

	protected CarController myCarController;
	private String name = "live";
	private Timer myTimer;
	protected int lastSpeed;
	
	public DataReceiver(CarController toAdd){
		myCarController = toAdd;
	}
	
	public int getLastReportedSpeed(){
		return lastSpeed;
	}
	@Override
	public void run() {

		myTimer = new Timer();
		myTimer.schedule(new TimerTask() {
				@Override
				public void run() {
						checkForUpdate();
					
				}
			}, 0, 2000);
		}
		
	

	protected void checkForUpdate(){
			//TODO check the Arduino socket/buffer for any transmission. 
		//myCarController.adviseOfNewCarReport(new CarUpdateNotification(25));*/
		
	}

	public String getName() {
		return name;
	}

}
