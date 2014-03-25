/**
 * in the same idea as Exceptions (no one uses Exception, they all extend it)
 * this forms the bases of the notification system in the program. 
 * All notifications will extend this one. 
 */

package com.ubcsolar.common;

public abstract class Notification { //abstract because it can't be used itself, only extended. 
	
	private long time; //the time at which the notification was created
	
	/**
	 * constructor, grabs the current time. 
	 */
	public Notification(){
		time = System.currentTimeMillis();
	}
	
	/**
	 * returns the time that the notification was created
	 */
	public long getTime(){
		return time;
	}
	
	/**
	 * for logging, gets the message explaining what's going on in a String
	 * i.e "Loaded map name is now: ____" or "car speed is now: ___"
	 * @return A string explaining the state
	 */
	public abstract String getMessage();

}
