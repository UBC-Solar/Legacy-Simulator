/**
 * This class implements a basic data storage system using a CSV file. 
 * Is supposed to be a basic implementation to stand in until we get a working
 * MySQL or proper database. Saves all data to a Comma Seperated Value (CSV) file 
 * so it can be opened in Excel for later analysts. 
 */
package com.ubcsolar.database;

import java.io.FileWriter;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.Queue;

import com.ubcsolar.common.DataUnit;
import com.ubcsolar.common.LogType;
import com.ubcsolar.common.SolarLog;

public class CSVDatabase extends Database {
	
	Queue<String> writingQueue; //will read from here and then write. 
	FileWriter myFileWriter; //used to write. 
	
	/**
	 * Constructor; Create and set up database 
	 * @param filename - filename for the .csv
	 * @throws IOException - if it can't create the file for some reason. 
	 */
	public CSVDatabase(String filename) throws IOException {
		super();
		setup(filename);
	}
	
	private void setup(String filename) throws IOException{
		//TODO: add a check for the '.csv' so we don't duplicate it. 
		SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(),
				"CSV Database created with name " + filename + ".csv");
		myFileWriter = new FileWriter(filename + ".csv");
		writingQueue = new PriorityQueue<String>();
		setUpTables();
	}
	
	/**
	 * Build a standard CSV database with the system time as the filename.
	 * @throws IOException
	 */
	public CSVDatabase() throws IOException {
		super();
		setup(""+System.currentTimeMillis()); //gaurenteed to be a unique filename. (unless you're making more than 1 per ms
											//in which case you have other problems to worry about)
	}

	
	
	/*
	 * This method is used to generate the queries needed to set up the tables
	 * in the Database. For this CSV it will just be the coloumn headers. 
	 */
	private void setUpTables() throws IOException{
		//Currently assuming a .csv file
		String tableSetup;
		tableSetup = "Entry,Time,Speed,TtlVltg,SOC,Temps";
		writingQueue.add(tableSetup);
		flushAndSave();
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
	public void flushAndSave() throws IOException{
		while(this.writingQueue.size()>0){
			myFileWriter.write(writingQueue.remove());
		}
		myFileWriter.flush();
		
	}
	


	@Override
	public void saveAndDisconnect() throws IOException {
		flushAndSave();
		myFileWriter.close();
	}

	@Override
	public void store(DataUnit toStore) {
		// TODO Auto-generated method stub

	}

	@Override
	public DataUnit get(String key) {
		// TODO Auto-generated method stub
		return null;
	}

}
