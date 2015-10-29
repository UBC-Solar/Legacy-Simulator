/**
 * Abstract Database object. Concrete database implementations (MySQL, kludgey .csv, etc) 
 * should extend this. 
 */
package com.ubcsolar.database;

import java.io.IOException;

import com.ubcsolar.common.*;

/**
 * @author Noah
 *
 */
public abstract class Database {

	/**
	 * 
	 */
	public Database() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * Flush and save everything to database. To be used in case of program crash,
	 * it will run this. 
	 * @throws IOException 
	 */
	abstract public void flushAndSave() throws IOException;
	
	/**
	 * Save everything and close/cut ties to Database. I.e close the File if a .csv
	 * @throws IOException 
	 */
	abstract public void saveAndDisconnect() throws IOException;
	
	/**
	 * Store a DataUnit in the database
	 * @param toStore - the DataUnit to be stored in the DB. 
	 */
	abstract public void store(DataUnit toStore);
	
	/**
	 * Get an element
	 * @param Item's unique key
	 * @return the DataUnit belonging to the key. 
	 */
	abstract public DataUnit get(String key);
	
}
