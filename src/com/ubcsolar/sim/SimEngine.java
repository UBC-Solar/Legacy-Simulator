package com.ubcsolar.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ubcsolar.common.ForecastReport;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.LocationReport;
import com.ubcsolar.common.Route;
import com.ubcsolar.common.TelemDataPacket;

public class SimEngine {

	public SimEngine(String infoStuff) {
		
	}

	
	public List<SimFrame> runSimulation(Route toTraverse, LocationReport startLocation, ForecastReport weatherReports, TelemDataPacket carStartingCondition, Map<GeoCoord,Double> requestedSpeeds){
	
	List<SimFrame> toReturn = new ArrayList<SimFrame>(toTraverse.getTrailMarkers().size());
	
	
	
	return toReturn;
	}
}
