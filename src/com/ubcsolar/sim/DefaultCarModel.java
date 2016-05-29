package com.ubcsolar.sim;

public class DefaultCarModel extends CarModel {
	private final double numberOfBatteryPacks = 4;
	private final double batteryCap = 800*numberOfBatteryPacks; //watt hrs
	private final double solarPanelArea = 10; //square meters
	private final double midTempThreshold = 100;
	private final double maxTempThreshold = 400;
	
	
	@Override
	public double getMaxBatteryCap() {
		// TODO Auto-generated method stub
		return batteryCap;
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
}
