package com.ubcsolar.sim;

public class DefaultCarModel extends CarModel {
	private final double numberOfBatteryPacks = 4;
	private final double batteryCap = 800*numberOfBatteryPacks; //watt hrs
	private final double solarPanelArea = 10; //square meters
	
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

}
