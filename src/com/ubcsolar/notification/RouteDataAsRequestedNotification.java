/**
 * this notification is sent when a UI element requests Point data.  
 */
package com.ubcsolar.notification;

import java.util.ArrayList;

import com.ubcsolar.common.DistanceUnit;
import com.ubcsolar.common.GeoCoord;

/**
 * @author Noah
 *
 */
public class RouteDataAsRequestedNotification extends Notification {
	private ArrayList<GeoCoord> listOfPoints;
	private int numOfDistanceRequested; //-1 if it's all points, or distance doesn't matter. 
	private DistanceUnit unitMeasuredBy; //Kilometers by default, but meaningless if distance requested is '-1'
	
	/**
	 * Default constructor
	 * @param geoCoords - the list of points needed
	 * @param numOfDistanceRequested - the distance requested
	 * @param unit - the unit the distance requested was measured in. 
	 */
	public RouteDataAsRequestedNotification(ArrayList<GeoCoord>geoCoords, int numOfDistanceRequested, DistanceUnit unit){
		initialize(geoCoords, numOfDistanceRequested, unit);
	}
	
	/**
	 * Constructor that will assume all points being given. 
	 * Initializes as if -1 kilometers requested. 
	 * @param geoCoords - all of the points. 
	 */
	public RouteDataAsRequestedNotification(ArrayList<GeoCoord>geoCoords){
		initialize(geoCoords, -1, DistanceUnit.KILOMETERS);
		
		
	}
	
	/**
	 * sets everything in the class as needed.
	 * @param geoCoords
	 * @param numOfDistanceRequested
	 * @param unit
	 */
	private void initialize(ArrayList<GeoCoord> geoCoords,
			int numOfDistanceRequested, DistanceUnit unit) {
		this.listOfPoints = geoCoords;
		this.numOfDistanceRequested = numOfDistanceRequested;
		this.unitMeasuredBy = unit;
		
	}

	/* 
	 * @see com.ubcsolar.notification.Notification#getMessage()
	 */
	@Override
	public String getMessage() {
		return "Sending points for the first " + this.numOfDistanceRequested + " " + this.unitMeasuredBy + ".";
	}

	/**
	 * @return the unitMeasuredBy
	 */
	public DistanceUnit getUnitMeasuredBy() {
		return unitMeasuredBy;
	}


	/**
	 * NOTE: If num of Distance was not specified,
	 * a value of -1 is returned.
	 * @return the numOfDistanceRequested
	 */
	public int getNumOfDistanceRequested() {
		return numOfDistanceRequested;
	}



	/**
	 * @return the listOfPoints
	 */
	public ArrayList<GeoCoord> getListOfPoints() {
		return listOfPoints;
	}


}
