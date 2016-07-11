package com.ubcsolar.weather;

import com.ubcsolar.Main.GlobalValues;
import com.ubcsolar.common.GeoCoord;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.github.dvdme.ForecastIOLib.ForecastIO;

public class ForecastIOFactory {
	
	//changeable by user
	private double latitude = 0;
	private double longitude = 0;
	private double precipProbability = 0;
	private String precipType = "rain";
	private double temperature = 0;
	private double apparentTemperature = 0;
	private double dewPoint = 0;
	private double humidity = 0;
	private double windSpeed = 0;
	private double windBearing = 0;
	private double cloudCover = 0;
	private String icon = "cloudy";
	private double stormBearing =0;
	private double stormDistance =0;

	
	//fixed
	private String timezone = "AMERICA";
	private int offset = -6;
	private String summary = "TEST";
	private int time = 0;
	private double precipIntensity = 0;
	private double visibility = 0;
	private double pressure = 0;
	private double ozone = 0;
	
	private final int NUM_HOURS_NEEDED = 4;
	
	public ForecastIOFactory location(GeoCoord location){
		this.latitude = location.getLat();
		this.longitude = location.getLon();
		return this;
	}
	
	public ForecastIOFactory temperature(double temperature){
		this.temperature = temperature;
		this.apparentTemperature = temperature;
		return this;
	}
	
	/**
	 * 
	 * @param cloudCover: must be a value between 0 and 1 inclusive
	 * 
	 * @return
	 */
	public ForecastIOFactory cloudCover(double cloudCover){
		this.cloudCover = cloudCover;
		
		if (cloudCover < 0.15){
			this.icon = "Sunny";
		}
		else if (cloudCover >=0.15 && cloudCover < 0.50){
			this.icon = "Partly Cloudy";
		}
		else if (cloudCover >= 0.50 &&  cloudCover < 0.90 ){
			this.icon = "Mostly cloudy";
		}
		else if( cloudCover >= 0.90){
			this.icon = "Cloudy";
		}
		return this;
	}
	
	public ForecastIOFactory dewPoint(double dewPoint){
		this.dewPoint = dewPoint;
		return this;
	}
	
	public ForecastIOFactory humidity(double humidity){
		this.humidity = humidity;
		return this;
	}
	
	public ForecastIOFactory stormDistance (double stormDistance ){
		this.stormDistance = stormDistance;
		return this;
	}
	
	public ForecastIOFactory stormBearing (double stormBearing ){
		this.stormBearing = stormBearing;
		return this;
	}
	
	public ForecastIOFactory windSpeed(double windSpeed){
		this.windSpeed = windSpeed;
		return this;
	}
	
	public ForecastIOFactory windBearing(double windBearing){
		this.windBearing = windBearing;
		return this;
	}
	
	
	/**
	 * 
	 * @param precipitationProbability: must be between 0 and 1 inclusive
	 * @return
	 */
	public ForecastIOFactory precipProb(double precipitationProbability){
		this.precipProbability = precipitationProbability;
		return this;
	}
	
	public ForecastIOFactory precipType(String precipType){
		this.precipType = precipType;
		return this;
	}
	
	public ForecastIO build(){
		JsonObject forecastInfo = new JsonObject();
		forecastInfo.add("latitude", latitude);
		forecastInfo.add("longitude", longitude);
		forecastInfo.add("timezone", timezone);
		forecastInfo.add("offset", offset);
		
		
		JsonObject currentlyDataPoint = buildDataArrayEntry("currently");
		forecastInfo.add("currently", currentlyDataPoint);
		
		JsonObject hourlyForecast = new JsonObject();
		hourlyForecast.add("summary", summary);
		hourlyForecast.add("icon", icon);
		
		JsonArray hourlyArray = new JsonArray();
		JsonObject hourlyDataPoint = buildDataArrayEntry("hourly");
		
		for(int i = 0; i < NUM_HOURS_NEEDED; i++){
			hourlyArray.add(hourlyDataPoint);
		}
		//TODO: decide whether need to change the time in entries in the dataArray
		
		hourlyForecast.add("data", hourlyArray);
		forecastInfo.add("hourly", hourlyForecast);
		
		JsonObject dailyForecast = new JsonObject();
		dailyForecast.add("summary", summary);
		dailyForecast.add("icon", icon);
		
		JsonArray dailyArray = new JsonArray();
		JsonObject dailyDataPoint = buildDataArrayEntry("daily");
		for(int i = 0; i < NUM_HOURS_NEEDED; i++){ // TODO, why is it no. of hours? why not days?
			dailyArray.add(dailyDataPoint);
		}
		dailyForecast.add("data", dailyArray);
		forecastInfo.add("daily", dailyForecast);
		
		forecastInfo.add("flags", buildFlagsObject());
		
		ForecastIO forecast = new ForecastIO(GlobalValues.WEATHER_KEY);
		forecast.getForecast(forecastInfo);
		
		return forecast;
		
	}
	
	private JsonObject buildFlagsObject(){
		JsonArray sourcesArray = new JsonArray();
		sourcesArray.add("gfs");
		sourcesArray.add("cmc");
		sourcesArray.add("nam");
		sourcesArray.add("rap");
		sourcesArray.add("rtma");
		sourcesArray.add("sref");
		sourcesArray.add("fnmoc");
		sourcesArray.add("isd");
		sourcesArray.add("nwspa");
		sourcesArray.add("madis");
		
		JsonArray isdArray = new JsonArray();
		isdArray.add("712350-99999");
		isdArray.add("713930-99999");
		isdArray.add("718600-99999");
		isdArray.add("718770-99999");
		isdArray.add("718776-99999");
		
		JsonArray madisArray = new JsonArray();
		madisArray.add("BLDQ1");
		madisArray.add("CYBW");
		madisArray.add("CYYC");
		madisArray.add("D4846");
		madisArray.add("D4993");
		madisArray.add("D7650");
		madisArray.add("E0424");
		madisArray.add("E1159");
		madisArray.add("E1808");
		madisArray.add("E2194");
		madisArray.add("E5375");
		madisArray.add("E5393");
		madisArray.add("E7122");
		madisArray.add("E7132");
		madisArray.add("NEIQ1");
		madisArray.add("PRIQ1");
		
		JsonObject flags = new JsonObject();
		flags.add("sources", sourcesArray);
		flags.add("isd-stations", isdArray);
		flags.add("madis-stations", madisArray);
		flags.add("units", "si");
		
		return flags;
	}
	
	private JsonObject buildDataArrayEntry(String timeScale){
		JsonObject dataArrayEntry = new JsonObject();
		if(timeScale.equals("currently")){
			dataArrayEntry.add("time", time);
			dataArrayEntry.add("summary", summary);
			dataArrayEntry.add("icon", icon);
			dataArrayEntry.add("precipIntensity", precipIntensity);
			dataArrayEntry.add("precipProbability", precipProbability);
			dataArrayEntry.add("precipType", precipType);
			dataArrayEntry.add("temperature", temperature);
			dataArrayEntry.add("apparentTemperature", apparentTemperature);
			dataArrayEntry.add("dewPoint", dewPoint);
			dataArrayEntry.add("humidity", humidity);
			dataArrayEntry.add("windSpeed", windSpeed);
			dataArrayEntry.add("windBearing", windBearing);
			dataArrayEntry.add("nearestStormDistance", stormDistance);
			dataArrayEntry.add("nearestStormBearing", stormBearing);
			dataArrayEntry.add("visibility", visibility);
			dataArrayEntry.add("cloudCover", cloudCover);
			dataArrayEntry.add("pressure", pressure);
			dataArrayEntry.add("ozone", ozone);
		}else if(timeScale.equals("hourly")){
			dataArrayEntry.add("time", time);
			dataArrayEntry.add("summary", summary);
			dataArrayEntry.add("icon", icon);
			dataArrayEntry.add("precipType", precipType);
			dataArrayEntry.add("precipIntensity", precipIntensity);
			dataArrayEntry.add("precipProbability", precipProbability);
			dataArrayEntry.add("temperature", temperature);
			dataArrayEntry.add("apparentTemperature", apparentTemperature);
			dataArrayEntry.add("dewPoint", dewPoint);
			dataArrayEntry.add("humidity", humidity);
			dataArrayEntry.add("windSpeed", windSpeed);
			dataArrayEntry.add("windBearing", windBearing);
			dataArrayEntry.add("nearestStormDistance", stormDistance);
			dataArrayEntry.add("nearestStormBearing", stormBearing);
			dataArrayEntry.add("visibility", visibility);
			dataArrayEntry.add("cloudCover", cloudCover);
			dataArrayEntry.add("pressure", pressure);
			dataArrayEntry.add("ozone", ozone);
		}else if(timeScale.equals("daily")){
			dataArrayEntry.add("time", time);
			dataArrayEntry.add("summary", summary);
			dataArrayEntry.add("icon", icon);
			dataArrayEntry.add("precipIntensity", precipIntensity);
			dataArrayEntry.add("precipProbability", precipProbability);
			dataArrayEntry.add("precipType", precipType);
			dataArrayEntry.add("dewPoint", dewPoint);
			dataArrayEntry.add("humidity", humidity);
			dataArrayEntry.add("windSpeed", windSpeed);
			dataArrayEntry.add("windBearing", windBearing);
			dataArrayEntry.add("nearestStormDistance", stormDistance);
			dataArrayEntry.add("nearestStormBearing", stormBearing);
			dataArrayEntry.add("visibility", visibility);
			dataArrayEntry.add("cloudCover", cloudCover);
			dataArrayEntry.add("pressure", pressure);
			dataArrayEntry.add("ozone", ozone);
			dataArrayEntry.add("sunriseTime", 0);
			dataArrayEntry.add("sunsetTime", 100);
			dataArrayEntry.add("moonPhase", 0);
			dataArrayEntry.add("precipIntensityMax", precipIntensity);
			dataArrayEntry.add("precipIntensityMaxTime", 100);
			dataArrayEntry.add("temperatureMin", temperature);
			dataArrayEntry.add("temperatureMinTime", 0);
			dataArrayEntry.add("temperatureMax", temperature);
			dataArrayEntry.add("temperatureMaxTime", 100);
			dataArrayEntry.add("apparentTemperatureMin", temperature);
			dataArrayEntry.add("apparentTemperatureMinTime", 0);
			dataArrayEntry.add("apparentTemperatureMax", temperature);
			dataArrayEntry.add("apparentTemperatureMaxTime", 100);
		}
		return dataArrayEntry;
	}
	
}
