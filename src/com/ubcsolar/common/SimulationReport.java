package com.ubcsolar.common;

import java.util.List;
import java.util.Map;

public class SimulationReport extends DataUnit {
	private final List<SimFrame> simData;
	private final Map<GeoCoord, Double> manuallyRequestedSpeeds;
	
	
	public SimulationReport(List<SimFrame> simData, Map<GeoCoord, Double> requestedSpeeds, String info) {
		this.simData = simData;
		this.manuallyRequestedSpeeds = requestedSpeeds;
	}
	
	public List<SimFrame> getSimFrames(){
		return simData;
	}
	
	@Override
	public double getTimeCreated() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<String, ? extends Object> getAllValues() {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<GeoCoord, Double> getManuallyRequestedSpeeds() {
		return manuallyRequestedSpeeds;
	}

}
