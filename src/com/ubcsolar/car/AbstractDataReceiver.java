package com.ubcsolar.car;

import jssc.SerialPortException;

public abstract class AbstractDataReceiver implements Runnable {
	protected CarController myCarController; //the parent to notify of a new result. 
	protected String name; //"live" because it's listening for real transmissions
	protected DataProcessor myDataProcessor;
	public AbstractDataReceiver(CarController toAdd, DataProcessor theProcessor){
		this.myCarController = toAdd;
		this.myDataProcessor = theProcessor;
		setName();		
	}
	
	public abstract void run();
	public abstract void stop();
	/**
	 * Used to set the name of the Receiver (for multiple cars, etc)
	 */
	abstract void setName();
	
	/**
	 * @return the name of the car loaded. 
	 */
	public String getName() {
		if(name == null){
			return "none";
		}
		return name;
	}

}
