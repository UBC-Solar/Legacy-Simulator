package com.ubcsolar.sim;
/**
This class holds the information on the weather, such as sun, wind, and rain
Eventually, we will want to access forecasts via computer and have them
feed into the simulator automatically. 
This is the class that will allow us to do it. 
We can add more information as we get more and more detailed in our factors.
(i.e latitude, altitude etc)
*/

public class Environment{
	private double sunIntensity;/** intensity of the sun in w/square meter*/
	private double sunAngle;/** angle of the sun in degrees. 0/180 is at the horizon, 90 at noon*/
	private int windStrength;/** windstrengh in... mph?*/
	private int windAngle;/**  wind direction in degrees (0-360, 0 being North) */
	private int wetness;/** how wet the track is, scale of 1-100 */
	private double temperature; // the ambient outside temperature

	
/** constructor. No way to change environments, 
 * can only set things when it's created.
 * Will need to add more arguments as this gets more complicated
 * @param sunIntensity - from 1-100, the current intensity of the sun in %. 
 * @param sunAngle - from 0-180, the angle of the sun in degrees
 * @param windStrength - the strength of the wind in mph (whatever they say in forecasts)
 * @param windAngle - the direction of wind in degrees (0-360)
 * @param wetness - from 1-100, how wet the track is. 
*/
public Environment(int sunIntensity, int sunAngle, int windStrength, int windAngle, int wetness,
		float temperature){
	this.sunIntensity = sunIntensity;
	this.sunAngle = sunAngle;
	this.windStrength = windStrength;
	this.windAngle = windAngle;
	this.wetness = wetness;
	this.temperature = temperature;
	Log.write("Environment created");
}

public int getWetness(){
	return wetness;
}

public double getSunIntensity(){
	return sunIntensity;
}

public int getWindAngle(){
	return windAngle;
}
public int windStrength(){
	return windStrength;
	}
public double getSunAngle(){
	return sunAngle;
}
public double getTemperature(){
	return 30;
}
}

