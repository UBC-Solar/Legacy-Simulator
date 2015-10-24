/**
 * this class is used as a snapshot of the car situation. It will hold the latest
 * data reported. 
 */

package com.ubcsolar.notification;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.ubcsolar.common.TelemDataPacket;

public class CarUpdateNotification extends Notification {

	private final TelemDataPacket thePacket; //the speed of the car at the time of notification
	String message = null;
	
	public CarUpdateNotification(TelemDataPacket dataPacket){
		//may need to add things here as we figure out what information exactly we're getting from the car
		super();
		this.thePacket = dataPacket;
	}
	
	/**
	 * return carSpeed
	 * @return the speed of car
	 */
	public TelemDataPacket getDataPacket(){
		return thePacket;
	}
	
	
	/**
	 * turns the information in this notification into a sentance. 
	 */
	@Override
	public String getMessage() {
		if (message == null){ //will be first time. No need to generate the string if we don't need to
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
			Date now = new Date((long) (thePacket.getTimeCreated()));
		    String strDate = sdfDate.format(now);
			message = "Received new data pack from car at " + strDate;
			return message;
		}
		else{
			return message;
		}
		
	}
	
}
