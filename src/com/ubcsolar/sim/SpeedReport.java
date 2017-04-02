package com.ubcsolar.sim;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.ubcsolar.common.GeoCoord;

public class SpeedReport {
	private Map<GeoCoord, Double> SpeedProfile;
	private SimResult Result;
	private double speed;
	
	public SpeedReport(Map<GeoCoord, Double> profile, SimResult result, double speed) {
		this.SpeedProfile = new LinkedHashMap<GeoCoord, Double>(profile);
		this.Result = result;
		this.speed = speed;
	}
	
	public SpeedReport(SpeedReport report) {
		this.SpeedProfile = new LinkedHashMap<GeoCoord, Double>(report.getSpeedProfile());
		this.Result = report.getSpeedResult();
		this.speed = report.getSpeed();
	}
	
	public Map<GeoCoord,Double> getSpeedProfile() {
		return new LinkedHashMap<GeoCoord, Double>(this.SpeedProfile);
	}
	
	public SimResult getSpeedResult() {
		return this.Result;
	}
	public double getSpeed() {
		return this.speed;
	}
			
}
