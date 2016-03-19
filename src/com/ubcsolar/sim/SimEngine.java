package com.ubcsolar.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.dvdme.ForecastIOLib.FIOCurrently;
import com.github.dvdme.ForecastIOLib.FIODataBlock;
import com.github.dvdme.ForecastIOLib.FIODataPoint;
import com.github.dvdme.ForecastIOLib.ForecastIO;
import com.ubcsolar.common.ForecastReport;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.LocationReport;
import com.ubcsolar.common.Route;
import com.ubcsolar.common.TelemDataPacket;

public class SimEngine {

	public SimEngine() {
		
	}

	
	public List<SimFrame> runSimulation(Route toTraverse, LocationReport startLocation, ForecastReport weatherReports, TelemDataPacket carStartingCondition, Map<GeoCoord,Double> requestedSpeeds){
	
	List<SimFrame> listOfFrames = new ArrayList<SimFrame>(toTraverse.getTrailMarkers().size());
	
	int startPos = getStartPos(toTraverse.getTrailMarkers(), startLocation.getLocation());
	ForecastIO weather = weatherReports.getForecasts().get(startPos); //assumes that the number of forecasts in weatherReports = number in Route.
	GeoCoord start = toTraverse.getTrailMarkers().get(startPos);
	GeoCoord next = toTraverse.getTrailMarkers().get(startPos + 1);
	Double reqSpeed = requestedSpeeds.get(start);
	TelemDataPacket startCondition = carStartingCondition;
	FIODataPoint startWeather = new FIODataBlock(weather.getHourly()).datapoint(0);
	
	listOfFrames.add(new SimFrame(startWeather, startCondition, startLocation, System.currentTimeMillis())); //starting frame is current. 
	
	
	//generateNextFrame(start, next, reqSpeed, weather, startCondition);
	
	
	
	return listOfFrames;
	}


	private SimFrame generateNextFrame(GeoCoord start, GeoCoord next, Double reqSpeed, ForecastIO weather,
			TelemDataPacket startCondition) {
		// TODO Auto-generated method stub
		return null;
		
	}


	private int getStartPos(ArrayList<GeoCoord> trailMarkers, GeoCoord location) {
		// find the closest point and return the position number. 
		return 0;
	}
}
