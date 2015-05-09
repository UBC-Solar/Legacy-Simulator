package com;

public class Battery{

//--------------------------------------------------------------------------------------------------------------
private int temperature;	// temperature of batteries in degrees C
private double currentCharge;	// current charge (watts) in batteries
private double maxCharge;	// max charge of battery (watts); static; used to calculate % charge
private double percentCharge;
private double time;

// CONSTRUCTORS--------------------------------------------------------------------------------------------------------------------------

public void createBattery(){											// Builds a default battery; should be called out at time=0.
	maxCharge = 500;
	currentCharge = maxCharge;
	percentCharge = currentCharge/maxCharge*100;
	temperature = 23;
	//Log.write("Default battery created.");
	//Log.write("Current charge: " + percentCharge);
}

public void nextBattery(){												// Creates a next battery; runs every iteration of ?? seconds.
	// reset currentCharge, temperature, percentCharge
	// log these to the log class/files
}

public double getCurrentCharge(double currentCharge){					// Returns current state of charge (as a percentage).
	return currentCharge;
}

public double getMaxRechargeTime(double voltage, double currentRecharge){	// Calculates the maximum time the battery could recharge for before it overcharges.
	double maxRechargeTime;												// Takes in the voltage & current from electric controller as inputs.
																			// Returns maxRechargeTime in seconds.
	return maxRechargeTime=0;
}

public double getCurrent(double voltage, double chargeTime){			// Calculates the current the battery can provide, considering it's current state of charge, and
	double current_charge;												// the voltage & time period required by the electric controller.
	
	return current_charge=0;
}

public double heatFromCharge(double current, double chargeTime){
	double heatFromCharge;
	// insert calculations here
	return heatFromCharge=0;
}

}