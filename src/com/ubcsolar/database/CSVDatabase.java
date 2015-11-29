/**
 * This class implements a basic data storage system using a CSV file. 
 * Is supposed to be a basic implementation to stand in until we get a working
 * MySQL or proper database. Saves all data to a Comma Seperated Value (CSV) file 
 * so it can be opened in Excel for later analysts. 
 */
package com.ubcsolar.database;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import javax.swing.filechooser.FileView;

import com.ubcsolar.common.DataUnit;
import com.ubcsolar.common.LogType;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.common.TelemDataPacket;

public class CSVDatabase extends Database {
	private final String folderpath = "Output\\"; //default save directory
	private Queue<String> writingQueue; //will read from here and then write. 
	private FileWriter myFileWriter; //used to write. 
	private int entryCounter; //used to generate primary keys. 
	private DateFormat actualDateFormat = new SimpleDateFormat("HH:mm:ss:SSS"); //time format. ss = seconds, SSS = ms
	//couldn't manage to format milliseconds in a way that Excel can handle as time
	//so just generated a second column to be able to graph it properly. 
	private DateFormat excelDateFormat = new SimpleDateFormat("HH:mm:ss"); //time format. ss = seconds, SSS = ms
	private boolean isDBConnected = false; //FileWriter didn't seem to have a 'isConnected' or 'isOpen' method. 
								//this is the workaround. 
	private final String columnTitles = "entry,RealTime,ExcelTime,Speed,BMSTmp,MotorTmp,Pck0Tmp,Pck1Tmp,Pck2Tmp,Pck3Tmp,TtlVltg,"
							+ "Pck0Cl1Vltg,Cl2Vltg,Cl3Vltg,Cl4Vltg,Cl5Vltg,C62Vltg,Cl7Vltg,Cl8Vltg,Cl9Vltg,Cl10Vltg,"
							+ "Pck1Cl1Vltg,Cl2Vltg,Cl3Vltg,Cl4Vltg,Cl5Vltg,C62Vltg,Cl7Vltg,Cl8Vltg,Cl9Vltg,Cl10Vltg,"
							+ "Pck2Cl1Vltg,Cl2Vltg,Cl3Vltg,Cl4Vltg,Cl5Vltg,C62Vltg,Cl7Vltg,Cl8Vltg,Cl9Vltg,Cl10Vltg,"
							+ "Pck3Cl1Vltg,Cl2Vltg,Cl3Vltg,Cl4Vltg,Cl5Vltg,C62Vltg,Cl7Vltg,Cl8Vltg,Cl9Vltg,Cl10Vltg";
	
	//Using this as a kluge until I implement actually reading from the CSV instead of just writing to it. 
	Map<Double,TelemDataPacket> recallStuff = new HashMap<Double, TelemDataPacket>();
	//Also a little kludgy, but lets me do the 'last X' method. (not sure how to do that in a map)
	ArrayList<TelemDataPacket> recallStuffList = new ArrayList<TelemDataPacket>();
	
	/**
	 * Constructor; Create and set up database. ALWAYS CREATES A NEW FILE. //TODO add an append method
	 * @param filename - filename for the .csv
	 * @throws IOException - if it can't create the file for some reason. 
	 */
	public CSVDatabase(String filename) throws IOException {
		//might as well set it to be "null". Can make a "null.csv". By setting the value
		//we don't get null pointer exceptions. 
		if(filename == null){
			filename = "null";
		}
		
		if(filename.length() == 0){
			throw new IOException("Blank filename is Invalid filename");
		}
		File testForExistence = new File(folderpath+filename + ".csv");
		if(testForExistence.exists()){
			throw new IOException("file already exists");
		}
		
		//if it's less than four chracters it's obviously not '.csv'.
		if(filename.length()<4){
			setup(filename);
			return;
		}
		//don't want to duplicate the '.csv' on the file name if someone puts it on there automatically. 		
		if(filename.substring(filename.length()-4).compareToIgnoreCase(".csv") == 0){
			setup(filename.substring(0, filename.length() - 4));
		}
		else{	
		setup(filename);
		}
	}
	
	/**
	 * Build a standard CSV database with the system time as the filename.
	 * @throws IOException
	 */
	public CSVDatabase() throws IOException {
		setup(""+System.nanoTime()); //pretty much guaranteed to be a unique filename. (unless you're making them faster than 1 per ns
									//but that is unlikely. 
	}
	
	/**
	 * Sets up and builds the first Database. File name created is filename.csv
	 * @param filename
	 * @throws IOException
	 */
	private void setup(String filename) throws IOException{
		entryCounter = 0; //TODO write a check for the last value if reopening a database
						//don't want to overwrite
		//TODO add it to a buffered OutPutStream (so that it only writes when it has a full page)
		SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(),
				"CSV Database created with name " + filename + ".csv");
		myFileWriter = new FileWriter(folderpath+ filename + ".csv");
		writingQueue = new PriorityQueue<String>();
		this.isDBConnected = true;
		setUpTables();
	}
	
	/**
	 * Returns a COPY of the last 'num' telemDataPacets received. 
	 * @param num
	 * @return
	 */
	public ArrayList<TelemDataPacket> getLastTelemDataPacket(int num){
		if(num <= 0){
			return new ArrayList<TelemDataPacket>();
		}
		
		if(num >= recallStuffList.size()){
			num = recallStuffList.size();
		}
		
		ArrayList<TelemDataPacket> temp = new ArrayList<TelemDataPacket>(num); //might as well make it the right size. 
		for(int i = (this.recallStuffList.size() - (num)); i<this.recallStuffList.size(); i++){
			temp.add(this.recallStuffList.get(i));
		}
		return temp;
	}
	

	public ArrayList<TelemDataPacket> getAllTelemDataPacketsSince(double startTime){
		
		int start = findPosOfFirstPktPastTime(startTime, this.recallStuffList);
		if(start == -1){
			return new ArrayList<TelemDataPacket>();
		}
		//TODO double check that below, it creates an ArrayList of the right size right off the bat.
		//important because we don't want it copying halfway done arrays a bunch of times. 
		ArrayList<TelemDataPacket> toReturn = new ArrayList<TelemDataPacket>(this.recallStuffList.size() - start);
		for(int i = start; i<this.recallStuffList.size(); i++){
			toReturn.add(this.recallStuffList.get(i));
		}
		return toReturn;
	}
	
	
	/**
	 * Assumes they're in chronological order. Returns the lowest position
	 * where the dataUnit's creation time is later than start time. 
	 * Returns -1 if none exist. 
	 * @param startTime
	 * @param toSearch
	 * @return
	 */
	private int findPosOfFirstPktPastTime(double startTime, ArrayList<TelemDataPacket> toSearch){
		//TODO change this to a better search algo (binary search?)
		for(int i=0; i<toSearch.size(); i++){
			if(toSearch.get(i).getTimeCreated()>startTime){
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Returns a COPY of a list of all telemdatapackets received
	 * @return
	 */
	public ArrayList<TelemDataPacket> getAllTelemDataPacket(){
		//TODO double check that this works.
		ArrayList<TelemDataPacket> toReturn = new ArrayList<TelemDataPacket>(this.recallStuffList);
		return toReturn;
	}
	
	/**
	 * returns a string containing exactly the number of commas specified
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
		/*
		 * right now in this basic implementation, the RAM and the csv
		 * are two different things, and we don't actually have a way
		 * of retrieving packets from the .csv, so this 'store' method
		 * may need to be re-written to accommodate that when it's implemented. 
		 */
		TelemDataPacket sanitizedPacket = sanitizeInput(toStore);
		putIntoRAM(sanitizedPacket);
		putIntoCSV(sanitizedPacket);
				
	}
	
	private void putIntoCSV(TelemDataPacket toStore) throws IOException {
		//TODO this algorithm needs to be improved! More dynamic
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

	/*
	 * sanitizes the TelemDataPacket so that the DB can store properly
	 * (mostly just replaces 'null' with empty maps).
	 * 
	 * The basic TelemDataPacket will reject all nulls, but it's 
	 * good to check for them anyway so we don't break the later 
	 * parts of DB processing. (I made an extended DataPacket that 
	 * returned nulls for the maps; it's possible.)
	 */
	private TelemDataPacket sanitizeInput(TelemDataPacket toStore) {
		//NOTE: The basic TelemDataPacket
		double creationTime = toStore.getTimeCreated();
		int speed = toStore.getSpeed();
		float totalVoltage = toStore.getTotalVoltage();
		HashMap<String,Integer> temperatures = toStore.getTemperatures();
		if(temperatures == null){
			temperatures = new HashMap<String, Integer>();
		}
		
		HashMap<Integer,ArrayList<Float>> cellVoltages = toStore.getCellVoltages();
		if(cellVoltages == null){
			cellVoltages = new HashMap<Integer, ArrayList<Float>>();
		}
		
		if(cellVoltages.containsValue(null)){
			for(Integer key : cellVoltages.keySet()){
				if(cellVoltages.get(key) == null){
					cellVoltages.replace(key, new ArrayList<Float>());
				}
			}
		}
		System.out.println("has null?" + cellVoltages.containsValue(null));
		return new TelemDataPacket(speed, (int) totalVoltage, temperatures, cellVoltages, creationTime);
		
		
	}

	//Puts the telemdatapacket into the right place in the list
	private void putIntoRAM(TelemDataPacket toStore) {

		//this is what was here, will need to be improved to make sure it
		//puts the packet in the right spot. 
		//TODO make it put stuff in the right spot. 
		this.recallStuff.put(toStore.getTimeCreated(), toStore);
		this.recallStuffList.add(toStore);
		
	}

	@Override
	public void store(DataUnit toStore) throws IOException {
		if(toStore instanceof TelemDataPacket){
			store((TelemDataPacket) toStore);
		}
	}

	private TelemDataPacket getTelemDataPacket(double key) {
		return this.recallStuff.get(key);
	}

	@Override
	public TelemDataPacket getTelemDataPacket(String key) {
		double doubleKey;
		try{
			doubleKey = Double.parseDouble(key);
		}catch(Exception e){
			return null;
		}
		return this.getTelemDataPacket(doubleKey);
	}

}
