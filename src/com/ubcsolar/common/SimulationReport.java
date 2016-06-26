package com.ubcsolar.common;

import java.util.List;
import java.util.Map;

public class SimulationReport extends DataUnit {
	
	public final static String classCSVHeaderRow = "simulation frame" + "," + SimFrame.classCSVHeaderRow;
	/**
	 * turns the class fields into an entry for a csv file
	 * see returnsEntireTable for info on row versus table
	 * @return the row as a string
	 */
	public String getCSVEntry()
	{
		int frame=0;
		String simTable="";
		for (SimFrame f : this.getSimFrames())
		{
			simTable += frame+ ","+ f.getCSVEntry() +"\r\n";
			frame++;
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
	private final Map<GeoCoord, Double> manuallyRequestedSpeeds;
	
	
	public SimulationReport(List<SimFrame> simData, Map<GeoCoord, Double> requestedSpeeds, String info) {
		this.simData = simData;
		this.manuallyRequestedSpeeds = requestedSpeeds;
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

	public Map<GeoCoord, Double> getManuallyRequestedSpeeds() {
		return manuallyRequestedSpeeds;
	}

}
