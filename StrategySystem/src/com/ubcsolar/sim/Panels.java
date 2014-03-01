package com.ubcsolar.sim;
/**


This class models the solar panels and their generating capacity, 
based on the variables in the environment.
See Stanford's simulator here: http://solarcar.stanford.edu/design/systems/strategy/
It would be awesome if this class could do that ^^ (minus the GUI of course)
Not entirely sure how it should pass the power to the ElectricalController (watts? Voltage and Current?)
*/
/** Model must include shape and properties of panels. */

public class Panels{
// ---------------CLASS FIELDS -------------------------------
private double maxPower; /** the most it will generate under ideal conditions*/
private double maxtemperature; /** the most amount of heat it can tolerate before shutting down */
// private double deltatemperature; /* temperature increase per time step*/
private Environment enviro;   // current angle panels are w.r.t. sun
private double current;  // current current
private double voltage;  // current voltage
private double power;   // current voltage * current// need this?
private double temperature;    // current temperature of the panels
private Track track;
private double powerProduced; // current track



//------------END OF FIELDS, START OF CONSTRUCTORS ---------------------------

/** default constructor. Builds the panels with default heat, angle, and watts*/
public Panels(double temperature, Environment newEnviro, double voltage, double current, Track newTrack){
	enviro = newEnviro;
	this.temperature = enviro.getTemperature();
 track = newTrack;
 this.voltage = voltage;
 this.current = current;
 power = (voltage * current);
 Log.write("Panels created")
 ;
	
}
/** @todo implement this! Can hardcode the model in*/

public Panels(Track newTrack){
	double angle = newTrack.getAngle();
	double direction = newTrack.getDirection();
	Log.write("Panels created");
}
//NOAH: wanted to have track involved becase we'll need to know
// the angle of the panels. Not sure how to implement this. 
//ANDREW: we'll need the current heading and position of the car,
// maybe these both come from the same variable track position 
	//(which has both angle and direction?)
/**@todo do something*/


/** constructor, loads model from file
 * @param fileName - the name of the file to pull the model from. 
 */
public Panels(String fileName){
	
	Log.write("Panels created");
}
/** loads the model from the given file. 
 * @param fileName - the file from which to load the model 
 */
private void loadModel(String fileName){
/** @todo implement this. */
maxPower = 5; //should come from file
}

// ---------------END OF CONSTRUCTORS, START OF PROGRAM!----------------------

/** predicts the next state of the panels, and how much they generated
 * @param time - the amount of time in Miliseconds that this iteration represents
 * @param worldEnviro: - the world's current environment.
 * @param doLog - whether to log on this iteration or not (if True, log).
 * @return powerGenerated - how much energy generated in iteration. 
 */
public double nextPanels(int time, Environment worldEnviro, Boolean doLog){
	
//update heat
//update position/angle?? 
/** @todo find a way so the panels know what angle they're on. */
calculateTemperature();
calculateVoltage(powerProduced);
double powerGenerated = calculatePower(time, worldEnviro);
return powerGenerated;
	}


/** calculates the amount of power generated in the given time
 * @param time - the amount of time this iteration spans
 * @param worldEnviro - the environment the car is in
 * @return powerGenerated - the amount of power generated (in Watts) in the given time
 */

private double calculatePower(int time, Environment worldEnviro){
	/*double actualAngle = enviro.getSunAngle()-track.getAngle();
	double actualAngleRadians = ( actualAngle * Math.PI) / 180 ;         // converts the sun angle to radians 
	double sunEnergy = (Math.sin(actualAngleRadians) * enviro.getSunIntensity()); // calculates the watts per square meter on a flat surface
													   // calculates the actual angle of sun hitting the panels
	double powerAvailable = calculateOutput(sunEnergy, powerProduced);
	*/ 
	/* assumption is that we have 389 panels each produce (0.6V * 6A) 3.6W under ideal conditions (sun intensity, angle)
	 and we have 389 cells for a total of 1400.4 Watts*/
	// the following is encapsulated for when we have actual enviro info
	double powerAvailable = 1400.4;
	this.powerProduced = calculatePanelEfficiency() * powerAvailable;
	double output = powerProduced * time;
	Log.write("Panels produced " + output + " watts of power");
	return output;
	 

// TODO need to adjust for square meter collecting area
 }

/*
public double calculatePowerTest(int time, float trackAngle, float sunAngle, float lightIncident, float lightDiffuse){
	float incident = lightIncident;
	float diffuse = lightDiffuse;
	double actualAngle = enviro.getSunAngle()-track.getAngle();
	double actualAngleRadians = ( actualAngle * Math.PI) / 180 ;         // converts the sun angle to radians 
	double sunEnergy = ((Math.sin(actualAngleRadians) * incident)
			           + diffuse); // calculates the watts per square meter on a flat surface									   // calculates the actual angle of sun hitting the panels
	double powerAvailableTest = calculateOutput(sunEnergy);
	double powerProduced = calculatePanelEfficiency() * powerAvailableTest;
	return powerProduced;
	Log.write("Panels produced " + powerProduced + " watts of power");

}

// produces the watts available given a certain amount of solar flux
public double calculateOutput(double sunEnergy){
	
})
*/

private double calculatePanelEfficiency(){
	double efficiencyLoss = temperature * 0.0032;
	double efficiency = 1.0 - efficiencyLoss;
	double eff1 = 1;
	Log.write("Panels operating at " + eff1 + " efficiency");
	return eff1;
}


// takes in available power and gives back the voltage and current associated with that on the power curve
private double calculateVoltage(double powerProduced){ 
	double currentOut = powerProduced / 38.06; // calculated based on bus voltage
	double voltageOut = 38.06;      	 	  // this is the average voltage across the 6 panel segments
	voltage = voltageOut;
	current = currentOut;
	power = currentOut * voltageOut;
	double output = voltageOut * currentOut;
	return output;
	
}

// 0.0035 is an arbitrary heat / watt produced value
private void calculateTemperature(){
	double deltatemperature = power * 0.0035; 
	double coolingtemp = coolingAmount();
	temperature = deltatemperature + this.temperature - coolingtemp ;
	Log.write("Panels produced " + deltatemperature + " amount of heat");
	Log.write("Panels are currently at " + temperature + " degrees celsius");
}


// TODO add cooling effects of speed
// Current cooling formula gets panels to approx 200 C after 5 hours operating at high solar intensity
 private double coolingAmount(){
	double temperatureDifference = this.temperature - enviro.getTemperature();
	double temperatureReduce = .0095 * (temperatureDifference);
	Log.write("Panels coooled by " + temperatureReduce + " degrees Celsius");
	return temperatureReduce ; 
 }	


 // ----------------- Getters and Setters ---------------- //
 // gives the current voltage output

 public double voltage_from_panel(){
	 return this.voltage;
 }
 
 // gives the current current output
 public double current_from_panel(){
	 return this.current;
 }
 
 public double power_from_panel(){
	 return this.current * this.voltage;
 }
 
//   shows the heat produced for this iteration
//   public double heat_from_panel(){
//	 return this.deltatemperature;
// }
 
 // shows the current temperature of the panels
 public double temperature_from_panel(){
	 return this.temperature;
 }
 

/* was old way of doing it before implement 'nextPanel' Can probably delete, 
//but algorithm concept may be useful.  
//calculates the amount of power generated in the given amount of time. 
public Double getPower(int time, Environment enviro, Boolean doLog){

	//TODO much better algorithm needed here. 
	Double powerGenerated = ((double)maxPower/60.0)*time*((double)enviro.getSunIntensity()/100.0);
	if(doLog){
		Log.write("Panels made " + powerGenerated + " power");
	}
	return powerGenerated;
}*/

}