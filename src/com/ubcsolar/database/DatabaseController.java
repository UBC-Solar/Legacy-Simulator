package com.ubcsolar.database;

import com.ubcsolar.common.*;
import com.ubcsolar.common.ModuleController;
import com.ubcsolar.common.TelemDataPacket;
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
	public DatabaseController(GlobalController myGlobalController)throws IOException {
		super(myGlobalController);
		
		//TODO write the code that generates the filename
		//TODO add it to a buffered outputstream (so that it only writes when it has a full page)
		myFileWriter = new FileWriter("test");
		
		writingQueue = new PriorityQueue<String>();
		 setUpTables();
		// TODO Auto-generated constructor stub
	}

	
	/*
	 * This method is used to generate the queries needed to set up the tables
	 * in the Database
	 */
	private void setUpTables() throws IOException{
		//Currently assuming a .csv file
		String tableSetup;
		tableSetup = "Entry,Time,Speed,TtlVltg,SOC,Temps";
		writingQueue.add(tableSetup);
		printAllQueued();
	}
	
	/*
	 * This is a klugey method. It has to be here in case the system crashes and needs to 
	 * force the DB to flush everything to file. 
	 * Currently, its the only way to actually write anything to file,
	 * but eventually I will add in a thread to do that asyncronously.
	 * NOTE: There may be a stock java structure that does that 
	 * (maintains performance benefit and doesn't lock the program with slow writes to file)
	 * possibly bufferedOutputStream?
	 */
	public void printAllQueued() throws IOException{
		while(this.writingQueue.size()>0){
			myFileWriter.write(writingQueue.remove());
		}
		myFileWriter.flush();
		
	}
	@Override
	public void notify(Notification n) {
		System.out.println("got notification!: ");
		if(n instanceof NewDataUnitNotification){
			
			store(((NewDataUnitNotification) n).getDataUnit());
		}

	}

	@Override
	public void register() {
		this.mySession.register(this, NewDataUnitNotification.class);

	}
	private void store(TelemDataPacket toStore){
		System.out.println("Database storage method for a TelemDataPacket activated");
	}
	
	/* Commented out until LatLongs introduced
	private void store(LatLong toStore){
		
	}*/
	
	/* Commented out until LatLongs introduced
	 private void store(metar toStore){
	 }
	 */
	public void store(DataUnit toStore){
		if(toStore.getClass() == TelemDataPacket.class){
			store((TelemDataPacket) toStore);
			System.out.println("GOT A DATA UNIT: " + toStore.getClass()); 
		}
		
		
	}

}
