package com.ubcsolar.common;

import java.util.List;
import java.util.Map;

public class SimulationReport extends DataUnit {
	
	public final static String classCSVHeaderRow = "simulation frame";
	/**
	 * turns the class fields into an entry for a csv file
	 * see returnsEntireTable for info on row versus table
	 * @return the row as a string
	 */
	public String getCSVEntry()
	{
		int frame=0;
		String simTable="";
		simTable += frame +",";
		for (SimFrame f : this.getSimFrames())
		{
			simTable += ","+ f.getCSVEntry() +"\r\n";
		}
		return simTable;
	}
	
	/**
	 * gets the column headings as a csv row
	 * @return the row as a string
	 */
	public String getCSVHeaderRow()
	{
		return classCSVHeaderRow;
	}
	
	/**
	 * if the CSV output is multiline rather than a single line
	 * @return 
	 */
	public boolean returnsEntireTable ()
	{
		return true;
	}

	
	private final List<SimFrame> simData;
	public SimulationReport(List<SimFrame> simData, String info) {
		this.simData = simData;
	}
	
	public List<SimFrame> getSimFrames(){
		return simData;
	}
	
	@Override
	public double getTimeCreated() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<String, ? extends Object> getAllValues() {
		// TODO Auto-generated method stub
		return null;
	}

}
