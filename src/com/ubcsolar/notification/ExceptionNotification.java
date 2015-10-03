/**
 * This notification is to send exceptions to every class that needs to be aware of them.
 * Primarily, this will be the for the main UI to display to the user
 */
package com.ubcsolar.notification;

/**
 * @author Noah
 *
 */
public class ExceptionNotification extends Notification {

	private Exception e;
	private String explanation;
	
	
	public ExceptionNotification(Exception e, String explanation){
		super();
		this.e=e;
		this.explanation = explanation;
	}
	@Override
	public String getMessage() {
		return explanation;
	}
	
	public Exception getException(){
		return e;
	}

}
