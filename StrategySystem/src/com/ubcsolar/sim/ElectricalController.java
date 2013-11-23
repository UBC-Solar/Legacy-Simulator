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
	
	// Is motor regenerating.
	boolean motor_regen = checkMotorRegen(battery_voltage,throttle,angAccel);
	
	// The current that the motor is needed or regened
	double motor_current = getMotorCurrent(battery_voltage,throttle,angAccel);
	
	//double CurrentRequested = Current_needed(throttle,time); //this is how much voltage would be fed to the motor
	
	// Assume the current states the same in the "time" interval
	// @Tofix: maybe add another param "voltage" to panel.
	double panelCurrentGenerated = myPanels.nextPanels(time, worldEnviro, doLog); //this is how much power the solar cells made
	
	
	int rpm = 0;
	
	if (motor_regen)
	{
	
	if(doLog){Log.write("Current regened from motor: " + (-motor_current) + " A");}
	}
	else{
		if(doLog){Log.write("Current needed for motor: " + motor_current + " A");}
	}
	
	
	// The time needed for the battery to get fully charged
	
	
	
	// The current the panel can generate
	// To fix: add voltage to this method.
	double panel_current = current_from_panel();
	
	
	// The current that the battery can charge the system
	double battery_charge_current = current_battery_feed(time);

	
	//double sum_current = panel_current + battery_charge_current;
	
	
	if(motor_regen) // when motor is regenerating energy
	{
		// To fix: Add "time" param when we take "heat" into account.
		rpm = (int) myMotor.nextMotor(battery_voltage, throttle, angAccel,  doLog);
		if(doLog)
		Log.write("Motor is regenerating energy.");
		
		//Since the motor current is negative when it's regenerating, sum_current = pannel_current + (- motor_current).
		double sum_current=panel_current -motor_current ;
		
		if (time <= time_charging_battery(sum_current) )  // battery will not be full
		{
			myBattery.storeEnergy(time,sum_current);
			if(doLog){Log.write("Recharging the battery.");} 
		}
		else
		{
			if(doLog){Log.write("Battery Full.");} 
		}
	} else   // when motor needs to extract energy
	{
		double sum_current = panel_current + battery_charge_current;
		if(sum_current < motor_current) // When current provided by the system cannot feed the needs of motor
		{  
			//provide no power to motor
			rpm = (int) myMotor.nextMotor(0, throttle, angAccel,  doLog);
			if(doLog){Log.write("Energy can be provided is insucifficient.");
			Log.write("Was too much. No power applied to Motor");}
			
			//Try to put the panel energy to battery
			if (time <= time_charging_battery(panel_current) )  // battery will not be full
			{
				myBattery.storeEnergy(time,panel_current);
				if(doLog){Log.write("Recharging the battery");} 
			}
			else
			{
				if(doLog){Log.write("Battery Full.");} 
			}
			
		}
		else if (motor_current > panel_current)  // when panel cannot provide enough, extra energy from battery.
		{
			
				//Pull current from battery
				myBattery.drawEnergy(time,motor_current-panel_current);
				//Put all generated power to motor
				rpm = (int) myMotor.nextMotor(battery_voltage, throttle, angAccel,  doLog);
				if(doLog){Log.write("Pull energy from battery.");}
		}
		else if (panel_current == motor_current)
		{
			rpm = (int) myMotor.nextMotor(battery_voltage, throttle, angAccel,  doLog);
			//put all generated power into the motor. No need to use the battery
			if(doLog){Log.write("power in = power out");} 
		}
		else if(panel_current > motor_current)  // when panel provides extra energy , put extra energy to battery.
		{
			rpm = (int) myMotor.nextMotor(battery_voltage, throttle, angAccel,  doLog);
			if (time <= time_charging_battery(panel_current))   // when battery is not full, and it needs energy  
			{
				myBattery.storeEnergy(time,panel_current);
				if(doLog){Log.write("Pull energy from battery.");} 
			}	

			
		}
	
	}
	
	
myBattery.nextBattery(time, worldEnviro, doLog);
return rpm;
}


//------------GETTERS AND SETTERS-------------------





/** Gets voltage level from battery
 * @return The voltage that the battery is running at. (Assuming it's a fixed value)
*
 */

private double voltage_from_battery()
{
	return myBattery.getBatteryVoltage();
}

/** Gets current level from panel
 * @return The current that the panel is able to provide
 */
private double current_from_panel()
{
	return myPanels.current_from_panel();
}


/** Gets current that the battery can feed
 * @param time - 
 * @return The current that the battery is able to provide
 */
private double current_battery_feed(double time)
{
	return myBattery.getCurrent(time);
}


/** Gets total amount time to let the battery to reach full
 * @param voltage_recharged - the voltage level of the circuit
 * @param current_recharged - the current to feed the battery
 * @return The total amount time needed to get battery fully charged
 */
private double time_charging_battery(  double current_recharged){
	return ( myBattery.getMaxRechargeTime( current_recharged));
}

private boolean checkMotorRegen(double battery_voltage, int throttle, double angAccel)
{
	return myMotor.isRegen(battery_voltage,throttle,angAccel);
}
private double getMotorCurrent(double battery_voltage,int throttle,double angAccel)
{
	return myMotor.getCurrent(battery_voltage, throttle, angAccel);
}
}