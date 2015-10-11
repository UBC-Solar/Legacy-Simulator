package com.ubcsolar.car;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TelemDataPacket {
	private double timeCreated;
	private int speed;
	private int totalVoltage; //used to guesstimate state of charge
	//TODO make these abstract
	private HashMap<String,Integer> temperatures;
	private HashMap<Integer,ArrayList<Float>> cellVoltages;
	
	//NOAH: I want to make this a consructor, but XbeeDataReceiver relies on the default constructor and 
	//public variables. 
	public TelemDataPacket(int newSpeed, int newTotalVoltage, HashMap<String,Integer> newTemperatures, HashMap<Integer,ArrayList<Float>> newCellVoltages){
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
	
	public String toString(){
		return Integer.toString(this.speed) + "\n"
				+ Float.toString(this.totalVoltage) + "\n"
				+ this.temperatures.toString() + "\n"
				+ this.cellVoltages.toString() + "\n";
	}
}
