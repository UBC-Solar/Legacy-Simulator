/**
 * This class implements a basic data storage system using a CSV file. 
 * Is supposed to be a basic implementation to stand in until we get a working
 * MySQL or proper database. Saves all data to a Comma Seperated Value (CSV) file 
 * so it can be opened in Excel for later analysts. 
 */
package com.ubcsolar.database;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import com.ubcsolar.common.DataUnit;
import com.ubcsolar.common.LogType;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.common.TelemDataPacket;

public class CSVDatabase extends Database {
	
	Queue<String> writingQueue; //will read from here and then write. 
	FileWriter myFileWriter; //used to write. 
	int entryCounter; //used to generate primary keys. 
	DateFormat actualDateFormat = new SimpleDateFormat("HH:mm:ss:SSS"); //time format. ss = seconds, SSS = ms
	//couldn't manage to format milleseconds in a way that Excel can handle as time
	//so just generated a second coloumn to be able to graph it properly. 
	DateFormat excelDateFormat = new SimpleDateFormat("HH:mm:ss"); //time format. ss = seconds, SSS = ms
	boolean isDBConnected = false; //FileWriter didn't seem to have a 'isConnected' or 'isOpen' method. 
								//this is the workaround. 
	final String columnTitles = "entry,RealTime,ExcelTime,Speed,BMSTmp,MotorTmp,Pck0Tmp,Pck1Tmp,Pck2Tmp,Pck3Tmp,TtlVltg,"
							+ "Pck0Cl1Vltg,Cl2Vltg,Cl3Vltg,Cl4Vltg,Cl5Vltg,C62Vltg,Cl7Vltg,Cl8Vltg,Cl9Vltg,Cl10Vltg,"
							+ "Pck1Cl1Vltg,Cl2Vltg,Cl3Vltg,Cl4Vltg,Cl5Vltg,C62Vltg,Cl7Vltg,Cl8Vltg,Cl9Vltg,Cl10Vltg,"
							+ "Pck2Cl1Vltg,Cl2Vltg,Cl3Vltg,Cl4Vltg,Cl5Vltg,C62Vltg,Cl7Vltg,Cl8Vltg,Cl9Vltg,Cl10Vltg,"
							+ "Pck3Cl1Vltg,Cl2Vltg,Cl3Vltg,Cl4Vltg,Cl5Vltg,C62Vltg,Cl7Vltg,Cl8Vltg,Cl9Vltg,Cl10Vltg";
	Map<Integer,DataUnit> recallStuff = new HashMap<Integer, DataUnit>();
	
	/**
	 * Constructor; Create and set up database. ALWAYS CREATES A NEW FILE. //TODO add an append method
	 * @param filename - filename for the .csv
	 * @throws IOException - if it can't create the file for some reason. 
	 */
	public CSVDatabase(String filename) throws IOException {
		super();
		setup(filename);
	}
	
	/**
	 * Build a standard CSV database with the system time as the filename.
	 * @throws IOException
	 */
	public CSVDatabase() throws IOException {
		super();
		setup(""+System.currentTimeMillis()); //Guaranteed to be a unique filename. (unless you're making more than 1 per ms
											//in which case you have other problems to worry about)
	}
	
	/**
	 * Sets up and builds the first Database. 
	 * @param filename
	 * @throws IOException
	 */
	private void setup(String filename) throws IOException{
		entryCounter = 0; //TODO write a check for the last value if reopening a database
						//don't want to overwrite
		//TODO: add a check for the '.csv' so we don't duplicate it. 
		//TODO add it to a buffered OutPutStream (so that it only writes when it has a full page)
		SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(),
				"CSV Database created with name " + filename + ".csv");
		myFileWriter = new FileWriter("Output\\"+ filename + ".csv");
		writingQueue = new PriorityQueue<String>();
		this.isDBConnected = true;
		setUpTables();
	}
	
	/**
	 * returns a string containly exactly the number of commas specificed
	 * (useful for adding empty rows of data to a .csv)
	 * @param numberOfCommas
	 * @return a String consisting solely of commas. 
	 */
	private String numberOfCommas(int numberOfCommas){
		String toReturn = "";
		for(int i=0; i<numberOfCommas; i++){
			toReturn += ',';
		}
		
		return toReturn;
	}
	
	/*
	 * This method is used to generate the queries needed to set up the tables
	 * in the Database. For this CSV it will just be the column headers. 
	 */
	private void setUpTables() throws IOException{
		//Currently assuming a .csv file
		String tableSetup;
		tableSetup = this.columnTitles;
		writingQueue.add(tableSetup);
		flushAndSave();
	}
	
	/* Commented out until LatLongs introduced
	private void store(LatLong toStore){
		
	}*/
	
	/* Commented out until LatLongs introduced
	 private void store(metar toStore){
	 }
	 */
	
	/*
	 * This is a klugey method. It has to be here in case the system crashes and needs to 
	 * force the DB to flush everything to file. 
	 * Currently, its the only way to actually write anything to file,
	 * but eventually I will add in a thread to do that asyncronously.
	 * NOTE: There may be a stock java structure that does that 
	 * (maintains performance benefit and doesn't lock the program with slow writes to file)
	 * possibly bufferedOutputStream?
	 */
	public void flushAndSave() throws IOException{
		while(this.writingQueue.size()>0){
			myFileWriter.write(writingQueue.remove() + '\n');
		}
		myFileWriter.flush();
		
	}
	
	/**
	 * Returns true IFF the database is currently connected and not closed
	 * @return
	 */
	public boolean isConnected(){
		return this.isDBConnected;
	}


	@Override
	public void saveAndDisconnect() throws IOException {
		flushAndSave();
		myFileWriter.close();
		this.isDBConnected = false; 
	}
	
	/**
	 * Parse a TelemDataPacket into String that can be written straight to the CSV. 
	 * @param toStore - the TelemDataPacket to store
	 * @throws IOException - if there are issues writing it. 
	 */
	private void store(TelemDataPacket toStore) throws IOException{
		//"entry,Time,Speed,BMSTmp,MotorTmp,Pck0Tmp,Pck1Tmp,Pck2Tmp,Pck3Tmp,TtlVltg,Pck0Vltg,Pck2Vltg,Pck3Vltg";
		this.recallStuff.put(entryCounter, toStore);
		HashMap<String, Integer> temperatures = toStore.getTemperatures();
		HashMap<Integer, ArrayList<Float>> voltages = toStore.getCellVoltages();
		String toPrint ="" + 
				this.entryCounter++ + "," + 
				actualDateFormat.format(toStore.getTimeCreated()) + "," +
				excelDateFormat.format(toStore.getTimeCreated()) + "," +
				toStore.getSpeed() + "," +
				temperatures.get("bms")  + "," +
				temperatures.get("motor") + "," +
				temperatures.get("pack0") + "," +
				temperatures.get("pack1") + "," +
				temperatures.get("pack2") + "," +
				temperatures.get("pack3") + "," +
				toStore.getTotalVoltage() + ",";
				for(Float f : voltages.get(0)){
					toPrint += f + ",";
				}
				for(Float f : voltages.get(1)){
					toPrint += f + ",";
				}
				for(Float f : voltages.get(2)){
					toPrint += f + ",";
				}
				for(Float f : voltages.get(3)){
					toPrint += f + ",";
				}
				
		this.writingQueue.add(toPrint);
		this.flushAndSave();		
	}
	@Override
	public void store(DataUnit toStore) throws IOException {
		if(toStore instanceof TelemDataPacket){
			store((TelemDataPacket) toStore);
		}
	}

	@Override
	public DataUnit get(String key) {
		int intKey;
		//try{
			intKey = Integer.parseInt(key);
		//catch(Cast exception e){
			//throw illigal arument exception
			//}
			//TODO handle nulls. 
		return this.recallStuff.get(intKey);
	}

}
