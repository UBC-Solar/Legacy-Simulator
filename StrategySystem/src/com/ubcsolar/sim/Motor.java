package com.ubcsolar.sim;
/** This class models the motor. 
It holds the torque curves, and calculates how fast
it spins based on the power going into it. 
Also models heat to make sure we don't cook it.
Ideally, it would be similar to Stanford's panel sim (http://solarcar.stanford.edu/design/systems/strategy/)
except for motors instead.
*/

public class Motor{
//---------------CLASS FIElDS-------------------------------
private double torque;			/** the torque on the motor **/
private double current;			/** current that the motor is operating at **/
private double emfConstant; 	/** EMF constant determined by motor tests **/
private double torqueConstant;	/** torque-current constant determined by motor tests **/
private double charRes;			/** characteristic resistance of the motor **/

//-----------END OF FIELDS, START OF CONSTRUCTORS--------------
/** Copy constructor. Builds a motor with all fields and models equal to the given 
 * @param oldMotor - the motor to copy. 
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
/** @todo implement this. Figure out how to represent and read the motor representation. */
	emfConstant = 20.0;
	torqueConstant = 5.0;
	charRes = 100.0;
}

//--------END OF CONSTRUCTOR-TYPE METHODS, START OF CALULATING ONES--------------
/** characteristic resistance of the motor, 16 mOhm, and EMF Constant are assumptions. Need data.
 * @param delV 		 	 	- voltage of the motor treater as a generator
 * @param batteryVoltage 	- voltage of the main bus bar
 * @param accelPercent		- accelerator percentage - duty cycle, essentially
 * @param angVel			- angular velocity, w, of the motor
 * return current of the motor
 */
public double getCurrent(double batteryVoltage, int accelPercent, double angVel){
	double delCurrent;
	double delV;
	delV = getDelV(batteryVoltage, accelPercent, angVel);
	delCurrent = delV/charRes;
	return delCurrent;
}

/** finds delta voltage of the motor, if negative, torque is negative
 * @param delV 		 	 	- voltage of the motor treater as a generator
 * @param batteryVoltage 	- accelerator percentage
 * @param accelPercent		- duty cycle that the motor is running at
 * @param angAccel			- angular acceleration, w, of the motor
 * return delV of the motor
 */
private double getDelV(double batteryVoltage, int accelPercent, double angVel){
	double delV;
	delV = batteryVoltage * accelPercent - emfConstant * angVel; 
	return delV;
}

/** calculates torque of the motor, given current
 * @param torqueConstant	- torque-current relationship is assumed to be linear
 * return torque exerted by the motor
 */
public double getTorque(double current){
	torque = torqueConstant * current;
	return torque;
}

/** regenerative braking occurs when delV is a negative, therefore motor is spinning backwards
 * @param batteryVoltage 	- bus bar voltage, determined by battery
 * @param accelPercent		- duty cycle that the motor is running at
 * @param angAccel			- angular acceleration, w, of the motor
 * return boolean 			- true, if regen, false, if no regen
 */
public Boolean isRegen(double batteryVoltage, int accelPercent, double angVel){
	double delV;
	delV = batteryVoltage * accelPercent - emfConstant * angVel;
	if ( (angVel > 0 ) && (delV < 0)) 		// car is slowing down
		return true;
	else if ((angVel < 0) && (delV > 0))	// car when going backwards is slowing down
		return true;
	else 									// every other case 
		return false;
}

/** creates next motor object
 * @param batteryVoltage	- bus bar voltage, determined by the battery
 * @param dutyCycle			- duty cycle of the motor
 * @param angVel			- angular acceleration, w
 * @param doLog				- if true, will write to log
 * @return current drawn, given the parameters
 */
public double nextMotor(double batteryVoltage, int dutyCycle, double angVel, Boolean doLog){
	current = getCurrent(batteryVoltage, dutyCycle, angVel);
	torque = getTorque(current);
	Log.write("Motor torque is at: " + torque + " N m");
	if (current > 0)
		Log.write("Motor is now consuming: " + current + " A");
	else 
		Log.write("Motor is now producing: " + current + " A");
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