package com.ubcsolar.database;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.common.*;
import com.ubcsolar.notification.CarUpdateNotification;
import com.ubcsolar.notification.DatabaseCreatedOrConnectedNotification;
import com.ubcsolar.notification.DatabaseDisconnectedOrClosed;
import com.ubcsolar.notification.ExceptionNotification;
import com.ubcsolar.notification.NewLocationReportNotification;
import com.ubcsolar.notification.NewMapLoadedNotification;
import com.ubcsolar.notification.NewDataUnitNotification;
import com.ubcsolar.notification.Notification;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DatabaseController extends ModuleController {
/* NOTE from NOAH at Nov 11, 2015.
 * Currently struggling to decide if I want to make the methods more abstract
 * (i.e GetAll(class Unit) ) or more concrete,i.e getAllTelemDataPackets. 
 * I think the more abstract way is better, then we could introduce more types of 
 * data units without touching the database module. 
 * 
 * However, it needs to be implemented well, and doing it gets complicated. 
 * As a compromise, I'll do them with the concrete implementations for now and get it
 * working, and then come back and upgrade it later. 
 * 
 * I'm not anticipating adding many brand-new data types, at least, not so many 
 * that manually adding in more concrete methods would be a hardship. So I'll make it work first, then 
 * make it work well (KISS!)
 * 
 * I'll leave the more abstract ones commented out until we decide to implement them. 
 */
	
	private final String carPacketColumnNames = "entry,RealTime,ExcelTime,Speed,BMSTmp,MotorTmp,Pck0Tmp,Pck1Tmp,Pck2Tmp,Pck3Tmp,TtlVltg,"
			+ "Pck0Cl1Vltg,Cl2Vltg,Cl3Vltg,Cl4Vltg,Cl5Vltg,C62Vltg,Cl7Vltg,Cl8Vltg,Cl9Vltg,Cl10Vltg,"
			+ "Pck1Cl1Vltg,Cl2Vltg,Cl3Vltg,Cl4Vltg,Cl5Vltg,C62Vltg,Cl7Vltg,Cl8Vltg,Cl9Vltg,Cl10Vltg,"
			+ "Pck2Cl1Vltg,Cl2Vltg,Cl3Vltg,Cl4Vltg,Cl5Vltg,C62Vltg,Cl7Vltg,Cl8Vltg,Cl9Vltg,Cl10Vltg,"
			+ "Pck3Cl1Vltg,Cl2Vltg,Cl3Vltg,Cl4Vltg,Cl5Vltg,C62Vltg,Cl7Vltg,Cl8Vltg,Cl9Vltg,Cl10Vltg";
	private final String locationUpdateColumnNames = "entry, RealTime, ExcelTime, Car, Source, latitude, longitude, elevation";
	private final String printingRouteColumnNames = "pointNum, latitude, longitude, elevation, distanceFromPrevious";
	//Added a queue to do asynchronous writes to the permanent storage. 
	//NOTE: Currently string, but will probably change this
	//when I actually implement a database (could be a SQL query). 
	Database myCarPacketDatabase;
	Database myLocationUpdateDatabase;
	String databaseName;
	private final String DEFAULT_FOLDER_LOCATION = "Output"; //default place to create the database file. (CSVDatabase tries to save to 'output' by default).
	
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
		if(myCarPacketDatabase != null && myCarPacketDatabase.isConnected()){
			myCarPacketDatabase.saveAndDisconnect();
		}
		File testForExistence = new File(DEFAULT_FOLDER_LOCATION);
		if(!testForExistence.exists() || !testForExistence.isDirectory()){
			testForExistence.mkdir();
		}
		String time = "" + System.currentTimeMillis();
		myCarPacketDatabase = new CSVDatabase("Output\\" + time + "-CarPacketSystem", carPacketColumnNames);
		myLocationUpdateDatabase = new CSVDatabase("Output\\" + time + "-locationUpdates", locationUpdateColumnNames);
		databaseName = ".csv"; //Just want to identify the type of DB (i.e csv vs SQL, etc.) It will already have the time created. 
		this.mySession.sendNotification(new DatabaseCreatedOrConnectedNotification(databaseName));
	}
	
	public boolean isDBConnected(){
		if(myCarPacketDatabase == null){
			return false;
		}
		else return myCarPacketDatabase.isConnected();
	}
	
	public String getDatabaseName(){
		if(myCarPacketDatabase == null || !myCarPacketDatabase.isConnected()){
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
		this.myCarPacketDatabase.saveAndDisconnect();
		this.myLocationUpdateDatabase.saveAndDisconnect();
		SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(), "Database saved and disconnected");
		this.mySession.sendNotification(new DatabaseDisconnectedOrClosed(this.databaseName));
		this.databaseName = null;
	}
	
	
	


	@Override
	public void register() {
		this.mySession.register(this, NewDataUnitNotification.class);
		this.mySession.register(this, CarUpdateNotification.class);
		this.mySession.register(this, NewLocationReportNotification.class);
		this.mySession.register(this, NewMapLoadedNotification.class);

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
		
		if(n.getClass() == NewMapLoadedNotification.class){
			n = (NewMapLoadedNotification) n;
			try {
				Database route = new CSVDatabase("Output\\" + "new_route - " + System.currentTimeMillis(), printingRouteColumnNames);
				route.writeRoute(((NewMapLoadedNotification) n).getRoute());
				route.flushAndSave();
			} catch (IOException e) {
				this.mySession.sendNotification(new ExceptionNotification(e, "failed at saving route"));
				e.printStackTrace();
			}
		}

	}
	

	public void store(DataUnit toStore) throws IOException{
		if(toStore.getClass() == TelemDataPacket.class){
			this.myCarPacketDatabase.store(toStore);
		}
		if(toStore.getClass() == LocationReport.class){
			this.myLocationUpdateDatabase.store(toStore);
		}
	}
	
	public void store(TelemDataPacket toStore) throws IOException{
		this.myCarPacketDatabase.store(toStore);
	}
	/* save until we implement LatLongs. 
	public void store(LatLong toStore) throws IOException{
		this.myDatabase.store(toStore);
	}*/
	
	/**
	 * Returns all current data units of type X. 
	 *  
	 * @return
	 *//*
	public <X> ArrayList<X> getAll(){
		//TODO implement this
		return null;
	}*/
	
	
	
	/**
	 * This list may be updated a new dataunits come in, but no guarantees 
	 * made based on time.
	 * @return
	 *//*
	public <X> ProtectedList<X> getAllUpdating(){
		//TODO implement me. Should just be a case of building the protectedList. 
		return null;
	}*/
	
	public ArrayList<TelemDataPacket> getAllTelemDataPacket(){
		//TODO implement me
		return null;
	}
	
	
	/**
	 * returns the last num dataunits of type X, or all of them if num>size.
	 * @param num
	 * @return
	 *//*
	public <X> ArrayList<X> getLast(int num){
		//TODO implement me
		return null;
	}*/
	
	public ArrayList<TelemDataPacket> getLastTelemDataPacket(int num){
		//TODO implement me
		return null;
	}
	/* Commented out until LatLongs implemented
	public LatLong getLastLatLong(int num){
		//TODO implement me
		return null
	}*/
	/* commented out until Metars implemented in program
	public ArrayList<Metar> getLastMetar(int num){
		return null;
	}*/
	
	/**
	 * Returns all current data units of type X received since TIME. 
	 * @param startTime, in double format. Will return data units such that their time >= startTime
	 * @return
	 *//*
	public <X> ArrayList<X> getAllSince(Double startTime){
		//TODO implement this
		return null;
	}*/
	
	public ArrayList<TelemDataPacket> getAllTelemDataPacketsSince(double startTime){
		//TODO implement this
		return null;
	}
	
	/* commented out until LatLongs implemented
	public ArrayList<LatLong> getAllLatLongsSince(double startTime){
		return null;
		//TODO implement this
	}*/
	
	/* commented out until Metars implemented in program
	public ArrayList<Metar> getAllMetarsSince(int num){
		return null;
	}*/
	/* This one I figured we'll need when we implement weather reports. (grab all weather reports in 
	 * in a 5km radius)
	public <X> ProtectedList<X> getAllCurrentNearby(LatLong spot, int radius){
		//TODO implement this
		return null;
	}*/
	 
}

