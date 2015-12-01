/**
 * interface between UI and model
 * all notifications sent though here
 * all UI requests to the model sent here. 
 */

package com.ubcsolar.car;

import com.ubcsolar.Main.GlobalController;

//import org.jfree.util.Log;

import com.ubcsolar.common.*;
import com.ubcsolar.database.DatabaseController;
import com.ubcsolar.notification.*;

import jssc.SerialPortException;

public class CarController extends ModuleController {
	
	private DatabaseController myDatabase; //references to the data warehouse. Want to store the car's broadcasts
	private DataProcessor myDataProcessor; //where to send the car's broadcasts for processing. 
	private AbstractDataReceiver myDataReceiver; //what will capture the car's raw broadcasts
	private TelemDataPacket lastReceived; //last datapacket received. 
	//Could consider adding a cache for this here, but for now we will rely on the 
	//cache in the DB. 
	
	/**
	 * constructor
	 * @param myGlobalController - the GlobalController to send notifications to.
	 */
	public CarController(GlobalController myGlobalController) {
		super(myGlobalController);
		myDatabase = mySession.getMyDataBaseController();
		myDataProcessor = new DataProcessor(this);
	}
	
	/**
	 * Create a new connection to a car (will attempt to close any existing ones) 
	 */
	public void establishNewConnection(){
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
	 * Clear any lastReceived caches. 
	 * If there is no dataReceiver loaded, silently ignores the command. 
	 */
	public void stopListeningToCar(){
		if(myDataReceiver != null){
				try {
					myDataReceiver.stop();
				} catch (SerialPortException e) {
					mySession.sendNotification(new ExceptionNotification(e, "Failed at closing serial connection, "
							+ e.getClass()));
				}
			myDataReceiver = null; //Otherwise looks like it's connected. 
			this.sendNotification(new NewCarLoadedNotification("DISCONNECTED")); 
		}	
		if(this.lastReceived != null){
			this.lastReceived = null;
		}
	}
	
	/**
	 * this is where the class receives any notifications it registered for. 
	 * the "shoulder tap" 
	 */
	@Override
	public void notify(Notification n) {
		//handle any notifications that were registered for

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
	 * Gets the last received TelemDataPacket. 
	 * @return the last TelemDataPacket, or null if there hasn't yet been one. 
	 */
	public TelemDataPacket getLastTelemDataPacket(){
		return this.lastReceived;
	}

	/**
	 * This is the method that the subclasses will call. 
	 * It will add the report to the log
	 * If broadcasting is turned on, it will send a notification out.  
	 * @param carUpdateNotification - the notification to send out. 
	 */
	public void adviseOfNewCarReport(TelemDataPacket newPacket) {
		this.lastReceived = newPacket; //cache the latest one. 
		//See the note at the top about further caching. For now, lets just use DB.
		sendNotification(new CarUpdateNotification(newPacket));
	}
	
	

	
	
	
	
}
