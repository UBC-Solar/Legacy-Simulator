package com.ubcsolar.sim;
/** this class will basically become our virtual map. 
Teams will have driven the route and added everything
 about it into their virtual track
 (speed limits, sharp corners, potholes, cattle grates, hills)
The elevation changes and hills are the most important, but knowing
speed limits and other things that would influence our deicisions
is important. 
Noah isn't quite sure what this will end up looking like. We may have a 
pure track (just gps coordinates) and a higher-level map class. 
Or we can roll it all into one. 
*/

import java.util.*;
public class Track{

// --------- Fields ----------- //
	
private double angle ;
private double direction;
private double position;

// -------- Constructor ---------//



public Track(String message, String fileName){
	//TODO load track values from file with the fileName
	System.out.println(message);
}

public List getTrack(){
	//TODO figure out how we're storing the track. And return it. 
	return new ArrayList<Integer>();
}

public double getPosition(){
	return position;
}
public double getDirection(){
	return direction;
}
public double getAngle(){
	return angle;
}
}
