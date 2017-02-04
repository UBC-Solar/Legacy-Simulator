package com.ubcsolar.sim;

import java.util.HashMap;
import java.util.Map;

import com.ubcsolar.common.GeoCoord;

public class SpeedReport {
	private Map<GeoCoord, Double> SpeedProfile;
	private SimResult Result;
	
	public SpeedReport(Map<GeoCoord, Double> profile, SimResult result) {
		this.SpeedProfile = new HashMap<GeoCoord, Double>(profile);
		this.Result = result;
	}
	
	public Map<GeoCoord,Double> getSpeedProfile() {
		return new HashMap<GeoCoord, Double>(this.SpeedProfile);
	}
	
	public SimResult getSpeedResult() {
		return this.Result;
	}
			
}
