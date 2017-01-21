package com.ubcsolar.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.ubcsolar.sim.SimEngine;

public class Route extends DataUnit {

	public final static String classCSVHeaderRow = "pointNum" + "," + GeoCoord.classCSVHeaderRow + ","
			+ "distanceFromPrevious, Total Distance";

	/**
	 * turns the class fields into an entry for a csv file see
	 * returnsEntireTable for info on row versus table
	 * 
	 * @return the row as a string
	 */
	public String getCSVEntry() {
		StringBuilder multiLine = new StringBuilder("");
		int entryNum = 0;
		double tempDistance = 0;
		GeoCoord lastPoint = null;
		double runningTotalDistance = 0;
		for (GeoCoord g : this.getTrailMarkers()) {
			if (lastPoint == null) {
				multiLine.append("" + entryNum + "," + g.getCSVEntry() + "," + tempDistance + "," + runningTotalDistance
						+ "\r\n");
				lastPoint = g;
			} else {
				tempDistance = lastPoint.calculateDistance(g);
				runningTotalDistance += tempDistance;
				multiLine.append("" + entryNum + "," + g.getCSVEntry() + "," + tempDistance + "," + runningTotalDistance
						+ "\r\n");
				lastPoint = g;
			}
			entryNum++;

		}
		return multiLine.toString();

	}

	/**
	 * gets the column headings as a csv row
	 * 
	 * @return the row as a string
	 */
	public String getCSVHeaderRow() {
		return classCSVHeaderRow;
	}

	/**
	 * if the CSV output is multiline rather than a single line
	 * 
	 * @return
	 */
	public boolean returnsEntireTable() {
		return true;
	}

	private final ArrayList<GeoCoord> trailMarkers;
	private final String title;
	private final ArrayList<PointOfInterest> pointsOfIntrest;
	private final double timeCreated;
	
	public Route(String title, List<GeoCoord> trailMarkers, List<PointOfInterest> pointsOfIntrest) {
		this.trailMarkers = new ArrayList<GeoCoord>(trailMarkers);
		
		
/******************************************************************************************************************************/
		Set<GeoCoord> bridge = new HashSet<GeoCoord>(); //keeps track of all coordinates that are on a bridge
		Boolean onBridge = false; //keeps track on whether or not the checked point is part of a bridge
		
		
		for (int i = 1; i < this.trailMarkers.size(); i++) { 
			//get angle of inclination of current point relative to previous point
			double angle = SimEngine.getInclinationAngle(this.trailMarkers.get(i - 1), this.trailMarkers.get(i))*(180.0/Math.PI);
			
			//check if angle of inclination is more than 20 degrees (downwards) and point is not on bridge
			if (angle < -20 && onBridge == false) {
				System.out.println("on bridge: " + angle + ": " + this.trailMarkers.get(i).getCSVEntry());
				//point is probably start of bridge
				onBridge = true;
				bridge.add(this.trailMarkers.get(i));
				
			}
			
			
			else if (onBridge == true && angle > 1) {
				System.out.println("off bridge: " + angle + ": " + this.trailMarkers.get(i).getCSVEntry());
				//bridge.add(this.trailMarkers.get(i));
				//if onBridge is true (prev. point is on bridge) and current angle inclination is > 1, it is probably the end of the bridge
				onBridge = false;
			}
			
			else if (onBridge == true) {
				//if previous point is on bridge and current angle inclination is < 1, add point to bridge
				bridge.add(this.trailMarkers.get(i));
			}
			
		}
		
		//remove all (wrong) points that are on bridge
		for (GeoCoord g : bridge) {
			this.trailMarkers.remove(g);
		}
/*****************************************************************************************************************************************/
		this.title = title;
		this.pointsOfIntrest = new ArrayList<PointOfInterest>(pointsOfIntrest);
		timeCreated = System.currentTimeMillis();
	}

	public ArrayList<GeoCoord> getTrailMarkers() {
		return new ArrayList<GeoCoord>(trailMarkers);
	}

	public String getTitle() {
		return title;
	}

	public ArrayList<PointOfInterest> getPointsOfIntrest() {
		return new ArrayList<PointOfInterest>(pointsOfIntrest);
	}

	@Override
	public double getTimeCreated() {
		return timeCreated;
	}

	public Map<String, ? extends Object> getAllValues() {
		Map<String, Object> toReturn = new HashMap<String, Object>();
		toReturn.put("Trail Markers", trailMarkers);
		toReturn.put("Points Of Intrest", pointsOfIntrest);
		toReturn.put("Title", title);
		toReturn.put("Time Created", timeCreated);
		return toReturn;
	}

	public GeoCoord getClosestPointOnRoute(GeoCoord location) {
		double minimumDistance = location.calculateDistance(trailMarkers.get(0));
		int minimumIndex = 0;
		for (int i = 1; i < trailMarkers.size(); i++) {
			double currentDistance = location.calculateDistance(trailMarkers.get(i));
			if (currentDistance < minimumDistance) {
				minimumDistance = currentDistance;
				minimumIndex = i;
			}
		}
		return trailMarkers.get(minimumIndex);
	}

}
