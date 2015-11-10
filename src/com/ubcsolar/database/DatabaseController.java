package com.ubcsolar.database;

import com.ubcsolar.common.*;
import com.ubcsolar.common.ModuleController;
import com.ubcsolar.common.TelemDataPacket;
import com.ubcsolar.notification.CarUpdateNotification;
import com.ubcsolar.notification.DatabaseCreatedOrConnectedNotification;
import com.ubcsolar.notification.DatabaseDisconnectedOrClosed;
import com.ubcsolar.notification.ExceptionNotification;
import com.ubcsolar.notification.NewDataUnitNotification;
import com.ubcsolar.notification.Notification;
import com.ubcsolar.ui.GlobalController;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DatabaseController extends ModuleController {

	//Added a queue to do asynchronous writes to the permanent storage. 
	//NOTE: Currently string, but will probably change this
	//when I actually implement a database (could be a SQL query). 
	Queue<String> writingQueue; //will read from here and then write. 
	FileWriter myFileWriter;
	Database myDatabase;
	String databaseName;
	public DatabaseController(GlobalController myGlobalController)throws IOException {
		super(myGlobalController);
		buildNewDatabase();
	}
	
  /**
   * This method used to build the connection to the database.
   * Could probably do some work here so that we could specify the database type. 
   * @throws IOException
   */
	public void buildNewDatabase() throws IOException{
		if(myDatabase != null && myDatabase.isConnected()){
			myDatabase.saveAndDisconnect();
		}
		myDatabase = new CSVDatabase();
		databaseName = ".csv";
		this.mySession.sendNotification(new DatabaseCreatedOrConnectedNotification(databaseName));
	}
	
	public boolean isDBConnected(){
		if(myDatabase == null){
			return false;
		}
		else return myDatabase.isConnected();
	}
	
	public String getDatabaseName(){
		if(myDatabase == null || !myDatabase.isConnected()){
			return null;
		}
		else return databaseName;
	}
	
	/* Maybe use these? would like to make them more abstract than that. 
	public void buildNewCSVDatabase() throws IOException{
		myDatabase = new CSVDatabase();
	}*/
	
	/**
	 * finalize database, save everything to disk, and then close or disconnect it. 
	 * @throws IOException - if it can't disconnect the database. 
	 */
	public void saveAndDisconnect() throws IOException{
		this.myDatabase.saveAndDisconnect();
		SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(), "Database saved and disconnected");
		this.mySession.sendNotification(new DatabaseDisconnectedOrClosed(this.databaseName));
		this.databaseName = null;
	}
	
	
	


	@Override
	public void register() {
		this.mySession.register(this, NewDataUnitNotification.class);
		this.mySession.register(this, CarUpdateNotification.class);

	}
	
	@Override
	public void notify(Notification n) {
		try {
		if(n.getClass() == CarUpdateNotification.class){
			
				store(((CarUpdateNotification) n).getDataUnit());
				//TODO add 'store' methods that are more concrete. 
				return; //otherwise we get double entries with the fail-safe data-unit catch. 
			
		}
		//This was when the Register/Notify system could handle extended notifications. 
		//(i.e would know that a newTelemDataPacket is also a NewDataUnitNotification)
		//Left in here as a failsafe, but should be using only concrete classes. 
		if(n instanceof NewDataUnitNotification){
			store(((NewDataUnitNotification) n).getDataUnit());
		}
		
		
		} catch (IOException e) {
			this.mySession.sendNotification(new ExceptionNotification(e, "Error storing lastest data unit"));
			e.printStackTrace();
		}

	}
	

	public void store(DataUnit toStore) throws IOException{
		System.out.println("DATAUNIT Method USED TO STORE");
		if(toStore.getClass() == TelemDataPacket.class){
			this.myDatabase.store(toStore);
		}
	}
	
	public void store(TelemDataPacket toStore) throws IOException{
		this.myDatabase.store(toStore);
	}
	/* save until we implement LatLongs. 
	public void store(LatLong toStore) throws IOException{
		this.myDatabase.store(toStore);
	}*/
	
	/**
	 * Returns all current data units of type X. 
	 * This list may be updated a new dataunits come in, but no guarantees 
	 * made based on time. 
	 * @return
	 */
	public <X> ProtectedList<X> getAll(){
		//TODO implement this
		return null;
	}
	
	/**
	 * returns the last num dataunits of type X, or all of them if num>size. 
	 * This list may be updated a new dataunits come in, but no guarantees 
	 * made based on time. 
	 * @param num
	 * @return
	 */
	public <X> ProtectedList<X> getLast(int num){
		return null;
	}
	
	/**
	 * Returns all current data units of type X received since TIME. 
	 * This list may be updated a new dataunits come in, but no guarantees 
	 * made based on time. 
	 * @param startTime, in double format. Will return data units such that their time >= startTime
	 * @return
	 */
	public <X> ProtectedList<X> getAllSince(Double startTime){
		//TODO implement this
		return null;
	}
	/* This one I figured we'll need when we implement weather reports. (grab all weather reports in 
	 * in a 5km radius)
	public <X> ProtectedList<X> getAllCurrentNearby(LatLong spot, int radius){
		//TODO implement this
		return null;
	}*/
	 
}

