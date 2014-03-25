/**
 * this interface allows the creation of a list
 * of listeners. 
 */
package com.ubcsolar.common;


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
