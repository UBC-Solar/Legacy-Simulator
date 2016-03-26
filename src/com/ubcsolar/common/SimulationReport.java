package com.ubcsolar.common;

import java.util.List;
import java.util.Map;

public class SimulationReport extends DataUnit {
	private final List<SimFrame> simData;
	public SimulationReport(List<SimFrame> simData, String info) {
		this.simData = simData;
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

}
