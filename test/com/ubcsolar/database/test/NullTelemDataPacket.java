/**
 * 
 * If you give an empty set for temperatures or cell voltages,
 * this class will save them simply as 'null'. 
 * This class modifies the default behavior of TelemDataPacket,
 * and this lets us test Database objects to see how they'll handle null values,
 * to either ensure they'll throw and exception or silently handle them, 
 * as we'd like. 
 */
package com.ubcsolar.database.test;

import java.util.ArrayList;
import java.util.HashMap;

import com.ubcsolar.common.TelemDataPacket;

public class NullTelemDataPacket extends TelemDataPacket {
	
	
	private HashMap<String,Integer> myTemperatures;
	private HashMap<Integer,ArrayList<Float>> myCellVoltages;
	


	public NullTelemDataPacket(int newSpeed, int newTotalVoltage, HashMap<String, Integer> newTemperatures,
			HashMap<Integer, ArrayList<Float>> newCellVoltages) {
		super(newSpeed, newTotalVoltage,newTemperatures, newCellVoltages);
	
		if(newTemperatures.size() == 0){
			this.myTemperatures = null;
		}else{
			this.myTemperatures = newTemperatures;
		}
		
		if(newCellVoltages.size() == 0){
			this.myCellVoltages = null;
		}else{
			this.myCellVoltages = newCellVoltages;
		}
	}
	
	public NullTelemDataPacket(int newSpeed, int newTotalVoltage, HashMap<String, Integer> newTemperatures,
			HashMap<Integer, ArrayList<Float>> newCellVoltages, double timeInMillis) {
		super(newSpeed, newTotalVoltage,newTemperatures, newCellVoltages, timeInMillis);
	
		if(newTemperatures.size() == 0){
			this.myTemperatures = null;
		}else{
			this.myTemperatures = newTemperatures;
		}
		
		if(newCellVoltages.size() == 0){
			this.myCellVoltages = null;
		}else{
			this.myCellVoltages = newCellVoltages;
		}
	}
	
	
	@Override
	public HashMap<String,Integer> getTemperatures(){
		if(myTemperatures == null){
			return null;
		}
		else{
			return super.getTemperatures();
		}
	}
	
	@Override
	public HashMap<Integer,ArrayList<Float>> getCellVoltages(){
		if(myCellVoltages == null){
			return null;
		}
		else{
			return super.getCellVoltages();
		}
	}

}
