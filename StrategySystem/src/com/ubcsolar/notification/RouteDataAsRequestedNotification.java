/**
 * this notification is sent when a UI element requests Point data.  
 */
package com.ubcsolar.notification;

import java.util.ArrayList;

import com.ubcsolar.common.DistanceUnit;
import com.ubcsolar.map.Point;

/**
 * @author Noah
 *
 */
public class RouteDataAsRequestedNotification extends Notification {
	private ArrayList<Point> listOfPoints;
	private int numOfDistanceRequested; //-1 if it's all points, or distance doesn't matter. 
	private DistanceUnit unitMeasuredBy; //Kilometers by default, but meaningless if distance requested is '-1'
	
	/**
	 * Default constructor
	 * @param points - the list of points needed
	 * @param numOfDistanceRequested - the distance requested
	 * @param unit - the unit the distance requested was measured in. 
	 */
	public RouteDataAsRequestedNotification(ArrayList<Point>points, int numOfDistanceRequested, DistanceUnit unit){
		initialize(points, numOfDistanceRequested, unit);
	}
	
	/**
	 * Constructor that will assume all points being given. 
	 * Initializes as if -1 kilometers requested. 
	 * @param points - all of the points. 
	 */
	public RouteDataAsRequestedNotification(ArrayList<Point>points){
		initialize(points, -1, DistanceUnit.KILOMETERS);
		
		
	}
	
	/**
	 * sets everything in the class as needed.
	 * @param points
	 * @param numOfDistanceRequested
	 * @param unit
	 */
	private void initialize(ArrayList<Point> points,
			int numOfDistanceRequested, DistanceUnit unit) {
		this.listOfPoints = points;
		this.numOfDistanceRequested = numOfDistanceRequested;
		this.unitMeasuredBy = unit;
		
	}

	/* 
	 * @see com.ubcsolar.notification.Notification#getMessage()
	 */
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return null;
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
	public ArrayList<Point> getListOfPoints() {
		return listOfPoints;
	}


}
