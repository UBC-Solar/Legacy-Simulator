package com.ubcsolar.sim;
/** This class will store the driver, the guy that 'drives' the car. 
The driver will have the driving strategy, and make decisions about
driving due to the upcoming track (slow down for a turn? Conserve battery power?)
It's here was can introduce factors such as speed limits, safe cornering limits, etc. 
Also calls the next Car
*/

/** @todo find a way to represent driving strategy */

public class Driver{
// ---------------CLASS FIELDS -------------------------------
/** the driver's car to control */
private Car myCar;

/** the Track that the driver will be driving on. Position stored in Car. */
Track myTrack;

/** @todo race strategy here */
//RaceStrategy myRaceStrategy??
//------------END OF FIELDS, START OF CONSTRUCTORS ---------------------------
/** default constructor. Creates default car. 
 * @param track - the track that he will be driving on.
 */
public Driver(Track track){
	//loadDrivingStrategy(fileName); //NOAH: Not needed. It will be default, so we can hard code it in
	//it will be either hard-coded here or in the subclass. 
	Log.write("Driver created");
	myTrack = track;
	myCar = new Car(myTrack);
}

/**
 * Constructor that will be used more. 
 * builds a driver, with specified Car, track, and strategy. 
 * @param fileName - the file to load the strategy from
 * @param newCar - the car (built outside the class) to use.
 * @param newTrack - the track the driver will be driving on. (it doesn't change)
 */
public Driver(String fileName, Track newTrack, Car newCar){
myCar = newCar;
myTrack = newTrack;
loadDrivingStrategy(fileName);
}

/** copy constructor. Builds a new driver with all fields equal to the previous one. 
 * @param oldDriver - the driver that we want to copy
 */
public Driver(Driver oldDriver){
myCar = oldDriver.getCar();
myTrack = oldDriver.getTrack();
/** @todo figure out a getter method for a driver's strategy */
//myStrategy = oldDriver.getStrategy();??

}
/** loads and stores the driving strategy for this simulation, from the file. 
 * Modifies: this. (should store the strategy in a class field
 * @param fileName - the file to load from
 */
private void loadDrivingStrategy(String fileName){
/** @todo implement this! Need to load and store.  */
}


// ---------------END OF CONSTRUCTORS, START OF PROGRAM!----------------------
/** Chooses the next throttle setting, and update the car. 
 * AKA 'perdict the next state of the driver and it's object variables
 * @param time - the amount of time in Miliseconds that this iteration represents
 * @param worldEnviro - the world's current environment.
 * @param doLog - whether to log on this iteration or not (if True, log).
 * (NOAH: ^^We may want a more granular simulation (i.e every iteration is 1 ms)
 * but we don't need to log all of that (that would be a huge file!))
*/
public void nextDriver(int time, Environment worldEnviro, Boolean doLog){
	int preferredThrottle = chooseThrottle(); //where do I want the throttle to be
		if(doLog && myCar.getThrottle() != preferredThrottle){ //if we're logging this iteration
		Log.write("Throttle set for " + preferredThrottle); //log it!
		}
	
	myCar.nextCar(time, worldEnviro, doLog, preferredThrottle);
	//call nextCar, continue the 'next' chain
}



/** chooses ideal speed to be travelling, based on race strategy and other conditions (track, enviro, etc.)
 * @return the speed in km/h that he wants to be travelling
 */
private int chooseSpeed(){
	return 50; /** @todo strategy algorithm here. Basically will be implementing the strategy he loaded earlier. */
	/** @todo should we add a log here to say "here's what the driver's thinking"?*/
}

/** This method converts the speed that the driver wants into a throttle setting (i.e pedal halfway down)
 * Due to things like hills, he may have to adjust the throttle just to keep a steady speed. 
 * Just like a real car, he may not quite know exactly which throttle position = the real world speed. 
 * @return throttlesetting - driver's preferred throttle setting (in %)
*/
private int chooseThrottle(){
	/** @todo come up with better algorithm to calculate this. (should we extend the motor class as a guide?) */
	int maxCarSpeed = 120; //120 km/hr is car's max speed?? (this is a crappy work-around
	/** @todo if he's on a hill, he'll need to use more throttle than on flat ground. Implement that. */
	int throttlesetting = (chooseSpeed() * 100)/(maxCarSpeed);//very simple algorithm for picking throttle. 
														//assumes 50% throttle = 50% speed etc.  
	return throttlesetting;
}



//-------------END OF PROGRAM, START OF GETTERS/SETTERS ---------------------
/** Getter (sort of) for strategy
 * @return the filename where the driver's strategy was loaded from. 
 */
public String getStrategyFileName(){
/** @todo figure out how to implement this! */
return "something.txt";
}
/** Getter for myTrack
 * @return myTrack - the driver's track
 */
 public Track getTrack(){
 return myTrack;
 }
/** Getter for myCar
 * @return myCar - the drivers Car
 */
public Car getCar(){
return myCar;
}
}