package com.ubcsolar.sim;
/**
This class models the ElectricalController in controlling the panels, motor, and battery.
I wasn't sure exactly how they interact in the real world, or what units to use
and pass among the subclasses. 
Currently I use Watts, but I'm sure that must change. 
Will need to go through the ElectricalController-down and check for proper interaction. 
*/

public class ElectricalController{
//---------------CLASS FIElDS-------------------------------
private Battery myBattery; /**The car battery that the ElectricalController manages*/
private Panels myPanels;  /**the solar panels that the ElectricalController manages*/
private Motor myMotor; /** the motor that the ElectricalController manages. */

//-----------END OF FIELDS, START OF CONSTRUCTORS--------------

/** default constructor, builds a ElectricalController with default model and fields. 
 * @param newTrack - the track that will be driven on
 */
public ElectricalController(Track newTrack){
	myBattery = new Battery();
	myPanels = new Panels(newTrack);
	myMotor = new Motor();
}

/** Standard constructor, builds a ElectricalController with given parameters. 
 * @param fileName - the file from which to load model data (if any)
 * @param newBattery - the battery to load the ElectricalController with
 * @param newPanels - the panels to load the ElectricalController with
 * @param newMotor - the Motor to load the ElectricalController with
 */
public ElectricalController(String fileName, Battery newBattery, Panels newPanels, Motor newMotor){
	/** @todo do we need to load a model? */
	Log.write("ElectricalController created");
	myBattery = newBattery;
	myPanels = newPanels;
	myMotor = newMotor;
}

/** Copy constructor
 * create a ElectricalController with identical class properties as the given one
 */
public ElectricalController(ElectricalController oldElectricalController){
/** @todo implement this constructor, and needed 'getter functions'*/
}
//--------END OF CONSTRUCTOR-TYPE METHODS, START OF CALULATING ONES--------------


/** predicts the next state of the ElectricalController and subclasses, given the arguments.
 * @param time - the time (in milliseconds) that this iteration spans
 * @param worldEnviro - the environment the car is operating
 * @param doLog - If True, will write messages to the log. 
 * @param throttle - the current requested throttle setting
 * @param netForce - the current net force on the car. 
 * @param netWeight - the net weight of the car. //NOAH: May be able to remove this if given in constructor?
 */
public int nextElectricalController(int time, Environment worldEnviro, Boolean doLog, int throttle, int netForce, int netWeight){
/** @todo figure out how to calculate regenerative braking */
	double wattageRequested = convertToWattage(throttle); //this is how much voltage would be fed to the motor
	double panelWattageGenerated = myPanels.nextPanels(time, worldEnviro, doLog); //this is how much power the solar cells made
	int rpm = 0;
	if(doLog){Log.write("Wattage requested was: " + wattageRequested + " Watts");}
	double difference = panelWattageGenerated - wattageRequested;

	/** @todo will need a better model for what the electrical system does than below */
	double delta = 0.000000000000000000000001;
	if(Math.abs(difference) < delta){ //essentially zero
		rpm = myMotor.nextMotor(time, worldEnviro, doLog, netForce, netWeight, wattageRequested);
		//put all generated power into the motor. No need to use the battery
		if(doLog){Log.write("power in = power out");} //mention that. That's cool.
	}
	else if(difference<0){ //requested more power than was generated
		if(myBattery.doesContain(-difference)){ //if the batteries have enough
			myBattery.draw(Math.abs(difference)); //put all generated power to motor, pull difference from batteries
			rpm = myMotor.nextMotor(time, worldEnviro, doLog, netForce, netWeight, wattageRequested);
		}
		else{ //provide no power to motor
			rpm = myMotor.nextMotor(time, worldEnviro, doLog, netForce, netWeight, 0);
			myBattery.store(panelWattageGenerated);
			Log.write("Was too much. No power applied to Motor");
		}
	}
	else if(difference>0){ //extra power generated
		myBattery.store(difference);
		rpm = myMotor.nextMotor(time, worldEnviro, doLog, netForce, netWeight, wattageRequested);
	}
	

myBattery.nextBattery(time, worldEnviro, doLog);
return rpm;
}

/** converts the throttle percentage into a energy value
 * @param throttle - the throttle setting (in percentage)
 * @return energyNeeded - the amount of power (in Volts) it would take
 */
private double convertToWattage(int throttle){
/** @todo implement this! */
double energyNeeded = 30;
return energyNeeded;
}
//------------GETTERS AND SETTERS-------------------
/** @todo make these for all class fields */


private double voltage_from_panel()
{
	return myPanels.voltage();
}
private double current_from_panel()
{
	return myPanels.current;
}
private double current_needed_for_motor( double voltage, double velocity)
{
	
	return myMotor.current(voltage,velocity);
}


public void current_divide()
{
	double time_recharge_battery;
	double panel_voltage = voltage_from_panel();
	double panel_current = current_from_panel();
	
	double motor_current = current_needed_for_motor(panel_voltage,car_velocity);
	//double battery_recharged_current = panel_current - motor_current;
	double battery_charge_current = battery_charge_current(panel_voltage);
	//double remaining_current = panel_current - motor_current;
	
	double sum_current = panel_current + battery_charge_current;
	
	
	if (motor_current > sum_current)  // when both panel and battery cannot provide enough energy
	{
		Log.write("Energy can be provided is insucifficient.");
	}
	else if (motor_current > panel_current)  // when panel cannot provide enough, extra energy from battery.
	{
		
	}
	else   // when panel provides extra energy , put extra energy to battery.
	{
		
		if(true)  // when battery is not full, and it needs energy  
		{
		}	
		else   // when battery is full, and no energy is needed
		{
		}
		
	}
	time_recharge_battery = battery_recharged_time(panel_voltage,battery_recharged_current);
	
	
}
private double battery_charge_current(double battery_charge_voltage)
{
	return myBattery.charge_current(battery_charge_voltage);
}
private double battery_recharged_time( double voltage_recharged, double current_recharged){
	return ( myBattery.time_recharged(voltage_recharged, current_recharged));
}



}