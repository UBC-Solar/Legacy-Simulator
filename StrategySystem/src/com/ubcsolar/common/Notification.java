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
	
	/*
	 * returns the time that the notification was created
	 */
	public long getTime(){
		return time;
	}

}
