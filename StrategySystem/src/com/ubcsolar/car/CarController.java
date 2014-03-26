/**
 * interface between UI and model
 * all notifications sent though here
 * all UI requests to the model sent here. 
 */

package com.ubcsolar.car;

import com.ubcsolar.common.ModuleController;
import com.ubcsolar.common.Notification;
import com.ubcsolar.ui.GlobalController;

// TODO: Check threading. Should be calling subclasses as their own thread
public class CarController extends ModuleController {

	private DataReceiver myDataReceiver; //what will capture the car's broadcasts
	
	/**
	 * constructor
	 * @param toAdd - the GlobalController to send notifications to.
	 */
	public CarController(GlobalController toAdd) {
		super(toAdd);
		myDataReceiver = new DataReceiver(this);
		myDataReceiver.run();
		sendNotification(new NewCarLoadedNotification(myDataReceiver.getName()));
		// TODO Auto-generated constructor stub
	}
	
	public void startFakeCar(){
		myDataReceiver = new SimulatedDataReceiver(this);
		myDataReceiver.run();
		sendNotification(new NewCarLoadedNotification(myDataReceiver.getName()));
	}
	
	/**
	 * this is where the class receives any notifications it registered for. 
	 * the "shoulder tap" 
	 */
	@Override
	public void notify(Notification n) {
		//TODO handle any notifications that were registered for

	}

	/**
	 * registers for any notifications it needs to hear
	 */
	@Override
	public void register() {
		// TODO Auto-generated method stub

	}
	
	public String getLoadedCarName(){
		return myDataReceiver.getName();
	}

	//returns null if no speed has ever been reported
	public int getLastReportedSpeed(){
		return myDataReceiver.getLastReportedSpeed();
	}

	public void adviseOfNewCarReport(CarUpdateNotification carUpdateNotification) {
		// TODO store this in some kind of record. 
		sendNotification(carUpdateNotification);
		
	}

}
