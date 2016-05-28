package com.ubcsolar.sim;

public abstract class CarModel {
	
	/**
	 * returns the car's battery max cap in watts
	 * @return
	 */
	public abstract double getMaxBatteryCap();
	
	/**
	 * the car's solar panel area in square meters
	 * @return
	 */
	public abstract double getSolarPanelArea();

}
