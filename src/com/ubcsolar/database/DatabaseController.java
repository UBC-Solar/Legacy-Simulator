package com.ubcsolar.database;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.common.*;
import com.ubcsolar.notification.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import org.json.JSONObject;

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
	
	
	String sessionFolderName = this.DEFAULT_FOLDER_LOCATION + this.sessionFolder;
	private final CSVDatabase<TelemDataPacket> myCarPacketDatabase = new CSVDatabase<TelemDataPacket>(sessionFolderName + "TelemPackets");
	private final CSVDatabase<LocationReport> myLocationUpdateDatabase = new CSVDatabase<LocationReport>(sessionFolderName + "Location Reports");
	private final String simResultsFolderName = sessionFolderName + "simulations\\";
	private final List<CSVDatabase<SimulationReport>> mySimulationResultsDB = new ArrayList<CSVDatabase<SimulationReport>>();
	private final String forecastFolderName = sessionFolderName + "forecasts\\";
	private final List<CSVDatabase<ForecastReport>> forecastReportsDB = new ArrayList<CSVDatabase<ForecastReport>>();
	private final String routesFolderName = sessionFolderName + "Routes\\";
	private final List<CSVDatabase<Route>> loadedRoutesDB = new ArrayList<CSVDatabase<Route>>();
	private String lastForecastFilename;
	

	
	public DatabaseController(GlobalController myGlobalController)throws IOException {
		super(myGlobalController);
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
		this.mySession.register(this, NewForecastReport.class);
		this.mySession.register(this, NewSimulationReportNotification.class);
	}
	
	@Override
	public void notify(Notification n) {
		try{
			if(n instanceof CarUpdateNotification){
				CarUpdateNotification temp = (CarUpdateNotification) n;
				this.myCarPacketDatabase.store(temp.getDataPacket());
			}
			else if(n instanceof NewLocationReportNotification){
				NewLocationReportNotification temp = (NewLocationReportNotification) n;
				this.myLocationUpdateDatabase.store(temp.getCarLocation());
			}
			else if(n instanceof NewMapLoadedNotification){
				NewMapLoadedNotification temp = (NewMapLoadedNotification) n;
				CSVDatabase<Route> toAdd;
				toAdd = new CSVDatabase<Route>(this.routesFolderName+System.currentTimeMillis()+"_"+temp.getMapLoadedName()); //name should be unique
				toAdd.store(temp.getRoute());
				this.loadedRoutesDB.add(toAdd);
			}
			else if(n instanceof NewForecastReport){
				NewForecastReport temp = (NewForecastReport) n;
				CSVDatabase<ForecastReport> toAdd;
				toAdd = new CSVDatabase<ForecastReport>(this.forecastFolderName+System.currentTimeMillis()); //name should be unique
				toAdd.store(temp.getTheReport());
				this.forecastReportsDB.add(toAdd);
				
				this.storeForecastAsFile(temp.getTheReport());				
			}
			
			else if(n instanceof NewSimulationReportNotification){
				NewSimulationReportNotification temp = (NewSimulationReportNotification) n;
				CSVDatabase<SimulationReport> toAdd;
				toAdd = new CSVDatabase<SimulationReport>(this.simResultsFolderName+System.currentTimeMillis()); //name should be unique
				toAdd.store(temp.getSimReport());
				this.mySimulationResultsDB.add(toAdd);
			}
			
			
		}
		catch(IllegalArgumentException e){
			e.printStackTrace(); //TODO make it send an errornotification. 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	


	
	
	private void storeForecastAsFile(ForecastReport theReport) throws FileNotFoundException {
		String filename = this.forecastFolderName + System.currentTimeMillis() + theReport.getRouteNameForecastsWereCreatedFor()+ ".FIO";
		this.lastForecastFilename = filename;
		PrintWriter toPrint = new PrintWriter(filename);
		toPrint.print(theReport.toJSON().toString());
		toPrint.close();
	}
	
	public ForecastReport getLastCachedForecastReport(File file) throws IOException, FileNotFoundException{
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		String resultBack = "";
		String temp = "";
		while((temp = br.readLine())!=null){
			resultBack += temp;
		}
		
		return new ForecastReport(new JSONObject(resultBack));
		
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
	
	
	public String getCurrentOutputFolderName(){
		return this.sessionFolderName;
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

