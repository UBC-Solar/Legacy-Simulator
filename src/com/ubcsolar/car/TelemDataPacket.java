package com.ubcsolar.car;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TelemDataPacket {
	public int speed;
	public int totalVoltage;
	public int stateOfCharge;
	public Map<String,Integer> temperatures = new HashMap<String,Integer>();
	public Map<Integer,ArrayList<Float>> cellVoltages = new HashMap<Integer,ArrayList<Float>>();
	
	//NOAH: I want to make this a consructor, but XbeeDataReceiver relies on the default constructor and 
	//public variables. 
	public void LoadNewData(int newSpeed, int newTotalVoltage,int newStateOfCharge,
			Map<String,Integer> newTemperatures, Map<Integer,ArrayList<Float>> newCellVoltages){
		this.speed = newSpeed;
		this.totalVoltage = newTotalVoltage;
		this.stateOfCharge = newStateOfCharge;
		this.temperatures = newTemperatures;
		this.cellVoltages = newCellVoltages;
	}
	public int getSpeed(){
		return speed;
	}
	
	public float getTotalVoltage(){
		return totalVoltage;
	}
	
	public int getStateOfCharge(){
		return stateOfCharge;
	}
	
	public Map<String,Integer> getTemperatures(){
		Map<String,Integer> copyTemperatures = new HashMap<String,Integer>();
		copyTemperatures.putAll(temperatures);
		return copyTemperatures;
	}
	
	public Map<Integer,ArrayList<Float>> getCellVoltages(){
		Map<Integer,ArrayList<Float>> copyCellVoltages = new HashMap<Integer,ArrayList<Float>>();
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
