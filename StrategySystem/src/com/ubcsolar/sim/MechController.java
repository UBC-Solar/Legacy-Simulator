package com.ubcsolar.sim;

public class MechController {
	private double speed;

	public MechController(double speed){
		speed = 0.0;

	}
	public MechController(Track newTrack){
		speed = 0.0;
	}
	
public double nextMech(double tourque){
	return 5; //km/h
}
public double getAngularVelocity(){
	return 200.0;
}
public double calculateResistence(){
	return 750;
}

	
}
