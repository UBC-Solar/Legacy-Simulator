package com.ubcsolar.car;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataReceived {
	public int speed;
	public float totalVoltage;
	public int stateOfCharge;
	public Map<String,Integer> temperatures = new HashMap<String,Integer>();
	public Map<Integer,ArrayList<Float>> cellVoltages = new HashMap<Integer,ArrayList<Float>>();
	
	public String toString(){
		return Integer.toString(this.speed) + "\n"
				+ Float.toString(this.totalVoltage) + "\n"
				+ this.temperatures.toString() + "\n"
				+ this.cellVoltages.toString() + "\n";
	}
}
