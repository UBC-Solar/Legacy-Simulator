/**
 * I wanted the car controller and rest of the program to be 
 * able to handle connections the same way, despite the type of connection or protocol. 
 * Currently known concrete classes are the XbeeSerial connection and a basic simulated receiver. 
 */

package com.ubcsolar.car;

public abstract class AbstractDataReceiver implements Runnable {
	protected CarController myCarController; //the parent to notify of a new result. 
	protected String name; //Each receiver should have a name in case we have multiple connections
							//or just to confirm if we've killed on and started another. 
	
	//By putting as much processing as possible in another class, we reduce the risk of missing an 
	//incoming packet, especially if we accidentally go into an infinite loop.
	protected DataProcessor myDataProcessor; //the processor. 
											
	public AbstractDataReceiver(CarController toAdd, DataProcessor theProcessor){
		this.myCarController = toAdd;
		this.myDataProcessor = theProcessor;
		setName();		
	}
	//These two methods needed to implement Runnable
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
			return "ERROR: NO NAME"; //if we've screwed up and haven't named the object. 
		}
		return name;
	}

}
