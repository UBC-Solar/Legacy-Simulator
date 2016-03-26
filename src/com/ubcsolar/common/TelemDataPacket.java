package com.ubcsolar.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TelemDataPacket extends DataUnit{

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
	private final int stateOfCharge;
	
	/**
	 * This constructor used if packet was received at an earlier time and needs to be specified.
	 * I.e pulled out of a database. 
	 */
	public TelemDataPacket(double newSpeed, int newTotalVoltage, Map<String,Integer> newTemperatures, Map<Integer,ArrayList<Float>> newCellVoltages, int newStateOfCharge, double timeCreated){
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
		this.stateOfCharge= newStateOfCharge;
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
	public TelemDataPacket(double newSpeed, int newTotalVoltage, Map<String,Integer> newTemperatures, Map<Integer,ArrayList<Float>> newCellVoltages, int newStateOfCharge){
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
	public int getStateOfCharge(){
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