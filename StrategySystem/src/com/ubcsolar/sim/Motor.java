package com.ubcsolar.sim;
/** This class models the motor. 
It holds the torque curves, and calculates how fast
it spins based on the power going into it. 
Also models heat to make sure we don't cook it.
Ideally, it would be similar to Stanford's panel sim (http://solarcar.stanford.edu/design/systems/strategy/)
except for motors instead.
Noah wasn't sure how the motor interfaced with the ElectricalController, so may have to redo
how they interact (currently passing watts, may need to pass voltage and current) 
*/
/** @todo figure out how to model regen */

public class Motor{
//---------------CLASS FIElDS-------------------------------
private double force;		/** current force exerted on the motor **/
private double temperature;	/** the temp. of the motor (Celsius). Don't want to overheat now. */
private double voltage;		/** voltage that the motor is operating at **/
private double current;		/** current that the motor is operating at **/
private double radius; 		/** radius of the wheel **/
//-----------END OF FIELDS, START OF CONSTRUCTORS--------------
/** Copy constructor. Builds a motor with all fields and models equal to the given 
 * @param oldMotor - the battery to copy. 
 */
 public Motor(Motor oldMotor){
 /** @todo implement this, and all needed getters. */
 }

/** standard constructor. Builds a Motor with a model loaded from the file
 * @param fileName - the file to load the model from
 */
public Motor(String fileName){
	Log.write("ElectricalController created motor");
	loadModel(fileName);
}

private void loadModel(String fileName){
/** @todo implement this. Figure out how to represent the Motor model. */
	//These should be coming from the model file. Here until implemented.
	//Need these from tests of the motor:
	//noLoadSpeed
	//stallTorque
}

/** default constructor, builds a motor with a default Model */
public Motor(double newForce, double newRadius, double newTemperature, double newVoltage, double newCurrent, double newRPM){
	force = newForce;		
	temperature = newTemperature;
	voltage = newVoltage;
	current = newCurrent;
	radius = newRadius;
}

//--------END OF CONSTRUCTOR-TYPE METHODS, START OF CALULATING ONES--------------

/** returns force predicted by power - torque graph
 * assumption: power - torque graph is linear
 * @param voltage 	- current voltage that the motor is running at
 * @param radius	- radius of the wheel
 */
/*
private double getRPM(double voltage, double current){
	double power;
	double calculatedRPM;
	double c = 5000; // slope of power - RPM graph, made-up value
	power = voltage*current;
	calculatedRPM = c * power;
	return calculatedRPM;
}
*/

/** predicts the next state of the motor and all class fields
 *  assumes steady state
 * @param time 		- the time (in milliseconds) that this iteration spans
 * @param radius	- radius of the wheel
 * @param doLog 	- if True, will write messages to the log 
 * @param netForce 	- the current net force on the car. 
 * @param netWeight - net weight of the car
 * @returns the RPM of the motor
 */
public double nextMotor(double time, Environment worldEnviro, Boolean doLog, double netForce, double netWeight, double voltage, double current){
	// todo create a better heat model
	// function is too massive and messy. need to clean up calculations. get it working for now. 
	// returns currentRPM of steady state motors
	int currentRPM;
	double torqueVoltage;
	double noLoadSpeed = 1000;
	double stallTorque = 1000;
	double slope = noLoadSpeed/stallTorque;
	torqueVoltage = slope * voltage + noLoadSpeed;
	currentRPM = (int)(5* torqueVoltage);
	Log.write("Motor now spinning at: " + currentRPM + " rpm");
	return currentRPM;
}

//------------GETTERS AND SETTERS-------------------
/** @todo implement these */

}