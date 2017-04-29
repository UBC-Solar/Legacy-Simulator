package com.ubcsolar.sim;

import java.util.List;
import java.util.Map;

import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.SimFrame;
import com.ubcsolar.common.TelemDataPacket;

public class SimResult {
	private List<SimFrame> listOfFrames;
	private long travelTime;
	private TelemDataPacket endTelemData;
	private Map<GeoCoord, Double> speedProfile;
	private boolean runSuccessful;
	
	public SimResult(List<SimFrame> frameList, long totalTime, TelemDataPacket endPacket, Map<GeoCoord, Double> speedProfile, boolean runSuccessful){
		listOfFrames = frameList;
		travelTime = totalTime;
		endTelemData = endPacket;
		this.speedProfile = speedProfile;
		this.runSuccessful = runSuccessful;
	}
	
	public List<SimFrame> getListOfFrames(){
		return listOfFrames;
	}
	
	public long getTravelTime(){
		return travelTime;
	}
	
	public TelemDataPacket getFinalTelemData(){
		return endTelemData;
	}

	public Map<GeoCoord, Double> getSpeedProfile() { return speedProfile; }

	public boolean wasRunSuccessful() { return runSuccessful; }
}
