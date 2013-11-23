package com.ubcsolar.sim;
/**

This class represents the car. It holds data such as speed and location,
and it uses models to calculate total resistance (such as air and rolling)
'next car' will iterate it.
Should be 'driven' just like a car. Go faster, slower, maybe turns, but no decisions. 
*/

/** @todo add in support for 'too fast' exceptions, or 'you crashed' exceptions*/

public class Car{
//----------------CLASS FIELDS -------------------------

/** @todo add location */
/** @todo add distance tracking. How far has it gone? */
private Track myTrack; /** the track the car is driving on */
private int myWeight; /** the weight of the car, in Kg's. */
private int currentSpeed; /** current speed the car is travelling */
private int throttle; /** speed the driver wants to be going. */
private boolean isAccelerating; /** is true if car is accelerating (i.e speed will be different next iteration) */
private ElectricalController myElectricalController; /** ElectricalController of the car @todo rename to ElectricalController*/
public final int maxSpeed; /** car's max speed in km/h. Needed to convert between percent. */
private MechController myMechController;
//NOAH: ^^ this can probably be removed. It was a dumb workaround. 

//-----------END OF FIELDS, START OF CONSTRUCTORS--------------
/** default Constructor, initializes a default car with default PowerSystem. 
 * Note: has default model, doesn't load from file
 * @param newTrack - the track that the car will be driving on. (assuming starting at beginning of track)
*/ 
public Car( Track newTrack){
		/** @todo implement the location handling */
	myTrack = newTrack; //assuming starting at beginning
	//myLocation = myTrack.get(0); ??? 

	//loadModels(fileName); We can hardcode it in
	currentSpeed = 0; //assuming starting from a full stop
	throttle = 0;
	Log.write("Car created");
	myElectricalController = new ElectricalController(newTrack);
	myMechController = new MechController(newTrack);
	maxSpeed = 120; /** @todo remove this. */
}

/** Constructor that will be used more often. 
 * builds a car with a given ElectricalController, speed, and model file. 
 * @param fileName - the file to load the model data and properties from
 * @param newElectricalController - the ElectricalController to use
 * @param newCurrentSpeed - the speed, in km/h that the car is travelling
 * @param newTrack - the track that the car is driving on
 */
public Car(String fileName, int newCurrentSpeed, Track newTrack,  ElectricalController newElectricalController, MechController newMechController){
	myTrack = newTrack;
	myElectricalController = newElectricalController;
	myMechController = newMechController;
	currentSpeed = newCurrentSpeed;
	loadModels(fileName);
	maxSpeed = 120; /** @todo eliminate this */
}

/** Copy constructor
 * creates a car with all fields equal to the old one. 
 * @param oldCar - the car to copy from
 */
public Car(Car oldCar){
/** @todo implement this. Add needed getter methods*/
/** @todo eliminate this*/
maxSpeed =120; 
}

/** Loads the models from the file. 
 * @param fileName - the file to load the model from. 
 */
private void loadModels(String fileName){
myWeight = 150;  /** @todo get weight from the car file */
/** @todo implement! load models from file given the filename. */
}
//--------END OF CONSTRUCTOR-TYPE METHODS, START OF CALULATING ONES--------------



/** predicts the next state of the driver and it's object variables
 * calculates the next car at the end of this time interval. 
 * @param time - the length of time in Seconds that this iteration represents
 * @param worldEnviro - the state of the world's environment
 * @param doLog - if True: then log. If we did a very precise sim, we wouldn't need all the logs. 
*/
public void nextCar(int time, Environment worldEnviro, Boolean doLog, int throttleSetting){
	int currentNetForce = calculateNetForce();
	int rpm = myElectricalController.nextElectricalController(time, worldEnviro, doLog, throttleSetting, currentNetForce, myWeight);
	currentSpeed = translateToSpeed(rpm);
	if(doLog){Log.write("Going " + currentSpeed + " km/h");}
	checkIfSkidding(worldEnviro);
	updateLocation(time, currentSpeed);
	/** @todo add a distance calculator. Went x speed for x time, therefore... */
}

private void updateLocation(int time, int currentSpeed){
/** @todo calculate location based on speed, and update the car */
//NOAH: may have to use the old speed and old location, and then the new speed 
//(may have to assume instant change in speed. If we have low enough intervals, there
//will be no difference. 
}

/** turns the wheel speed into the car's speed 
 * @param rpm - the speed of rotation of the wheels
 * @return speed - the speed of the car in km/h
 */
private int translateToSpeed(int rpm){
double wheelDiameter = 0.000780; //wheel diameter in KM
/** @todo turn wheelDiameter into class field. */
return (int)Math.floor((wheelDiameter*60.0)*rpm); //rotations per min* diameter (km)*60(min->hour)
/** @todo change formula to using perimeter, instead of  just diameter*/
//^^ need to return an int. 
/**@todo change to return a double? */
}

/**calculates the total load (apparent weight) of the car
 * @return netForce - the net force (in Newtons) acting on the car
 */
 public int calculateNetForce(){
	int netForce = 0; 
	netForce += calculateGravityForce();
	netForce += calculateRunningFriction();
	netForce += calculateAeroFriction();
	return netForce;
 }
 /** calculates the force of aerodynamic fricition
  * @return aeroFriction - the force of friction (in Newton) from the air. 
  */
  private int calculateAeroFriction(){
  /** @todo implement better algorithm */
  return 50; 
  }
 /** calculates the force of friction on the wheels of the car
  * @return runningFriction - the net force of friction (in Newtons)
  */
  private int calculateRunningFriction(){
  /** @todo implement better algorithm */
  return 100;
  }
 /** calculates the force of gravity on the car, given position on the track
  * (Only non-zero if on a hill)
  * @return gravityForce - net force of gravity (in Newtons)
  */
private int calculateGravityForce(){
	/** @todo better algorithm. Will involve Trig. */ 
	return 0; //will be zero if on flat ground
}
 
/*
This function determines, based on the wetness of the environment and the handling model,
whether the car is skidding on the tarmac
*/
private void checkIfSkidding(Environment enviro){
	//TODO come up with better skidding algorithm, based on turning speed and wetness of track
	Boolean skidding = false;
	if(skidding){
		Log.write("Skidding!!!");
	}
}

//------------GETTERS AND SETTERS-------------------
/*
//Sets the throttle. 
//This function assumes that the throttle can be changed instantly. 
public void setThrottle(int newThrottleSetting){
	throttle = newThrottleSetting;
}*/
/** returns the current speed of the car
 * @return currentSpeed */
public int getCurrentSpeed(){
	return currentSpeed;
}

/** returns the throttle setting of the car
 * @return throttleSetting*/
public int getThrottle(){
	return throttle; //may need this so the driver 'knows' he hit accelerate
}

/** lets the driver know that the throttle setting is ahead of the speed 
 * so that he doesn't keep increasing it. ("oh, not fast enough, better increase")
 * @return isAccelerating - true if the car will be going faster the next iteration at the same throttle */
public boolean isAccelerating(){
	return isAccelerating;
}

}
