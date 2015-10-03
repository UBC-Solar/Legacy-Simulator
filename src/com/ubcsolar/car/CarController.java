/**
 * interface between UI and model
 * all notifications sent though here
 * all UI requests to the model sent here. 
 */

package com.ubcsolar.car;

import com.ubcsolar.common.ModuleController;
import com.ubcsolar.notification.CarUpdateNotification;
import com.ubcsolar.notification.NewCarLoadedNotification;
import com.ubcsolar.notification.Notification;
import com.ubcsolar.ui.GlobalController;

// TODO: Check threading. Should be calling subclasses as their own thread
public class CarController extends ModuleController {
	
	
	private Database myDatabase; //references to the data warehouse. Want to store the car's broadcasts
	private DataProcessor myDataProcessor; //where to send the car's broadcasts for processing. 
	private DataReceiver myDataReceiver; //what will capture the car's raw broadcasts
	
	/**
	 * constructor
	 * @param myGlobalController - the GlobalController to send notifications to.
	 */
	public CarController(GlobalController myGlobalController) {
		super(myGlobalController);
		

		myDatabase = new Database();
	}
	
	/**
	 * Create a new connection to a car (will attempt to close any existing ones) 
	 */
	public void establishNewConnection(){
		//TODO set up the exceptions properly
		stopListeningToCar(); //close any existing current connection
		myDataReceiver = new SimulatedDataReceiver(myDataProcessor, this);
		myDataReceiver.run();
		sendNotification(new NewCarLoadedNotification(myDataReceiver.getName()));
	}
	
	/**
	 * This method is a bit of a kludge; starts a data receiver that fakes receiving broadcasts
	 * from the car. (To use for testing)
	 * Should probably be able to set up the main newConnection method to be able to take an argument 
	 * specifying the car to connect to (ie fake or real). 
	 */
	public void startFakeCar(){
		stopListeningToCar();
		myDataReceiver = new SimulatedDataReceiver(myDataProcessor, this);
		myDataReceiver.run();
		sendNotification(new NewCarLoadedNotification(myDataReceiver.getName()));
	}
	
	/**
	 * stops listening to updates. If it's a simulated car, stops producing new notifications. 
	 * If there is no dataReceiver loaded, silently ignores the command. 
	 */
	public void stopListeningToCar(){
		//TODO set up exceptions properly. What if can't close? What if...? 
		if(myDataReceiver != null){
			myDataReceiver.stop();
		}
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
		// add registration code here. 

	}
	
	/**
	 * gets the name of the car currently loaded. 
	 * Car name will be something like "live" or "sim ___"
	 * @return Name of the currently loaded car
	 */
	public String getLoadedCarName(){
		return myDataReceiver.getName();
	}

	/**
	 * gets the last reported speed of the car. 
	 * Returns 0 if no speed has ever been reported.  
	 * @return the last reported speed of the car. 
	 */
	public int getLastReportedSpeed(){
		//TODO: consider moving this to a Double or float. 
		return myDatabase.getLastSpeed();
	}

	/**
	 * This is the method that the subclasses will call. 
	 * It will add the report to the log
	 * If broadcasting is turned on, it will send a notification out.  
	 * @param carUpdateNotification - the notification to send out. 
	 */
	public void adviseOfNewCarReport(CarUpdateNotification carUpdateNotification) {
		// TODO store this in some kind of record. 
		sendNotification(carUpdateNotification);
		
	}

}
