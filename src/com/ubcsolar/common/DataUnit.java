/**
 * All bottom-level data units in the program should extend this, so the Database can properly 
 */
package com.ubcsolar.common;

import java.util.List;
import java.util.Map;

public abstract class DataUnit {
	
	/**
	 * Should return the time of creation of the data. 
	 * @return the time of creation in milliseconds. 
	 */
	public abstract double getTimeCreated();
	
	/**
	 * A key/value map that contains every piece of data.
	 * Could use this one day to dynamically handle every piece of data.  
	 * @return
	 */
	public abstract Map<String, ? extends Object> getAllValues();
	

}
