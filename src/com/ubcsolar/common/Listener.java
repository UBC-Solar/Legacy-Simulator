/**
 * Notifications and listeners are how we will implement multithreading eventually. 
 * Notifications will be messages sent across threads via the Global Controller.
 * If a class needs to be able to get those messages, then it needs to implement this
 * Listener Interface. 
 */
package com.ubcsolar.common;

import com.ubcsolar.notification.Notification;


public interface Listener {
	
	/**
	 * this method will be called when a notification needs to be sent
	 * @param n - the notification sending
	 */
	public void notify(Notification n);
	
	/**
	 * used to register (probably with the Global Controller) to receive notifications
	 */
	public abstract void register(); 

}
