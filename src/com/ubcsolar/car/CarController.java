/**
 * interface between UI and model
 * all notifications sent though here
 * all UI requests to the model sent here. 
 */

package com.ubcsolar.car;

//import org.jfree.util.Log;

import com.ubcsolar.common.*;
import com.ubcsolar.common.ModuleController;
import com.ubcsolar.database.DatabaseController;
import com.ubcsolar.notification.*;
import com.ubcsolar.ui.GlobalController;

import jssc.SerialPortException;

// TODO: Check threading. Should be calling subclasses as their own thread
public class CarController extends ModuleController {
	
	//TODO update this and refactor code to use the database module
	private DatabaseController myDatabase; //references to the data warehouse. Want to store the car's broadcasts
	private DataProcessor myDataProcessor; //where to send the car's broadcasts for processing. 
	private AbstractDataReceiver myDataReceiver; //what will capture the car's raw broadcasts
	private TelemDataPacket lastReceived; //last datapacket received. 
	//TODO implement a proper cache for this. Not sure exactly what the parameters are for it. 
	
	/**
	 * constructor
	 * @param myGlobalController - the GlobalController to send notifications to.
	 */
	public CarController(GlobalController myGlobalController) {
		super(myGlobalController);
		myDatabase = mySession.getMyDataBaseController();
		myDataProcessor = new DataProcessor(this); //TODO turn this into threaded properly.
	}
	
	/**
	 * Create a new connection to a car (will attempt to close any existing ones) 
	 */
	public void establishNewConnection(){
		//TODO set up the exceptions properly
		try{
			stopListeningToCar(); //close any existing current connection
		
		myDataReceiver = new XbeeSerialDataReceiver(this, myDataProcessor);
		myDataReceiver.run();
		sendNotification(new NewCarLoadedNotification(myDataReceiver.getName()));
		}
		catch(SerialPortException e){
			//Not able to create the datareceiver connection 
			ExceptionNotification notification = new ExceptionNotification(e, "Unable to connect to Car, no Serial Port found"); 
			SolarLog.write(LogType.ERROR, notification.getTimeCreated(), notification.getMessage());
			sendNotification(notification);
			e.printStackTrace();
			stopListeningToCar();
		}
	}
	
	/**
	 * This method is a bit of a kludge; starts a data receiver that fakes receiving broadcasts
	 * from the car. (To use for testing)
	 * Should probably be able to set up the main newConnection method to be able to take an argument 
	 * specifying the car to connect to (ie fake or real). 
	 */
	public void startFakeCar(){
		stopListeningToCar();
		myDataReceiver = new BasicSimulatedDataReceiver(myDataProcessor, this);
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
			myDataReceiver = null; //Otherwise looks like it's connected. 
			this.sendNotification(new NewCarLoadedNotification("DISCONNECTED")); 
		}	
		
		//TODO consider something about the DB here. It should probably do something if a new
		//car is loaded, but do we handle that here or in the DB?
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
		if(myDataReceiver == null){
			return "None";
		}
		return myDataReceiver.getName();
	}

	/**
	 * gets the last reported speed of the car. 
	 * Returns -1 if no speed has ever been reported.  
	 * @return the last reported speed of the car. 
	 */
	public int getLastReportedSpeed(){
		//TODO: consider moving this to a Double or float. 
		//TODO cache the last couple TelemDataPackets here somewhere
		//TODO configure this to actually get the needed ones from the DB 
		//if they're not in the cache. 
		if(this.lastReceived == null){
			return -1;
			//TODO check DB to see if it's ever received one.
		}
		else{
			return this.lastReceived.getSpeed();
		}
		//return myDatabase.getLastSpeed();
	}

	/**
	 * This is the method that the subclasses will call. 
	 * It will add the report to the log
	 * If broadcasting is turned on, it will send a notification out.  
	 * @param carUpdateNotification - the notification to send out. 
	 */
	public void adviseOfNewCarReport(TelemDataPacket newPacket) {
		this.lastReceived = newPacket; //cache the latest one. 
		sendNotification(new CarUpdateNotification(newPacket));
	}

	
	
	
	
}
