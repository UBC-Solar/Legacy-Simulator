package com.ubcsolar.sim;

/**
This class models the battery. The important features are:
- display the battery's temperature and state of charge at set time iterations
- show error if it overheats
- show error if it overcharges

Drawing & storing power is done through other methods.

Interacts mainly with the electric controller:
- lets the electric controller know how long the batteries could charge for, until it reaches 100%
- lets the electric controller know what current the batteries can provide, given the voltage and time period demanded
*/


public class Battery{
	
//---------------------------------------CLASS FIElDS--------------------------------------------------------------------------------------
private int temperature; /** temperature of batteries in degrees C */													
private double maxChargeCapacity; /**max charge of the battery (in amp-hours) */ 
private double batteryVoltage; /** nominal voltage of battery (in volts) */
private double storedEnergy; /** the current remaining electric energy in the battery (in watt-hours) */
private double maxStoredEnergy; /** electric energy stored in the battery when it's 100% charged */
private double time;
//-----------------------------------------------------------------------------------------------------------------------------------------


//------------------------------------SETTER & GETTER METHODS------------------------------------------------------------------------------
/** Constructs a default battery, at 100% charged state.
 *  Values for maxChargeCapacity & batteryVoltage are taken from manufacturer's product data sheet.
 *  @todo Confirm this assumption: total stored electric energy in batteries is given by:  Ah*V = Wh		(charge capacity*voltage = energy)
 */
public Battery(){
	maxChargeCapacity = 220;				 
	batteryVoltage = 40.15;
	maxStoredEnergy = maxChargeCapacity*batteryVoltage;
	storedEnergy = maxStoredEnergy;
	Log.write("Default battery created.");
}

/** Reuturns the battery's voltage. */
public double getBatteryVoltage(){
	return batteryVoltage;
}

/** Returns the current state of charge of battery, as a percentage. */
public int getStateOfCharge(){
	return (int)((double)storedEnergy/(double)maxStoredEnergy)*100; 
}

/** Predict the next state of the battery. 
 * @param time - the time interval (in seconds)
 * @todo need to finish this method
 */
public void nextBattery(double time, Environment worldEnviro, Boolean doLog){
}

/** @todo get properties and model from file */
public Battery(String fileName){
	Log.write("Battery created");
}
//------------------------------------------------------------------------------------------------------------------------------------------


//-----------------------------------------CALCULATION METHODS------------------------------------------------------------------------------
/** Calculates the maximum time the battery could recharge for, before it overcharges. (in seconds)
 * Should display error if the specified charge is over the battery's max charge capacity
 * @param voltage - specified by the electrical controller (in volts)
 * @param currentRecharge - the current the battery will be charged at, specified by the electrical controller (in amps)
 * @todo Confirm the battery's max charge capacity, and confirm that it absolutely cannot charge at a capacity that exceeds it
 * @todo Confirm this assumption: the charging time is given by: (maxWh-currentWh)/(V*A)
 * @todo Incorporate charging efficiency, temperature, other factors that will affect the charging time
 * */
public double getMaxRechargeTime(double voltage, double currentRecharge){
	double rechargeTime;
	if (currentRecharge > maxChargeCapacity){
		Log.write("Cannot exceed max charge capactiy");
		return -1.0;				
	}
	else{
		rechargeTime =(maxStoredEnergy-storedEnergy)/(voltage*currentRecharge )*3600;
		return rechargeTime;
	}
}

/** Calculates what current the battery can provide (in amps), considering it's current state of charge.
 * @param voltage - specified by the electrical controller
 * @param chargeTime - time period (in seconds) for which the battery will be providing this current; specified by electrical controller
 * @todo Confirm this assumption: Wh/(V*t) = A
 * @todo Incorporate charging efficiency, temperature, other factors that will be of influence
 * */
public double getCurrent(double voltage, double chargeTime){ 
	double currentCharge;
	currentCharge = storedEnergy/(chargeTime/3600 * voltage);
	return currentCharge;
}

public double heatFromCharge(double current, double chargeTime){
	double heatFromCharge=0;
	// TO DO: need calculations here
	// need to consider ambient temperature, battery's temperature, and the heat produced by the adjacent batteries in the pack
	return heatFromCharge;
}

public void storeEnergy(double time, double current, double voltage){					// Store energy (in watt-hours)
	storedEnergy = storedEnergy + (current*voltage*time)/3600;	
}

public void drawEnergy(double time, double current, double voltage){					// Draw energy (in watt-hours)
	storedEnergy = storedEnergy - (current*voltage*time)/3600;
}
//-----------------------------------------------------------------------------------------------------------------------------------------


}

