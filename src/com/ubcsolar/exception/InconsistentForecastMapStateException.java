package com.ubcsolar.exception;

public class InconsistentForecastMapStateException extends Exception {

	private final String forecastRouteName;
	private final String loadedRouteName;
	public InconsistentForecastMapStateException(String forecastRouteName, String currentlyLoadedRouteName) {
		this.forecastRouteName = forecastRouteName;
		this.loadedRouteName = currentlyLoadedRouteName;
	}

	
	public String getForecastRouteName(){
		return this.forecastRouteName;
	}
	
	public String getLoadedRouteName(){
		return this.loadedRouteName;
	}
}
