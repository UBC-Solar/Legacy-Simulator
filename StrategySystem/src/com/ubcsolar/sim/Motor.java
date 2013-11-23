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
private double torque;			/** the torque on the motor **/
private double current;			/** current that the motor is operating at **/
private double emfConstant; 	/** EMF constant determined by motor tests **/
private double torqueConstant;	/** torque-current constant determined by motor tests **/
private double charRes;			/** characteristic resistance of the motor **/
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
	emfConstant = 20;
	torqueConstant = 18.0;
	charRes = 0.016;
}

private void loadModel(String fileName){
/** @todo implement this. Figure out how to represent the Motor model. */
	//These should be coming from the model file. Here until implemented.
	//Need these from tests of the motor:
	// characteristic resistance
	// emfconstant
	// torque constant
}

/** default constructor, builds a motor with a default Model */
public Motor(double newTorque,double newCurrent){
	current = newCurrent;
	torque = newTorque;
	emfConstant = 20;
	torqueConstant = 18.0;
	charRes = 0.016;
}

//--------END OF CONSTRUCTOR-TYPE METHODS, START OF CALULATING ONES--------------

/** characteristic resistance of the motor, 16 mOhm, and EMF Constant are assumptions. Need data.
 * @param delV 		 	 	- voltage of the motor treater as a generator
 * @param batteryVoltage 	- voltage of the main bus bar
 * @param accelPercent		- accelerator percentage - duty cycle, essentially
 * @param angAccel			- angular acceleration, w, of the motor
 * return current of the motor
 */
public double getCurrent(double batteryVoltage, int accelPercent, double angAccel){
	double current;
	double delV;
	delV = getDelV(batteryVoltage, accelPercent, angAccel);
	current = delV/charRes;
	return current;
}

/** finds delta voltage of the motor, if negative, torque is negative
 * @param delV 		 	 	- voltage of the motor treater as a generator
 * @param batteryVoltage 	- accelerator percentage
 * @param accelPercent		- duty cycle that the motor is running at
 * @param angAccel			- angular acceleration, w, of the motor
 * return delV of the motor
 */
private double getDelV(double batteryVoltage, int accelPercent, double angAccel){
	double delV;
	delV = batteryVoltage * accelPercent - emfConstant * angAccel;
	return delV;
}

/** calculates torque of the motor, given current
 * @param torqueConstant	- torque-current relationship is assumed to be linear
 * return torque exerted by the motor
 */
public double getTorque(){
	torque = torqueConstant * current;
	return torque;
}

/** regenerative braking occurs when delV is a negative, therefore motor is spinning backwards
 * @param batteryVoltage 	- bus bar voltage, determined by battery
 * @param accelPercent		- duty cycle that the motor is running at
 * @param angAccel			- angular acceleration, w, of the motor
 * return boolean 			- true, if regen, false, if no regen
 */
public Boolean isRegen(double batteryVoltage, int accelPercent, double angAccel){
	double delV;
	delV = batteryVoltage * accelPercent - emfConstant * angAccel;
	if (delV > 0)
		return false;
	else
		return true;
}

/** creates next motor object
 * @param batteryVoltage	- bus bar voltage, determined by the battery
 * @param dutyCycle			- duty cycle of the motor
 * @param angAccel			- angular acceleration, w
 * @param doLog				- if true, will write to log
 * @return current drawn, given the parameters
 */
public double nextMotor(double batteryVoltage, int dutyCycle, double angAccel, Boolean doLog){
	current = getCurrent(batteryVoltage, dutyCycle, angAccel);
	torque = getTorque();
	Log.write("Motor now spinning at: " + torque + " N m");
	Log.write("Motor now pulling: " + current + " A");
	return current;
}

//------------GETTERS AND SETTERS-------------------
/** Getter methods for Characteristic Resistance, EMF constant, Torque Constant **/
public double getCharRes(){
	return charRes;
}
public double getEMFConstant(){
	return emfConstant;
}
public double getTorqueConstant(){
	return torqueConstant;
}

/** Setter methods for Current and Torque **/
public void setCurrent(double value){
	current = value;
}
public void setTorque(double value){
	torque = value;
}

}