/**
 * this class forms the receiver for the data transmission from the car. 
 * */

package com.ubcsolar.car;

import java.util.Timer;
import java.util.TimerTask;

//TODO turn this class into an abstract one, and move the listening implementation into a concrete
//subclass
public class DataReceiver implements Runnable { //needs to be threaded so it can listen for a response

	protected CarController myCarController; //the parent to notify of a new result. 
	private String name = "live"; //"live" because it's listening for real transmissions
	private Timer myTimer; //how often to check. May be able to remove this and just have it block while
							//listening
	protected int lastSpeed; //the last reported speed of the car. Will probably 
							//have a value for each possible value
	
	/**
	 * default constructor.
	 * @param toAdd - the CarController to notify when it gets a new result
	 */ 
	 
	 	public DataReceiver(CarController toAdd){
		myCarController = toAdd;
	}
	
	/**
	 * gets the last reported speed, in km/h. 
	 * @return the last repoted speed, in km/h. 
	 */
	public int getLastReportedSpeed(){
		return lastSpeed;
	}
	
	/**
	 * starts the listening timer
	 */
	@Override
	public void run() {
		//TODO: rather than a timer, just have it block until it gets a new one. 
		myTimer = new Timer();
		myTimer.schedule(new TimerTask() {
				@Override
				public void run() {
						checkForUpdate();
					
				}
			}, 0, 2000);
		}
		
	public void stop(){
		myTimer.cancel();
	}
	/**
	 * this class will check to see if an update has landed in the buffer yet.
	 */
	protected void checkForUpdate(){
			//TODO check the Arduino socket/buffer for any transmission. 
		//myCarController.adviseOfNewCarReport(new CarUpdateNotification(25));
		
	}
	
	
	/**
	 * 
	 * @return the name of the car loaded. 
	 */
	public String getName() {
		return name;
	}

}
