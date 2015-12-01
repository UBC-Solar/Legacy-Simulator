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
	
	return allValues;
}
	private final double timeCreated;
	private final int speed;
	private final int totalVoltage; //used to guesstimate state of charge
	//TODO make these abstract
	private final HashMap<String,Integer> temperatures;
	private final HashMap<Integer,ArrayList<Float>> cellVoltages;
	
	/**
	 * This constructor used if packet was received at an earlier time and needs to be specified.
	 * I.e pulled out of a database. 
	 */
	public TelemDataPacket(int newSpeed, int newTotalVoltage, HashMap<String,Integer> newTemperatures, HashMap<Integer,ArrayList<Float>> newCellVoltages, double timeCreated){
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
	}
	
	/*
	 * Creates identical objects for each entry and adds them to the 
	 * new Map. (Needed to make separate ArrayLists so they couldn't
	 * be changed after creating the TelemDataPacket). 
	 * NOTE: This will create identical maps ONLY if the copyTo map is empty
	 * otherwise will simply add the values.
	 */
	private void copyOverCellVoltages(HashMap<Integer, ArrayList<Float>> copyTo,
			HashMap<Integer, ArrayList<Float>> copyFrom) {
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
	 */
	public TelemDataPacket(int newSpeed, int newTotalVoltage, HashMap<String,Integer> newTemperatures, HashMap<Integer,ArrayList<Float>> newCellVoltages){
		if(newTemperatures == null || newCellVoltages == null){
			throw new NullPointerException("Can't created a DataUnit with a null value");
		}
		 
		this.timeCreated = System.currentTimeMillis();
		this.speed = newSpeed;
		this.totalVoltage = newTotalVoltage;
		this.temperatures = new HashMap<String,Integer>(newTemperatures);
		this.cellVoltages = new HashMap<Integer, ArrayList<Float>>();//(newCellVoltages);
		this.copyOverCellVoltages(cellVoltages,newCellVoltages);
	}
	public int getSpeed(){
		return speed;
	}
	
	/**
	 * 
	 * @return 
	 */
	public double getTimeCreated(){
		return this.timeCreated;
	}
	
	public float getTotalVoltage(){
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
	
	@Override
	public String toString(){
		return this.timeCreated + "\n"
				+ Integer.toString(this.speed) + "\n"
				+ Float.toString(this.totalVoltage) + "\n"
				+ this.temperatures.toString() + "\n"
				+ this.cellVoltages.toString() + "\n";
	
	}
	
	
	@Override
	public boolean equals(Object toCheckAgainst){
		//TODO Tweak this so that it uses the getter methods from the other class
		//in case of extended classes (i.e the null telem data packet).
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
		
		if(toCompare == null){
			return false; //won't get here, but leaving it here for code readibility
		}
		
		//TODO double check this calculation and how to compare doubles. 
		//May switch to System.nanoTime(), is that enough digits?
		if(Math.abs((toCompare.timeCreated - this.timeCreated))>0.000000000000001){
			return false; //timeCreated not the same. 
		}
		
		if(toCompare.speed != this.speed){
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
		//this is a terrible hash, but simple and it will work.
		//Probably not all that much better than a hash function that returns a constant. 
		//TODO come up with a better hash function. Possibly all values added together?
		return this.getSpeed();
	}

	}