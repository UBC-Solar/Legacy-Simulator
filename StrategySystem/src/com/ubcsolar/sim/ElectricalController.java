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
	
	double motor_current = current_needed_for_motor(panel_voltage,car_velocity);
	//double battery_recharged_current = panel_current - motor_current;
	double battery_charge_current = battery_charge_current(panel_voltage);
	//double remaining_current = panel_current - motor_current;
	
	double sum_current = panel_current + battery_charge_current;
	
	
	if (motor_current > sum_current)  // when both panel and battery cannot provide enough energy
	{
		//provide no power to motor
		rpm = myMotor.nextMotor(time, worldEnviro, doLog, netForce, netWeight, 0);
		Log.write("Energy can be provided is insucifficient.");
		Log.write("Was too much. No power applied to Motor");
		if (battery_not_full)
		{
		myBattery.store(panelWattageGenerated);
		if(doLog){Log.write("Extra energy charging battery");} 
		}
		
	}
	else if (motor_current > panel_current)  // when panel cannot provide enough, extra energy from battery.
	{
		if(myBattery.doesContain(motor_current - panel_current)){ //if the batteries have enough
			myBattery.draw(Math.abs(motor_current - panel_current)); //put all generated power to motor, pull difference from batteries
			rpm = myMotor.nextMotor(time, worldEnviro, doLog, netForce, netWeight, CurrentRequested);
		}
		
	}
	else if(panel_current == motor_current)
	{
		rpm = myMotor.nextMotor(time, worldEnviro, doLog, netForce, netWeight, CurrentRequested);
		//put all generated power into the motor. No need to use the battery
		if(doLog){Log.write("power in = power out");} //mention that. That's cool.
	}
	else if(panel_current > motor_current)  // when panel provides extra energy , put extra energy to battery.
	{
		rpm = myMotor.nextMotor(time, worldEnviro, doLog, netForce, netWeight, CurrentRequested);
		
		
		if(battery_not_full)  // when battery is not full, and it needs energy  
		{
			myBattery.store(panelWattageGenerated);
			if(doLog){Log.write("Extra energy charging battery");} 
		}	

		
	}
	// time_recharge_battery = battery_recharged_time(panel_voltage,battery_recharged_current);
	
	
//	if(Math.abs(difference) < delta){ //essentially zero
//		rpm = myMotor.nextMotor(time, worldEnviro, doLog, netForce, netWeight, CurrentRequested);
//		//put all generated power into the motor. No need to use the battery
//		if(doLog){Log.write("power in = power out");} //mention that. That's cool.
//	}
//	else if(difference<0){ //requested more power than was generated
//		if(myBattery.doesContain(-difference)){ //if the batteries have enough
//			myBattery.draw(Math.abs(difference)); //put all generated power to motor, pull difference from batteries
//			rpm = myMotor.nextMotor(time, worldEnviro, doLog, netForce, netWeight, CurrentRequested);
//		}
//		else{ //provide no power to motor
//			rpm = myMotor.nextMotor(time, worldEnviro, doLog, netForce, netWeight, 0);
//			myBattery.store(panelWattageGenerated);
//			Log.write("Was too much. No power applied to Motor");
//		}
//	}
//	else if(difference>0){ //extra power generated
//		myBattery.store(difference);
//		rpm = myMotor.nextMotor(time, worldEnviro, doLog, netForce, netWeight, wattageRequested);
//	}
	

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


// return: total current (max current) that battery and solar panel can feed to the motor.
private double total_current()
{
	//To do: (Whether/How) Integrate current regenerated from motor;
	return (current_from_panel() + battery_charge_current(voltage_from_panel()));
}



// @return voltage that the panel is able to provide
private double voltage_from_panel()
{
	return myPanels.voltage();
}

//@return total current that the panel is able to provide
private double current_from_panel()
{
	return myPanels.current();
}

//@param The voltage and velocity level under which the motor should run.
//@return Current reuqired to run the motor under given voltage and velocity level.
private double current_needed_for_motor( double voltage, double velocity)
{
	
	return myMotor.current(voltage,velocity);
}




public void current_divide(double current_needed)
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
	// time_recharge_battery = battery_recharged_time(panel_voltage,battery_recharged_current);
	
	
}


//param 
//return 
private double battery_charge_current(double battery_charge_voltage)
{
	return myBattery.charge_current(battery_charge_voltage);
}


private double battery_recharged_time( double voltage_recharged, double current_recharged){
	return ( myBattery.time_recharged(voltage_recharged, current_recharged));
}



}