// last edited on Saturday, Nov 2
// To-Do add coments. Debug and test
public class MotorBasic {
//------------------ CLASS FIELDS ----------------------------------------
	public double voltage;		// Temperature, as a safeguard for the motors
	public double velocity;			// The speed of the car
	// pass voltage, velocity - give back current

	//---------- END OF CLASS FIELDS, START OF CONSTRUCTORS ---------------------------------

	public Motor(double voltage, double velocity){
    }
	
//------------ END OF CONSTRUCTORS, START OF METHODS -------------------------------------
		
	public double findNoLoadSpeed(double voltage, double noLoadSpeedConstant){
		double noLoadSpeed;
		noLoadSpeed = voltage * noLoadSpeedConstant;
		return NoLoadSpeed; 		// Predicted by tests; a function of Voltage i.e. higher voltage means higher stallTorque
	}

	public double findStallTorque(double voltage, double stallTorqueConstant){
		double stallTorque;
		stallTorque = voltage * stallTorqueConstant;
		return stallTorque						// Predicted by tests; a function of Voltage i.e. higher voltage means higher noLoadSpeed
	}
	
	public double findTorque(voltage, velocity){
		double stallTorque;
		double noLoadSpeed;
		double torque;
		double c;								// slope of velocity vs torque graph
		stallTorque = findStallTorque(voltage, 1);
		noLoadSpeed = findNoLoadSpeed(voltage, 1);
		c = stallTorque/noLoadSpeed;
		Torque = stallTorque - c * velocity;
		return torque;
	}
	
	public double current(volage, velocity){
		double torqueConstant = 7; 				// random number
		double current;
		current = findTorque(voltage, velocity) * torqueConstant;
		return current;
	}
		
	// a method for calculating current provided by regenerative braking
	public double regenCurrent(double torque){
		double current;
		return regenCurrent;
	} 
	
	// a method for calculating voltage provided by regenerative braking
	public double regenVoltage(double torque){
		double regenVoltage
		return regenVoltage
	}
	
	// a method for calculating force given torque of the motors and radius - for car class
	public double force(double torque, double radius){
		double force
		return force;
	}	
}