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

import com.ubcsolar.common.DataUnit;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.LocationReport;
import com.ubcsolar.common.LogType;
import com.ubcsolar.common.Route;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.common.TelemDataPacket;

public class CSVDatabase<V extends DataUnit>{
	private Queue<String> writingQueue; //will read from here and then write. 
	private FileWriter myFileWriter; //used to write. 
	private int entryCounter; //used to generate primary keys. 
	private DateFormat actualDateFormat = new SimpleDateFormat("HH:mm:ss.SSS"); //time format. ss = seconds, SSS = ms
	//couldn't manage to format milliseconds in a way that Excel can handle as time
	//so just generated a second column to be able to graph it properly. 
	private DateFormat excelDateFormat = new SimpleDateFormat("HH:mm:ss"); //time format. ss = seconds, SSS = ms
	private boolean isDBConnected = false; //FileWriter didn't seem to have a 'isConnected' or 'isOpen' method. 
								//this is the workaround. 
	private String columnTitles;
	
	//Using this as a kluge until I implement actually reading from the CSV instead of just writing to it. 
	private Map<Double,V> recallStuff = new HashMap<Double, V>();
	//Also a little kludgy, but lets me do the 'last X' method. (not sure how to do that in a map)
	private ArrayList<V> recallStuffList = new ArrayList<V>();
	
	/**
	 * Constructor; Create and set up database. ALWAYS CREATES A NEW FILE.
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
		File testForExistence = new File(filename + ".csv");
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
	 * Attempts to save to folder 'Output', or default file level if that doesn't work. 
	 * @throws IOException
	 */
	public CSVDatabase() throws IOException {
		File testForExistence = new File("Output");
		if(testForExistence.exists() && testForExistence.isDirectory()){
			setup("Output\\"+System.nanoTime()); //pretty much guaranteed to be a unique filename. (unless you're making them faster than 1 per ns
			//but that is unlikely. 
		}
		else{
			//if there is no 'output' folder
			setup("" + System.nanoTime());
		}

	}
	
	/**
	 * Sets up and builds the first Database. File name created is filename.csv
	 * @param filename
	 * @throws IOException
	 */
	private void setup(String filename) throws IOException{
		entryCounter = 0; 
		SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(),
				"CSV Database created with name " + filename + ".csv");
		System.out.println("FILENAME : " + filename);
		File file = new File(filename+".csv");
		file.getParentFile().mkdirs(); //makes the folder path
		myFileWriter = new FileWriter(file);
		writingQueue = new PriorityQueue<String>();
		this.isDBConnected = true;
	}
	
	/**
	 * Returns a COPY of the last 'num' telemDataPacets received. 
	 * @param num
	 * @return
	 */
	public ArrayList<V> getLast(int num){
		if(num <= 0){
			return new ArrayList<V>();
		}
		
		if(num >= recallStuffList.size()){
			num = recallStuffList.size();
		}
		
		ArrayList<V> temp = new ArrayList<V>(num); //might as well make it the right size. 
		for(int i = (this.recallStuffList.size() - (num)); i<this.recallStuffList.size(); i++){
			temp.add(this.recallStuffList.get(i));
		}
		return temp;
	}
	
	/**
	 * Returns All TelemDataPackets created later than the specified time, or the entire
	 * list if there are no packets earlier than the startTime. 
	 * @param startTime
	 * @return the ArrayList of all 
	 */
	public ArrayList<V> getAllSince(double startTime){
		
		int start = findPosOfFirstPktPastTime(startTime, this.recallStuffList);
		if(start == -1){
			return new ArrayList<V>();
		}
		
		ArrayList<V> toReturn = new ArrayList<V>(this.recallStuffList.size() - start);
		for(int i = start; i<this.recallStuffList.size(); i++){
			toReturn.add(this.recallStuffList.get(i));
		}
		return toReturn;
	}
	
	
	/**
	 * Assumes they're in chronological order. Returns the lowest position
	 * where the dataUnit's creation time is later than start time. 
	 * Returns the array's size if none exist. 
	 * @param startTime
	 * @param toSearch
	 * @return
	 */
	private int findPosOfFirstPktPastTime(double startTime, ArrayList<V> toSearch){
		if(toSearch.size()== 0){
			return 0;
		}
		/*
		 * This search algo could probably use some optimizing (if it's earliest it takes O(n)),
		 * but in most cases we'll be be storing the latest packet, and so putting it at the 
		 * end is actually best case (should be O(1))
		 */
		for(int i = toSearch.size(); i>0; i--){
			if(toSearch.get(i-1).getTimeCreated()<startTime){
				return i;
			}
		}
		return 0; //obviously none in the loop were less than, so it should go at the beginning.  
	}
	
	
	
	/**
	 * Returns a COPY of a list of all TelemDataPackets received
	 * @return
	 */
	public ArrayList<V> getAll(){
		ArrayList<V> toReturn = new ArrayList<V>(this.recallStuffList);
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


	/**
	 * Saves all currently pending packets to the database and closes the fileStream and file. 
	 * Call before closing the program to ensure proper disconnection. 
	 */
	public void saveAndDisconnect() throws IOException {
		if(isDBConnected){ //gotta make sure we don't mess up the db connected state. 
			//check is here so multiple calls to 'save and disconnected'
			//don't throw errors. 
			flushAndSave();
			myFileWriter.close();
		}
		this.isDBConnected = false; 
	}

	/*
	 * Stores the dataunit in the database.
	 * NOTE: generally stores in chronological order, and where multiple dataunits
	 * have the same createdTime (i.e within the same millisecond), in an arbitrary order. 
	 * (non-Javadoc)
	 * @see com.ubcsolar.database.Database#store(com.ubcsolar.common.DataUnit)
	 */
	public void store(V toStore) throws IOException {
		if(this.columnTitles == null){ //means headers haven't been printed out yet
			this.columnTitles = toStore.getCSVHeaderRow();
			this.writingQueue.add(this.columnTitles);
		}
		
		if(toStore.returnsEntireTable()){
			if(this.recallStuffList.size()>=1){ //can't store more than one
				String exceptionError = "Tried to store 2 dataunits that are tables in the same CSV";
				throw new IllegalArgumentException(exceptionError);
			}
		}
		
		putIntoRAM(toStore);
		this.writingQueue.add(toStore.getCSVEntry());
		this.flushAndSave();
		 
	}
	
	/**
	 * Puts the DataUnit into the correct position in the list. 
	 * @param toStore
	 */
	private void putIntoRAM(V toStore) {
		this.recallStuff.put(toStore.getTimeCreated(), toStore);
		int storePos = this.findPosOfFirstPktPastTime(toStore.getTimeCreated(), this.recallStuffList);
		this.recallStuffList.add(storePos, toStore);

	}

	/**
	 * returns the dataunit corresponding to the key.
	 * @param string
	 * @return
	 */
	public TelemDataPacket getDataUnit(String string) {
		// TODO Auto-generated method stub
		return null;
	}

}