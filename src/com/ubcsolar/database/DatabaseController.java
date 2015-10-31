package com.ubcsolar.database;

import com.ubcsolar.common.*;
import com.ubcsolar.common.ModuleController;
import com.ubcsolar.common.TelemDataPacket;
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
	}
	
	
	
	@Override
	public void notify(Notification n) {
		if(n instanceof NewDataUnitNotification){
			
			try {
				store(((NewDataUnitNotification) n).getDataUnit());
			} catch (IOException e) {
				this.mySession.sendNotification(new ExceptionNotification(e, "Error storing lastest data unit"));
				e.printStackTrace();
			}
		}

	}

	@Override
	public void register() {
		this.mySession.register(this, NewDataUnitNotification.class);

	}
	

	public void store(DataUnit toStore) throws IOException{
		if(toStore.getClass() == TelemDataPacket.class){
			this.myDatabase.store(toStore);
		}
		
		
	}

}
