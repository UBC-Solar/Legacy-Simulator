package com.ubcsolar.sim;

import java.awt.Color;

public class DefaultCarModel extends CarModel {
	private final double numberOfBatteryPacks = 4;
	private final int numberOfPackCells= 10;
	private final double batteryCap = 800*numberOfBatteryPacks; //watt hrs
	private final double solarPanelArea = 10; //square meters
	
	//might need to change later. Or add more threshold for each part of the car
	private final double midTempThreshold = 50; 
	private final double maxTempThreshold = 100;
	private final double criticalStateOfChargeTreshold = 10; // %
	private final double midStateOfChargeTreshold = 20; // %
	private final Color dark_green = new Color(19, 168, 58);
	
	@Override
	public double getMaxBatteryCap() {
		// TODO Auto-generated method stub
		return batteryCap;
	}
	
	public int getPackCells()
	{
		return numberOfPackCells;
	}

	@Override
	public double getSolarPanelArea() {
		// TODO Auto-generated method stub
		return solarPanelArea;
	}
	
	public double getMidTempTreshold (){
		return midTempThreshold;
	}

	public double getMaxTempTreshold (){
		return maxTempThreshold;
	}
	
	public double getMidStateOfChargeTreshold (){
		return midStateOfChargeTreshold;
	}

	public double getCriticalStateOfChargeTreshold (){
		return criticalStateOfChargeTreshold;
	}
	public Color getGreen (){
		return dark_green;
	}
	
}
