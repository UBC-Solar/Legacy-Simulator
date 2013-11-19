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
public void defaultBattery(){															// Constructs a default battery. Should be called out at time = 0.
	maxChargeCapacity = 20;														 
	batteryVoltage = 3.65;																// Values for charge capacity & voltage are taken from manufacturer's product data sheet
	maxStoredEnergy = maxChargeCapacity*batteryVoltage;									// Working under the assumption that energy is give by Ah*V = Wh
	storedEnergy = maxStoredEnergy;														// Assuming battery is 100% charged
	Log.write("Default battery created.");
}

public int getStateOfCharge(){															// Returns the current state of charge as a percentage.
	return (int)((double)storedEnergy/(double)maxStoredEnergy)*100; 
}

public void nextBattery(double time, Environment worldEnviro, Boolean doLog){
}
//------------------------------------------------------------------------------------------------------------------------------------------


//-----------------------------------------CALCULATION METHODS------------------------------------------------------------------------------
public double getMaxRechargeTime(double voltage, double currentRecharge){				// Calculates the maximum time the battery could recharge for before it overcharges.
	double rechargeTime;																// Takes in the voltage & current from electric controller as inputs. Returns time in seconds.
	if (currentRecharge > maxChargeCapacity){
		Log.write("Cannot exceed max charge capactiy");									// Display error when the specified charging current exceed the max charging capacity (20A)
		return -1.0;																		// Might need to omit this?? Battery could technically accept up to 112.5A for 10s (from product's data sheet)
	}
	else{
		rechargeTime =(maxStoredEnergy-storedEnergy)/(voltage*currentRecharge )*3600;	// Using: (maxWh - currentWh)/(V*A) = hrs; then converted to seconds.  
		return rechargeTime;
	}
}

public double getCurrent(double voltage, double chargeTime){							// Calculates the current the battery can provide, considering it's state of charge at the time. 
	double currentCharge;																// Voltage & time period in seconds (specified by the electric controller) are inputs.
	currentCharge = storedEnergy/(chargeTime/3600 * voltage);							// Using: Wh/(V*t) = A
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
//--------------------------------------------------------------------------------------------------------------------------------------------


}

