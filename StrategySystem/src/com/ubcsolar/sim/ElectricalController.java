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
	
	double CurrentRequested = Current_needed(throttle); //this is how much voltage would be fed to the motor
	double panelCurrentGenerated = myPanels.nextPanels(time, worldEnviro, doLog); //this is how much power the solar cells made
	int rpm = 0;
	if(doLog){Log.write("Current requested was: " + CurrentRequested + " A");}
	
	
	
	double time_recharge_battery;
	double panel_voltage = voltage_from_panel();
	double panel_current = current_from_panel();
	
	

	double battery_charge_current = current_battery_feed(panel_voltage);

	
	double sum_current = panel_current + battery_charge_current;
	
	
	if (CurrentRequested > sum_current)  // when both panel and battery cannot provide enough energy
	{
		//provide no power to motor
		
		rpm = myMotor.nextMotor(time, worldEnviro, doLog, netForce, netWeight, 0,0);
		Log.write("Energy can be provided is insucifficient.");
		Log.write("Was too much. No power applied to Motor");
		if (battery_not_full)
		{
		myBattery.store(panelWattageGenerated);
		if(doLog){Log.write("Extra energy charging battery");} 
		}
		
	}
	else if (CurrentRequested > panel_current)  // when panel cannot provide enough, extra energy from battery.
	{
		if(myBattery.doesContain(CurrentRequested - panel_current)){ //if the batteries have enough
			myBattery.draw(Math.abs(CurrentRequested - panel_current)); //put all generated power to motor, pull difference from batteries
			rpm = myMotor.nextMotor(time, worldEnviro, doLog, netForce, netWeight, CurrentRequested,panel_voltage);
		}
		
	}
	else if(panel_current == CurrentRequested)
	{
		rpm = myMotor.nextMotor(time, worldEnviro, doLog, netForce, netWeight, CurrentRequested,panel_voltage);
		//put all generated power into the motor. No need to use the battery
		if(doLog){Log.write("power in = power out");} //mention that. That's cool.
	}
	else if(panel_current > CurrentRequested)  // when panel provides extra energy , put extra energy to battery.
	{
		rpm = myMotor.nextMotor(time, worldEnviro, doLog, netForce, netWeight, CurrentRequested,panel_voltage);
		
		
		if(battery_not_full)  // when battery is not full, and it needs energy  
		{
			myBattery.store(panelWattageGenerated);
			if(doLog){Log.write("Extra energy charging battery");} 
		}	

		
	}

myBattery.nextBattery(time, worldEnviro, doLog);
return rpm;
}

/** converts the throttle percentage into a energy value
 * @param throttle - the throttle setting (in percentage)
 * @return current_needed - the amount of current (in A) it would take
 */

private double Current_needed(int throttle){
/** @todo implement this! */
	double current_needed;
	double total_current = 0;
	total_current=total_current();
	current_needed= total_current*throttle/100;

return current_needed;
}
//------------GETTERS AND SETTERS-------------------


/** Calculate the total current that the panel and battery have currently.
 * @return total current (max current) that battery and solar panel can feed to the motor.
 */
private double total_current()
{
	//To do: (Whether/How) Integrate current regenerated from motor;
	return (current_from_panel() + current_battery_feed(voltage_from_panel()));
}


/** Gets voltage level from panel
 * @return The voltage that the panel is able to provide
 */

private double voltage_from_panel()
{
	return myPanels.voltage();
}

/** Gets current level from panel
 * @return The current that the panel is able to provide
 */
private double current_from_panel()
{
	return myPanels.current();
}


//param 
//return 
private double current_battery_feed(double battery_charge_voltage)
{
	return myBattery.charge_current(battery_charge_voltage);
}


private double time_charging_battery( double voltage_recharged, double current_recharged){
	return ( myBattery.time_to_reach_full(voltage_recharged, current_recharged));
}



}