package com.ubcsolar.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Route extends DataUnit{
	
	public final static String classCSVHeaderRow = "pointNum,"+ GeoCoord.classCSVHeaderRow+","+"distanceFromPrevious, Total Distance";
	/**
	 * turns the class fields into an entry for a csv file
	 * see returnsEntireTable for info on row versus table
	 * @return the row as a string
	 */
	public String getCSVEntry()
	{
		String multiLine = "";
		int entryNum = 0;
		double tempDistance=0;
		GeoCoord lastPoint = null;
		double runningTotalDistance = 0;
		for(GeoCoord g : this.getTrailMarkers()){
			if(lastPoint == null){
				multiLine += (""+entryNum+","+g.getCSVEntry()+","+tempDistance+","+runningTotalDistance+"\r\n");
				lastPoint = g;
			}else{
				tempDistance = lastPoint.calculateDistance(g);
				runningTotalDistance += tempDistance;
				multiLine += (""+entryNum+","+g.getCSVEntry()+","+tempDistance+","+runningTotalDistance+"\r\n");
				lastPoint = g;
			}
			entryNum++;
			
		}
		return multiLine;

	}
	
	/**
	 * gets the column headings as a csv row
	 * @return the row as a string
	 */
	public String getCSVHeaderRow()
	{
		return classCSVHeaderRow;
	}
	
	/**
	 * if the CSV output is multiline rather than a single line
	 * @return 
	 */
	public boolean returnsEntireTable ()
	{
		return true;
	}


	private final ArrayList<GeoCoord> trailMarkers;
	private final String title;
	private final ArrayList<PointOfInterest> pointsOfIntrest;
	private final double timeCreated;
	
	public Route(String title, List<GeoCoord> trailMarkers, List<PointOfInterest> pointsOfIntrest) {
		this.trailMarkers = new ArrayList<GeoCoord>(trailMarkers);
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

	@Override
	public Map<String, ? extends Object> getAllValues() {
		Map<String, Object> toReturn = new HashMap<String, Object>();
		toReturn.put("Trail Markers", trailMarkers);
		toReturn.put("Points Of Intrest", pointsOfIntrest);
		toReturn.put("Title", title);
		toReturn.put("Time Created", timeCreated);
		return toReturn;
	}

}
