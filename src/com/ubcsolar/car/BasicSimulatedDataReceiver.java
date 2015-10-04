/**
 * this class provides simulated notifications. 
 * Currently very simple and not very realistic
 */
package com.ubcsolar.car;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.ubcsolar.notification.CarUpdateNotification;

import jssc.SerialPortException;

public class BasicSimulatedDataReceiver extends AbstractDataReceiver {

	int speed;
	
	private boolean isAccelerating;
	private String name;
	private Timer autoGenerate;
	public BasicSimulatedDataReceiver(DataProcessor myProcessor, CarController toAdd){
		super(toAdd, myProcessor); //Curently breaks because DataReceiver was changed.
		//TODO: modify and adjust so that I can have a fake car. 
		speed = 0; 
		isAccelerating = true;
		name = "basic sim";
		// TODO Auto-generated constructor stub
	}
	
	private void newThingy(TelemDataPacket newPacket){
		this.myDataProcessor.store(newPacket);
	}
	private class generateNewThings extends TimerTask{
		int iterations = 0;
		@Override
		public void run() {
			newThingy(generateNewTelemDataPack());
		}
		
		private TelemDataPacket generateNewTelemDataPack(){
			TelemDataPacket tempPacket = new TelemDataPacket();
			int speed = 1 * iterations;
			int totalVoltage = 1 * iterations;
			int stateOfCharge = 50;
			Map<String,Integer> temperatures = new HashMap<String,Integer>();
			Map<Integer,ArrayList<Float>> cellVoltages = new HashMap<Integer,ArrayList<Float>>();
			tempPacket.LoadNewData(speed, totalVoltage, stateOfCharge, temperatures, cellVoltages);
			iterations++;
			return tempPacket;
		}
	}
	
	@Override
	public void run() {
		if(autoGenerate == null){
			createNewTimer();
		}
		else{
			autoGenerate.cancel();
			createNewTimer();
		}
	}
	
	private void createNewTimer(){
		Long startDelay = (long) 0.0;
		Long repetitionDelayInMS = (long) 1000;
		autoGenerate = new Timer("TheCar");
		autoGenerate.schedule(new generateNewThings(), startDelay, repetitionDelayInMS);
	}
	
	public void stop(){
		autoGenerate.cancel();
	}
	
	@Override 
	public String getName(){
		return name;
	}
	
	/**
	 * generated a new speed. Accelerates up to 100, then slows back down to 0 linerally. 
	 */
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

	@Override
	void setName() {
		this.name = "fake!";
		
	}


	
}
