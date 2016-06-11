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
	
	String databaseName;
	private final String DEFAULT_FOLDER_LOCATION = "Output\\"; //default place to create the database file. (CSVDatabase tries to save to 'output' by default).
	private final String sessionFolder = "" + System.currentTimeMillis() + "\\";
	
	private final CSVDatabase<TelemDataPacket> myCarPacketDatabase;
	private final CSVDatabase<LocationReport> myLocationUpdateDatabase;
	private final String simResultsFolderName = DEFAULT_FOLDER_LOCATION + sessionFolder + "simulations";
	private final List<CSVDatabase<SimulationReport>> mySimulationResultsDB;
	private final String forecastFolderName = DEFAULT_FOLDER_LOCATION + sessionFolder + "forecasts";
	private final List<CSVDatabase<ForecastReport>> forecastReportsDB;
	private final String routesFolderName = DEFAULT_FOLDER_LOCATION + sessionFolder + "Routes";
	private final List<CSVDatabase<Route>> loadedRoutesDB;
	

	
	public DatabaseController(GlobalController myGlobalController)throws IOException {
		super(myGlobalController);
		String sessionFolderName = this.DEFAULT_FOLDER_LOCATION + this.sessionFolder;
		myCarPacketDatabase = new CSVDatabase<TelemDataPacket>(sessionFolderName);
		myLocationUpdateDatabase = new CSVDatabase<LocationReport>(sessionFolderName);
		mySimulationResultsDB = new ArrayList<CSVDatabase<SimulationReport>>();
		forecastReportsDB = new ArrayList<CSVDatabase<ForecastReport>>();
		loadedRoutesDB = new ArrayList<CSVDatabase<Route>>();
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
		try{
			if(n instanceof CarUpdateNotification){
				CarUpdateNotification temp = (CarUpdateNotification) n;
				
			}
		}
		catch(Exception e){
			
		}
		
		
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

