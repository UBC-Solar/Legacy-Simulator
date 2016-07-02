package com.ubcsolar.common;

import java.util.List;
import java.util.Map;

public class SimulationReport extends DataUnit {
	
	//private String simTable;
	private Map<String, String> map;
	public final static String classCSVHeaderRow = "simulation frame" + "," + SimFrame.classCSVHeaderRow;
	/**
	 * turns the class fields into an entry for a csv file
	 * see returnsEntireTable for info on row versus table
	 * @return the row as a string
	 */
	public String getCSVEntry()
	{
		StringBuilder simTable= new StringBuilder("");
		int frame=0;
				
		for (SimFrame f : this.getSimFrames())
		{
	//		Thread t= new Thread(new SimFrame(frame,this,f));
	//		t.start();
			
			simTable.append(frame+ ","+ f.getCSVEntry() +"\r\n");
			frame++;
		}
		
	//	System.out.println("Start of loop");

	//	if(this.getSimFrames()!=null){
	//		while (map.size()<= this.getSimFrames().size());
	//	}
	//	System.out.println("DONE");

		return simTable.toString();
	}
	/*
	public void notifyOfResult(String frame, String frameToPrint) {
		map.put(frame, frameToPrint);
		
		if(this.getSimFrames()!=null){
			if (map.size()== this.getSimFrames().size()){
				int f=0;
				String key="";
				for(int i=0;i<map.size();i++){
					key=""+f;
					simTable += key+ ","+ map.get(key)+"\r\n";
					f++;
					System.out.println("it's the frame : "+key);
				}
				map.put("last", "entry");
			}
		}
	}*/
	
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
