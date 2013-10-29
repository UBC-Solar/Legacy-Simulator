package com.ubcsolar.sim;
/** This class models the motor. 
It holds the tourque curves, and calculates how fast
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
private double maxDraw; /** the max electricity the motor can require. @todo fix this */
private int temperature;/** the temp. of the motor (Celsius). Don't want to overheat now. */
private int currentRPM;  /** the current spinning speed of the motor */

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
	maxDraw = 5; //made that number up. 
	temperature = 22;// start at room temperature
	currentRPM=0; /* current rotation speed */
}

/** default constructor, builds a motor with a default Model */
public Motor(){
/** @todo build a default motor */
	maxDraw = 5; //made that number up. 
	temperature = 22;// start at room temperature
	currentRPM=0; /* current rotation speed */
}

//--------END OF CONSTRUCTOR-TYPE METHODS, START OF CALULATING ONES--------------
/** calculates the net current the motor would consume given it's current 
 * @param voltageInput - the number of volts that would be given to the motor
 * @param netForce - units = tourque? The current load on the motor (total friction) // @todo check the units
 * @param netWeight - the mass of the moving object. This is included for change in momentum. 
 * @return netCurrentConsumed - the current the motor would consume (Amps) (or generate if it's a negative number)
 */
public double netCurrent(double voltageInput, int netForce, int netWeight){
/** @todo use the model to predict this */
double netCurrentConsumed = pullCurrentOutOfButt();
return netCurrentConsumed;
}

/** This function is for testing purposes
 * @return madeUpNumber - some number I made up. In Amps. 
 */
private double pullCurrentOutOfButt(){
/** @todo render this function redundant. Please. Don't want to make up numbers =) */
double madeUpNumber =7.5;
return madeUpNumber;
}

/** predicts the next state of the motor and all class fields
 * @param time - the time (in milliseconds) that this iteration spans
 * @param worldEnviro - the environment the car is operating
 * @param doLog - If True, will write messages to the log. 
 * @param throttle - the current requested throttle setting
 * @param netForce - the current net force on the car. 
 * @param netWeight - the net weight of the car. //NOAH: May be able to remove this if given in constructor?
 * @param wattageIn - the power given to the motor to convert into mechanical energy. 
 * @return the rpm of the motor
 */
public int nextMotor(int time, Environment worldEnviro, Boolean doLog, int netForce, int netWeight, double wattageIn){
int rpm = getSpinSpeed(wattageIn, time);
temperature += pullCurrentOutOfButt(); /** @todo model this better*/
Log.write("Motor now spinning at: " + rpm + " rpm");
temperature += 1; /** @todo get a better heat model! */
currentRPM = rpm;
return rpm;
}

/** calculates how fast the motor is now spinning
 * based on the energy input, and current spin speed
 * @param wattageIn - the power to the motor
 * @param time - the length of this iteration (longer means it can accelerate more)
 */
private int getSpinSpeed(double wattageIn, int time){
/** @todo need to fix this */
if(wattageIn>0){
return 420;
}
else{
return 0;
}
}

//------------GETTERS AND SETTERS-------------------
/** @todo implement these */

}