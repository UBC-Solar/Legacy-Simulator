package com.ubcsolar.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import com.github.dvdme.ForecastIOLib.FIODataPoint;

public class SimFrame extends DataUnit{ //implements Runnable{
	
	private DateFormat actualDateFormat = new SimpleDateFormat("HH:mm:ss.SSS"); //time format. ss = seconds, SSS = ms
	//couldn't manage to format milliseconds in a way that Excel can handle as time
	//so just generated a second column to be able to graph it properly. 
	private DateFormat excelDateFormat = new SimpleDateFormat("HH:mm:ss"); //time format. ss = seconds, SSS = ms
	
	private final static String reportSeparation = ",,";
	public final static String classCSVHeaderRow = "TimeCrt, ExcelTimeCrt, TimeRep, ExcelTimeRep" +reportSeparation
			+ ForecastReport.classCSVHeaderRow//TODO
			//+ reportSeparation
			+ TelemDataPacket.classCSVHeaderRow + reportSeparation + LocationReport.classCSVHeaderRow;
	/**
	 * turns the class fields into an entry for a csv file
	 * see returnsEntireTable for info on row versus table
	 * @return the row as a string
	 */
	public String getCSVEntry()
	{
		StringBuilder frameToPrint = new StringBuilder("");
		//frameToPrint += actualDateFormat.format (this.getTimeCreated())+ "," ;
		//frameToPrint += excelDateFormat.format(this.getTimeCreated()) + "," ;
		frameToPrint.append(actualDateFormat.format (this.getRepresentedTime())+ ",") ;
		frameToPrint.append(excelDateFormat.format(this.getRepresentedTime()) + reportSeparation) ;
		//frameToPrint += this.getForecast().getCSVEntry() + reportSeparation; //TODO
		frameToPrint.append(this.getCarStatus().getCSVEntry() + reportSeparation);
		frameToPrint.append(this.getGPSReport().getCSVEntry() + ",");
		return frameToPrint.toString();
	
	
	}
	
	/*
	@Override
	public void run() {
		String frameToPrint = "";
		frameToPrint += actualDateFormat.format (this.getRepresentedTime())+ "," ;
		frameToPrint += excelDateFormat.format(this.getRepresentedTime()) + reportSeparation ;
		//frameToPrint += this.getForecast().getCSVEntry() + reportSeparation; //TODO
		frameToPrint += this.getCarStatus().getCSVEntry() + reportSeparation;
		frameToPrint += this.getGPSReport().getCSVEntry() + ",";
		
		String frameEntry = ""+frame;
		parent.notifyOfResult(frameEntry,frameToPrint);

		
	}
	
	private int frame;
	private SimulationReport parent;
	SimFrame(int entry,SimulationReport temp ){
		this.frame=entry;
		parent=temp;
		
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
		return false;
	}

	
	private  final long timeCreated; //the time this frame was created, not the time it represents. 
	private  final long representedTime;
	private  final FIODataPoint forecast;
	private  final TelemDataPacket carStatus;
	private  final LocationReport GPSReport;
	
	
	public SimFrame(FIODataPoint forecast, TelemDataPacket carStatus, LocationReport GPSReport, long timeRepresented) {
		this.forecast = forecast;
		this.carStatus = carStatus;
		this.GPSReport = GPSReport;	
		this.representedTime = timeRepresented;
		this.timeCreated = System.currentTimeMillis();
	}
	public SimFrame(FIODataPoint forecast, TelemDataPacket carStatus, LocationReport GPSReport, long timeRepresented, long timeCreated) {
		this.forecast = forecast;
		this.carStatus = carStatus;
		this.GPSReport = GPSReport;
		this.representedTime = timeRepresented;
		this.timeCreated = timeCreated;	
	}

	@Override
	public double getTimeCreated() {
		return this.timeCreated;
	}

	@Override
	public Map<String, ? extends Object> getAllValues() {
		// TODO Auto-generated method stub
		return null;
	}

	public LocationReport getGPSReport() {
		return GPSReport;
	}

	public TelemDataPacket getCarStatus() {
		return carStatus;
	}

	public FIODataPoint getForecast() {
		return forecast;
	}
	public long getRepresentedTime() {
		return representedTime;
	}



}
