package com.ubcsolar.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Route extends DataUnit{

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
	
	public GeoCoord getClosestPointOnRoute(GeoCoord location){
		double minimumDistance = location.calculateDistance(trailMarkers.get(0));
		int minimumIndex = 0;
		for(int i = 1; i < trailMarkers.size(); i++){
			double currentDistance = location.calculateDistance(trailMarkers.get(i));
			if(currentDistance < minimumDistance){
				minimumDistance = currentDistance;
				minimumIndex = i;
			}
		}
		return trailMarkers.get(minimumIndex);
	}

}
