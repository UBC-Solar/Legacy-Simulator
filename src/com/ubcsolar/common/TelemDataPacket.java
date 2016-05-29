package com.ubcsolar.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TelemDataPacket extends DataUnit{

	private DateFormat actualDateFormat = new SimpleDateFormat("HH:mm:ss.SSS"); //time format. ss = seconds, SSS = ms
	//couldn't manage to format milliseconds in a way that Excel can handle as time
	//so just generated a second column to be able to graph it properly. 
	private DateFormat excelDateFormat = new SimpleDateFormat("HH:mm:ss"); //time format. ss = seconds, SSS = ms
	
	// the whole thing is so wired. :D TODO ask Noah
	public final static String classCSVHeaderRow = "RealTime,ExcelTime,Speed,StateOfCharge,BMSTmp,MotorTmp,Pck0Tmp,Pck1Tmp,Pck2Tmp,Pck3Tmp,TtlVltg,"
			+ "Pck0Cl1Vltg,Cl2Vltg,Cl3Vltg,Cl4Vltg,Cl5Vltg,C62Vltg,Cl7Vltg,Cl8Vltg,Cl9Vltg,Cl10Vltg,"
			+ "Pck1Cl1Vltg,Cl2Vltg,Cl3Vltg,Cl4Vltg,Cl5Vltg,C62Vltg,Cl7Vltg,Cl8Vltg,Cl9Vltg,Cl10Vltg,"
			+ "Pck2Cl1Vltg,Cl2Vltg,Cl3Vltg,Cl4Vltg,Cl5Vltg,C62Vltg,Cl7Vltg,Cl8Vltg,Cl9Vltg,Cl10Vltg,"
			+ "Pck3Cl1Vltg,Cl2Vltg,Cl3Vltg,Cl4Vltg,Cl5Vltg,C62Vltg,Cl7Vltg,Cl8Vltg,Cl9Vltg,Cl10Vltg";

	/**
	 * turns the class fields into an entry for a csv file
	 * see returnsEntireTable for info on row versus table
	 * @return the row as a string
	 */
	public String getCSVEntry()
	{
		HashMap<String, Integer> temperatures = this.getTemperatures();
		HashMap<Integer, ArrayList<Float>> voltages = this.getCellVoltages();
		String toPrint = "";
		
	
		toPrint += actualDateFormat.format(this.getTimeCreated()) + ",";
		toPrint += excelDateFormat.format(this.getTimeCreated()) + ",";
		
		toPrint += this.getSpeed() + ",";
		toPrint += this.getStateOfCharge() +",";
		toPrint += temperatures.get("bms")  + ","; //if the temperature calls return 'null', so be it.
												  // it can be written as such to the DB. 
		toPrint += temperatures.get("motor") + ",";
		toPrint += temperatures.get("pack0") + ",";
		toPrint += temperatures.get("pack1") + ",";
		toPrint += temperatures.get("pack2") + ",";
		toPrint += temperatures.get("pack3") + ",";
		toPrint += this.getTotalVoltage() + ",";
		
		//assumes that they have been loaded with the standard number of voltage entries
		//NOTE: May need to modify this if you change the number of cells on the car, 
		//or the amount per pack.
		int expectedNumOfCells = 10;
				
		for(int i = 0; i<4; i++){ 
			if(voltages.get(i) == null){ //will need to offset this so the rest are in position
				toPrint += this.numberOfCommas(expectedNumOfCells);
			}
			else{
				for(Float f : voltages.get(0)){
					toPrint += f + ",";
				}
			}
		}
				
				return toPrint;
	}
	
	private String numberOfCommas(int numberOfCommas){
		String toReturn = "";
		for(int i=0; i<numberOfCommas; i++){
			toReturn += ',';
		}
		
		return toReturn;
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
		return false;
	}

@Override
public Map<String, Object> getAllValues() {
	HashMap<String, Object> allValues = new HashMap<String, Object>();
	allValues.put("speed", this.getSpeed());
	allValues.put("total voltage", this.getTotalVoltage());
	allValues.put("temperatures", this.getTemperatures());
	allValues.put("cell voltages", this.getCellVoltages());
	allValues.put("state of charge", this.getStateOfCharge());
	
	return allValues;
}
	private final double timeCreated;
	private final double speed;
	private final int totalVoltage; //used to guesstimate state of charge
	private final HashMap<String,Integer> temperatures;
	private final HashMap<Integer,ArrayList<Float>> cellVoltages;
	private final double stateOfCharge;
	
	/**
	 * This constructor used if packet was received at an earlier time and needs to be specified.
	 * I.e pulled out of a database. 
	 */
	public TelemDataPacket(double newSpeed, int newTotalVoltage, Map<String,Integer> newTemperatures, Map<Integer,ArrayList<Float>> newCellVoltages, double stateOfCharge2, double timeCreated){
		if(newTemperatures == null || newCellVoltages == null){
			throw new NullPointerException("Can't created a DataUnit with a null value");
		}
		this.timeCreated = timeCreated;
		this.speed = newSpeed;
		this.totalVoltage = newTotalVoltage;
		//Don't want changes to the objects after construction to affect them. 
		this.temperatures = new HashMap<String,Integer>(newTemperatures);
		this.cellVoltages = new HashMap<Integer, ArrayList<Float>>();//(newCellVoltages);
		this.copyOverCellVoltages(cellVoltages,newCellVoltages);
		this.stateOfCharge= stateOfCharge2;
	}
	
	/*
	 * Creates identical objects for each entry and adds them to the 
	 * new Map. (Needed to make separate ArrayLists so they couldn't
	 * be changed after creating the TelemDataPacket). 
	 * NOTE: This will create identical maps ONLY if the copyTo map is empty
	 * otherwise will simply add the values.
	 */
	private void copyOverCellVoltages(Map<Integer, ArrayList<Float>> copyTo,
			Map<Integer, ArrayList<Float>> copyFrom) {
		for(Integer key : copyFrom.keySet()){
			if(copyFrom.get(key) == null){
				copyTo.put(key, new ArrayList<Float>());
			}
			else{
			copyTo.put(key, new ArrayList<Float>(copyFrom.get(key)));
			}
		}
		
	}

	/**
	 * 
	 * @param newSpeed speed of the car
	 * @param newTotalVoltage total voltage of the car
	 * @param newTemperatures map of temperatures of the car
	 * @param newCellVoltages a map of cell voltages.
	 * @param newStateOfCharge state of charge of the car
	 */
	public TelemDataPacket(double newSpeed, int newTotalVoltage, Map<String,Integer> newTemperatures, Map<Integer,ArrayList<Float>> newCellVoltages, double newStateOfCharge){
		if(newTemperatures == null || newCellVoltages == null){
			throw new NullPointerException("Can't created a DataUnit with a null value");
		}
		 
		this.timeCreated = System.currentTimeMillis();
		this.speed = newSpeed;
		this.totalVoltage = newTotalVoltage;
		this.temperatures = new HashMap<String,Integer>(newTemperatures);
		this.cellVoltages = new HashMap<Integer, ArrayList<Float>>();//(newCellVoltages);
		this.copyOverCellVoltages(cellVoltages,newCellVoltages);
		this.stateOfCharge= newStateOfCharge;
	}
	public double getSpeed(){
		return speed;
	}
	
	/**
	 * 
	 * @return 
	 */
	public double getTimeCreated(){
		return this.timeCreated;
	}
	
	public int getTotalVoltage(){
		return totalVoltage;
	}
		
	public HashMap<String,Integer> getTemperatures(){
		HashMap<String,Integer> copyTemperatures = new HashMap<String,Integer>();
		copyTemperatures.putAll(temperatures);
		return copyTemperatures;
	}
	
	public HashMap<Integer,ArrayList<Float>> getCellVoltages(){
		HashMap<Integer,ArrayList<Float>> copyCellVoltages = new HashMap<Integer,ArrayList<Float>>();
		for(Integer key : cellVoltages.keySet()){
			copyCellVoltages.put(key, new ArrayList<Float>(cellVoltages.get(key)));
		}
		return copyCellVoltages;
	}
	public double getStateOfCharge(){
		return stateOfCharge;
	}
	
	@Override
	public String toString(){
		return this.timeCreated + "\n"
				+ Double.toString(this.speed) + "\n"
				+ Float.toString(this.totalVoltage) + "\n"
				+ this.temperatures.toString() + "\n"
				+ this.cellVoltages.toString() + "\n";
	
	}
	
	
	@Override
	public boolean equals(Object toCheckAgainst){
		if(super.equals(toCheckAgainst)){
			return true; //shortcut: if they're the same object, they
						//must be equal
		}
		
		if(!(toCheckAgainst instanceof TelemDataPacket)){
			return false; 
		}
		TelemDataPacket toCompare; 
		try{
		toCompare = (TelemDataPacket) toCheckAgainst;
		}
		catch(Exception e){//casting exception??
			return false; //if it fails at casting, obviously not a TelemDataPacket and therefore not equal. 
		}
		
		/*
		if(toCompare == null){
			return false; //won't get here, but leaving it here for code readibility
		}*/ //commented out to get rid of the Eclipse warnings. 
		
		if(Math.abs((toCompare.timeCreated - this.timeCreated))>0.000000000000001){
			return false; //timeCreated not the same. 
		}
		
		if( Math.abs(toCompare.speed - this.speed)>0.000000000000001){ 
			return false; 
		}
		
		if(toCompare.totalVoltage != this.totalVoltage){
			return false;
		}
		
		if(!toCompare.cellVoltages.equals(this.cellVoltages)){
			return false;
		}
		
		if(!toCompare.temperatures.equals(this.temperatures)){
			return false;
		}
		
		return true; //Got here after all the checks, must be the same. 
		
	}
	
	@Override
	public int hashCode(){
		int hashCode = 0;
		hashCode += this.cellVoltages.hashCode();
		hashCode += this.temperatures.hashCode();
		hashCode += this.totalVoltage;
		hashCode += this.speed*100; //wil be the main thing different between packs. 
		return hashCode;
	}

	}