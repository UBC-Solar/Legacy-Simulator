package com.ubcsolar.sim;

/**
This class models the battery. The important features are the temperature and State of Charge.
Should throw errors if it overheats. 
draw determined by the ElectricalController. 
Noah wasn't sure exactly how it interfaces with the ElectricalController, so maybe different
implementation is needed. 
*/

public class Battery{
//---------------CLASS FIElDS-------------------------------
private int temperature; /** temperature of batteries/housing in degrees C */
private double wattsStored; /** the current charge (watts) in the battery*/
private double maxCharge; /**max charge of the battery, in watts. Used to calculate % charge*/ 
//-----------END OF FIELDS, START OF CONSTRUCTORS--------------]

/** copy constructor. 
 * creates a battery with all fields equal to the old one. 
 */
 public Battery(Battery oldBattery){
	/** @todo implement this and the needed getter methods */
 }
 
/** default constructor (no filename). 
 * builds a battery with built-in default model */
public Battery(){
	/** @todo implement default battery and model*/
	temperature = 22; // start at room temperature 
	wattsStored = 100; // start with 100 watts in the tank 
	maxCharge = 500; // .5kw battery 
	Log.write("default battery constructed");
}

/**
 * standard Constructor
 * @param fileName - the file from which to load the model from
*/
public Battery(String fileName){
	Log.write("battery created");
	loadModel(fileName);

}

/** fills in class fields and model from file
 * @param fileName - the file to load from
 */
 private void loadModel(String fileName){
 /** @todo represent and load the battery model here */
 // the following values provided until ^^ gets done. 
 	temperature = 0;
	wattsStored = 100;
	maxCharge = 500; //pulled that number out of my butt. Very small battery.
}
//--------END OF CONSTRUCTOR-TYPE METHODS, START OF CALULATING ONES--------------

/** predicts the next state of the ElectricalController and subclasses, given the arguments.
 * Note: drawing and storing of power is done through other methods. 
 */
public void nextBattery(int time, Environment worldEnviro,Boolean doLog){
	/** @todo implement */
	//check heat
	//anything else to do here?
	Log.write("Watts left: " + wattsStored);
}

/** to avoid over-drawing, this return true if there
 * is that amount, false if there isn't that much in the batteries.
 * @param wattsToCheck - the amount of watts that are wanted to be pulled out
 * @return doesContain - if there is that much energy stored, return true. 
 */
public boolean doesContain(double wattsToCheck){
	if(wattsStored>=wattsToCheck){
		return true;
	}
	else{return false;}
}

/** draws the requested amount from the battery 
 * @param wattsOutput - the amount of watts to remove from the battery
 */
public void draw(double wattsOutput){
	/** @todo implement this. Lower state of charge, check heat? Throw an exception if not enough */
	wattsStored -= wattsOutput;
	temperature += 1; /** @todo do this better? */
}

/** stores the given amount of watts in the battery, or vents it if over capacity.
 * @param wattsToPutIn - the amount of energy (Watts) to store. 
 */
public void store(double wattsToPutIn){
	/** @todo implement. Add to state of charge, check heat? */
	wattsStored += wattsToPutIn;
	temperature += 1; /** @todo implement heat modelling better*/
}

//------------GETTERS AND SETTERS-------------------
/** @todo implement more getters */
/**this method returns the state of charge in %
 * @return the % of charge in the battery
 */
public int getStateOfCharge(){
	return (int)((double)wattsStored/(double)maxCharge)*100;
}

}

