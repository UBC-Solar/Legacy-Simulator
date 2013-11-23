package com.ubcsolar.sim;

/*


This class is the controller for the world. The GUI will interact with this class.
It stores the track, driver, and environment, and changes them when needed
Should call nextWorld with the needed time frames (how granular do we want? 1 iteration/sec?)
*/
/** @todo come up with a way to store the various filenames instead of having them hardcoded */

public class World{
// ----------START OF FIELDS ---------------
private Environment worldEnviro; /** the world's current environment */
private Driver worldDriver; /**the world's current driver */
private Track worldTrack; /** the World's current track */

//-----------END OF FIELDS, START OF CONSTRUCTORS -----------
/**
 * this is the default World constructor. Calls initialize
 * @param fileName - the filename where we can pull models from, if needed. 
 * may need to add arguments to implement later features. 
*/
public World(String fileName){
	initialize(fileName);
}

/** builds and initializes the world
 * @param trackFileName - the filename from where to pull models, if needed.
 * may also need to add arguments here. 
 * NOAH: This function is separate from constructor, so we can re-initialize without declaring a whole new world
 */
public void initialize(String trackFileName){
	/** @todo buildconstructor chain here. Should make battery, 
	 * then a Powersystem with that battery, then a Car with that Powersysem etc.
	 * need to get needed constructors built*/
//Battery worldBattery = new Battery("filename here");
	Log.write("World - created");
	worldTrack = new Track("World created track", trackFileName);
	System.out.println("World created itself");
	worldEnviro = new Environment(100, 90, 0, 0, 0,22);
	//worldDriver = new Driver(worldTrack); //starts the general chain. need to fix this. 
	worldDriver = 
	new Driver("driverModel1.txt", worldTrack,
		new Car("CarModel1.txt", 0, worldTrack,
			new ElectricalController("ElectricalControllerModel1.txt",
				new Battery("batteryModel1.txt"),
				
				new Panels(20, worldEnviro, 0, 0, worldTrack), //TODO update Panel's string filename onstructor.
				new Motor("motorModel1.txt")
			), new MechController(worldTrack)
		)
	);
			
}

//---------END OF CONSTRUCTORS, START OF RUNNING METHODS ------------------
/** this runs the program. Everything comes from here
 * @param args - extra things passed by the command line
 */
public static void main(String[] args){
	System.out.println("hello world");
	World theWorld = new World("trackfile.exe"); //create a 'World' object (we could run 5 different simulations here)
	for(int second = 0; second<10; second++){ //currently running 10 iterations
		System.out.println(""); //clear line to make Iterations stand out.
		Log.write("Iteration: " + second);
		theWorld.nextWorld();
	}
}

/** the 'tick' function, starts the next___ chain. */
public void nextWorld(){
	worldDriver.nextDriver(1, worldEnviro, true);
}


}
