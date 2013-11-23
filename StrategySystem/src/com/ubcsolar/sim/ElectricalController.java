package com.ubcsolar.sim;
/**
This class models the ElectricalController in controlling the panels, motor, and battery.
 
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
	myMotor = new Motor("");
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
public int nextElectricalController(int time, Environment worldEnviro, Boolean doLog, int throttle, double angAccel){
/** @todo figure out how to calculate regenerative braking */
	
	
	double battery_voltage = voltage_from_battery();
	
	boolean moter_regen = checkMotorRegen();

	double CurrentRequested = Current_needed(throttle,time); //this is how much voltage would be fed to the motor
	
	// Assume the current states the same in the "time" interval
	double panelCurrentGenerated = myPanels.nextPanels(time, worldEnviro, doLog); //this is how much power the solar cells made
	
	int rpm = 0;
	
	if(doLog){Log.write("Current requested was: " + CurrentRequested + " A");}
	
	
	double time_recharge_battery;
	
	
	
	double panel_current = current_from_panel();
	
	

	double battery_charge_current = current_battery_feed(panel_voltage,time);

	
	double sum_current = panel_current + battery_charge_current;
	
	
	if (CurrentRequested > sum_current)  // when both panel and battery cannot provide enough energy
	{
		//provide no power to motor
		
		rpm = myMotor.nextMotor(time, worldEnviro, doLog, netForce, netWeight, 0,0);
		Log.write("Energy can be provided is insucifficient.");
		Log.write("Was too much. No power applied to Motor");
		if (time <= time_charging_battery(panel_voltage,panel_current) )  // battery will not be full
		{
		myBattery.storeEnergy(time,panel_current,panel_voltage);
		if(doLog){Log.write("Extra energy charging battery");} 
		}
		
	}
	else if (CurrentRequested > panel_current)  // when panel cannot provide enough, extra energy from battery.
	{
		if(time <= time_charging_battery(panel_voltage,panel_current-CurrentRequested)){ //if the batteries have enough
			myBattery.drawEnergy(time,CurrentRequested-panel_current,panel_voltage); //put all generated power to motor, pull difference from batteries
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
		
		
		if (time <= time_charging_battery(panel_voltage,panel_current))   // when battery is not full, and it needs energy  
		{
			myBattery.storeEnergy(time,panel_current-CurrentRequested,panel_voltage);
			if(doLog){Log.write("Extra energy charging battery");} 
		}	

		
	}

myBattery.nextBattery(time, worldEnviro, doLog);
return rpm;
}

/** converts the throttle percentage to a energy value
 * @param throttle - the throttle setting (in percentage)
 * @param time - the time interval
 * @return current_needed - the amount of current (in A) it would take
 */

private double Current_needed(int throttle,int time){
/** @todo implement this! */
	double current_needed;
	double total_current = 0;
	total_current=total_current(time);
	current_needed= total_current*throttle/100;

return current_needed;
}
//------------GETTERS AND SETTERS-------------------


/** Calculate the total current that the panel and battery have currently.
 * @param time - the time interval to execute the operation
 * @return total current (max current) that battery and solar panel can feed to the motor.
 */
private double total_current(int time)
{
	//To do: (Whether/How) Integrate current regenerated from motor;
	return (current_from_panel() + current_battery_feed(voltage_from_panel(),time));
}


/** Gets voltage level from battery
 * @return The voltage that the battery is running at. (Assuming it's a fixed value)
*
 */

private double voltage_from_battery()
{
	return myBattery.BatteryVoltageLevel();
}

/** Gets current level from panel
 * @return The current that the panel is able to provide
 */
private double current_from_panel()
{
	return myPanels.current_from_panel();
}


/** Gets current that the battery can feed
 * @param battery_charge_voltage - the voltage level of the circuit
 * @return The current that the battery is able to provide
 */
private double current_battery_feed(double battery_charge_voltage,double time)
{
	return myBattery.getCurrent(battery_charge_voltage,time);
}


/** Gets total amount time to let the battery to reach full
 * @param voltage_recharged - the voltage level of the circuit
 * @param current_recharged - the current to feed the battery
 * @return The total amount time needed to get battery fully charged
 */
private double time_charging_battery( double voltage_recharged, double current_recharged){
	return ( myBattery.getMaxRechargeTime(voltage_recharged, current_recharged));
}

private boolean checkMotorRegen()
{double batteryVoltage, int accelPercent, double angAccel
	return myMotor.isRegen()
}

}