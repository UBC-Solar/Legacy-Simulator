package com.ubcsolar.map;

import java.util.Timer;
import java.util.TimerTask;

import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.LocationReport;
import com.ubcsolar.common.Route;

public class GPSFromFakeReciever implements Runnable {
	private final MapController parent;
	private final String carName;
	private final String source;
	private Route route;
	private int current_index = 0;
	private Timer autoGenerate;

	public GPSFromFakeReciever(MapController parent, String carName, String source, Route route) {
		this.parent = parent;
		this.carName = carName;
		this.source = source;
		this.route = route;

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (autoGenerate == null) {
			createNewTimer();
		}
		
		else {
			
		}

	}

	public void stop() {
		autoGenerate.cancel();
	}
	
	private void createNewTimer(){
		Long startDelay = (long) 0.0; //Start immediately 
		Long repetitionDelayInMS = (long) 1000; //once per second seems reasonable. 
												//will need to tune to match the actual speed of the car. 
		autoGenerate = new Timer("TheFakeCar");
		autoGenerate.schedule(new generateNewThings(), startDelay, repetitionDelayInMS);
	}

	public void GPSEventJustCameIn() {
		// GPS coordinate randomly picked (in the mid-east states)
		// CarLocation(GeoCoord location, String carName, String source, double
		// timeCreated)
		double speed = 200.0; //km/hr
		double distance_per_timestep = speed/3600; //convert into km/s to see how much distance to travel every second
		GeoCoord current_location = this.route.getTrailMarkers().get(current_index); //get the current position of the car
		int next_index = 0; 
		
		//go through all the points of the route, starting from the one right next to the current position
		//start iterating through the points in a "forward direction" until the distance between the current checked point 
		//and the point where the car is is greater than the distance per timestep
		//the loop supports "wraparound" so the fake car goes round and round the track
		for (int i = 0; i < this.route.getTrailMarkers().size(); i++) {
			GeoCoord next_location = this.route.getTrailMarkers().get((current_index + i)%this.route.getTrailMarkers().size());
			if (current_location.calculateDistance(next_location) > distance_per_timestep) {
				next_index = (current_index + i)%this.route.getTrailMarkers().size(); //keep track of point where the car should be next
				break;
			}
		}
		current_index = next_index; //keep track of new position
		
		//move the car
		parent.recordNewCarLocation(
				new LocationReport(this.route.getTrailMarkers().get(current_index), this.carName, this.source, System.currentTimeMillis()));
	}
	

	private class generateNewThings extends TimerTask {

		/**
		 * This method is what the timer calls. Calls the method in parent class
		 * to handle the new packet. (analogy is that the parent class
		 * 'receives' a new packet that we programmed instead of the car).
		 */
		@Override
		public void run() {
			GPSEventJustCameIn();

		}
	}
}