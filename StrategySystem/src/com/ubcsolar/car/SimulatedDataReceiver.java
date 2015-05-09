/**
 * this class provides simulated notifications. 
 * Currently very simple and not very realistic
 */
package com.ubcsolar.car;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.ubcsolar.notification.CarUpdateNotification;

public class SimulatedDataReceiver extends DataReceiver {
	
	private String name;
	private Timer testDataTimer;
	
	public SimulatedDataReceiver(CarController toAdd) {
		super(toAdd);
		data = new DataReceived(); //has garbage data, we don't care
		name = "basic sim";
		// TODO Auto-generated constructor stub
	}
	
	
	@Override 
	public String getName(){
		return name;
	}
	
	public void run(){
		this.testDataTimer = new Timer();
		this.testDataTimer.schedule(new TimerTask(){
				@Override
				public void run(){
					checkForUpdate();
				}
			}, 0, 1000);
	}
	
	/**
	 * generated a new speed. Accelerates up to 100, then slows back down to 0 linerally. 
	 */
	@Override
	protected void checkForUpdate(){
		DataReceived newData = new DataReceived();
		newData.speed = (data.speed + 1) % 9001;
		newData.stateOfCharge = (data.stateOfCharge + 1) % 100;
		newData.totalVoltage = (float) (data.totalVoltage + 0.1);
		newData.temperatures.put("xxx", 101);
		newData.cellVoltages.put(1, new ArrayList<Float>());
		newData.cellVoltages.get(1).add((float) 1.0);
		data = newData;
	}
}
