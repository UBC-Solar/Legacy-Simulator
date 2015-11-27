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
	//TODO override toHash to make it consistent with .equals();
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
		//TODO double check documentation on hashmap constructor with Hashmap argument. 
		//(wrote this without access to javadoc)
		this.temperatures = new HashMap<String,Integer>(newTemperatures);
		this.cellVoltages = new HashMap<Integer, ArrayList<Float>>(newCellVoltages);
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
		
		for(int i=0; i<1000; i++){ //to force a new time
			for(int j = 0; j<100; j++){
				i--;
				i++;
			}
		}
		//TODO turn this into System.nanoTime(), more accurate. 
		this.timeCreated = System.currentTimeMillis();
		this.speed = newSpeed;
		this.totalVoltage = newTotalVoltage;
		this.temperatures = newTemperatures;
		this.cellVoltages = newCellVoltages;
	}
	public int getSpeed(){
		return speed;
	}
	
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
		return Integer.toString(this.speed) + "\n"
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
		
		if(toCheckAgainst.getClass() != TelemDataPacket.class){
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
			return false; //won't get here, leaving it here for code readibility
		}
		
		if(Math.abs((toCompare.timeCreated - this.timeCreated))>0.000000000000001){
			return false; //timeCreated not the same. 
		}
		
		if(toCompare.speed != this.speed){
			return false; 
		}
		
		if(toCompare.totalVoltage != this.totalVoltage){
			return false;
		}
		
		//TODO ensure that hashMap has an implementation of Equals and 
		//it's not just pointer-checking. 
		if(!toCompare.cellVoltages.equals(this.cellVoltages)){
			return false;
		}
		
		if(!toCompare.temperatures.equals(this.temperatures)){
			return false;
		}
		
		if(toCompare.getCellVoltages().size() != this.cellVoltages.size()){
			return false;
		}
		
		if(toCompare.getTemperatures().size() != this.temperatures.size()){
			return false;
		}
		
		System.out.print(toCompare.getTemperatures().size());
		System.out.println(this.temperatures.size());
		
		/*
		 * If the hashmap's equals only checks pointers (see above todo; don't have access to javadoc right now)
		 * the you can uncomment the methods below to compare values. 
		 */
		/*
		for(int i : this.cellVoltages.keySet()){
			if(!toCompare.cellVoltages.containsKey(i)){
				return false; 
			}
			if(this.cellVoltages.get(i).size() != toCompare.cellVoltages.get(i).size()){
				return false;
			}
			for(int j = 0; j<this.cellVoltages.size(); j++){
				if(Math.abs(this.cellVoltages.get(i).get(j) - toCompare.cellVoltages.get(i).get(j))>0.000000001){
					return false;
				}
			}
		}
		
		for(String i : this.temperatures.keySet()){
			if(!toCompare.temperatures.containsKey(i)){
				return false; 
			}
			if(this.temperatures.get(i) != toCompare.temperatures.get(i)){
				return false;
			}
		}
		
		
		*/
		
		
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