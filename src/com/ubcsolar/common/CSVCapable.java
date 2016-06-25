package com.ubcsolar.common;

public interface CSVCapable {
	
	/**
	 * turns the class fields into an entry for a csv file
	 * see returnsEntireTable for info on row versus table
	 * @return the row as a string
	 */
	String getCSVEntry();
	
	/**
	 * gets the column headings as a csv row
	 * @return the row as a string
	 */
	String getCSVHeaderRow();
	
	/**
	 * if the CSV output is multiline rather than a single line
	 * @return 
	 */
	boolean returnsEntireTable ();
	
	

}
