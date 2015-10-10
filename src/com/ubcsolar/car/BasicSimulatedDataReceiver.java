/**
 * This class is meant to act a data receiver, but it 
 * programatically creates (fakes) receiving data packets.  
 * It's important to have this for testing; there will be all sorts
 * of scenarios that we want to test for tat we won't actually put the car into,
 * this will allow us to simulate those from the lowest level in this program. 
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
	
	private void receiveNewPacket(TelemDataPacket newPacket){
		this.myDataProcessor.store(newPacket);
	}
		
	/**
	 * This class is Runnable (can be in it's own thread), this is what will be called to start it. 
	 */
	@Override
	public void run() {
		if(autoGenerate == null){
			createNewTimer();
		}
		else{ //we want to pretend we're connecting to a new car
			
			//UPDATE: if we want a new connection, we should make a new dataReceiver class, not handle it 
			//from within the same obect. 
			//autoGenerate.cancel(); 
			//createNewTimer();
		}
	}
	
	private void createNewTimer(){
		Long startDelay = (long) 0.0; //Start immideately 
		Long repetitionDelayInMS = (long) 1000; //once per second seems reasonable. 
												//will need to tune to match the actual speed of the car. 
		autoGenerate = new Timer("TheFakeCar");
		autoGenerate.schedule(new generateNewThings(), startDelay, repetitionDelayInMS);
	}
	
	//Need to kill all the threads here. 
	/**
	 * This method is required to implement Runnable. 
	 */
	public void stop(){
		autoGenerate.cancel();
	}
	
	/**
	 * Every DataReceiver must have a name (in case we connect to multiple cars, or get old datapackets)
	 * ... Except that we kill and start a new car connection from within here. Maybe we should
	 * force the creation of a new SimDataReceiver. Instead of handling that within the class. 
	 */
	@Override 
	public String getName(){
		return name;
	}
	/*
	 * This was a super simple algorithm method from when I first built the class.
	 * Been replaced by the Timer Task. 

	/**
	 * generated a new speed. Accelerates up to 100, then slows back down to 0 linerally. 
	 */ /*
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
	}*/

	@Override
	void setName() {
		this.name = "fake!";
		
	}


	
/**
 * This private class is used to generate new data packets.
 * It has to be a timerTask because the parent class uses a Timer to 
 * regularly generate new data packets in it's own thread (just as this 
 * program will have no control over when it receives a packet from the car, it should
 * have no control over when it 'receives' a packet from this class)
 * @author Noah
 *
 */
private class generateNewThings extends TimerTask{
	int iterations = 0; //I put this in to calculate new values from. Simplistic algo
						//could (almost definitely) probably be improved.
	
	/**
	 * This method is what the timer calls. Calls the method in parent class to handle the new packet. 
	 *  (analogy is that the parent class 'receives' a new packet that we programmed instead of the car). 
	 */
	@Override
	public void run() {
		receiveNewPacket(generateNewTelemDataPack()); 
											
	}
	
	/**
	 * Here is where we should put whatever algorithm to simulate the car getting a data packet. 
	 * If we want really accurated simulated data, we should probably hook the sim in here,
	 * however, there are going to be cases where we want the results to be different than the simulator.
	 * so if anything, this class will have to run it's own sim rather than just connecting to the real one. 
	 * 
	 * One possibly easy solution is to just have this sequentially
	 * read a list of packets from a pre-programed list. 
	 * @return the next TelemDataPacket in the series. 
	 */
	private TelemDataPacket generateNewTelemDataPack(){
		
		int speed = 1 * iterations; //terrible algo. Please improve me. 
									//Car should not be going faster than light after 
									//running this program 1x1^23 iterations. 
		int totalVoltage = 1 * iterations; //also terrible algo. 
		int stateOfCharge = 50; //voltage is changing while state of charge stays the same. 
								//seems legit. 
		
		Map<String,Integer> temperatures = new HashMap<String,Integer>();
		Map<Integer,ArrayList<Float>> cellVoltages = new HashMap<Integer,ArrayList<Float>>();
		//TODO not sure what these look like in the real car, need to program in some basic fakes
		TelemDataPacket tempPacket = new TelemDataPacket(speed, totalVoltage, temperatures, cellVoltages);
		iterations++;
		return tempPacket;
	}
}

}