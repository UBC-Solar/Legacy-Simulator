package com.ubcsolar.weather;

import java.util.ArrayList;
import java.util.List;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.github.dvdme.ForecastIOLib.ForecastIO;
import com.ubcsolar.Main.GlobalValues;
import com.ubcsolar.common.GeoCoord;

public class ForecastIOFactory2 {
	private static double latitude = 0;
	private static double longitude = 0;
	private static String timezone = "AMERICA";
	private static int offset = -6;
	private static List<JsonObject> datapoints;
	
	public static void addDatapoints(List<JsonObject> newDatapoints){
		datapoints = new ArrayList<JsonObject>(newDatapoints);

		for(int i = 1; i < datapoints.size() - 1; i++){
			int j = i;
			while(j>0 && Integer.parseInt(datapoints.get(j).get("time").toString())
				< Integer.parseInt(datapoints.get(j-1).get("time").toString())){
					JsonObject temp = datapoints.get(j-1);
					datapoints.set(j-1, datapoints.get(j));
					datapoints.set(j, temp);
					j = j-1;
			}
		}
	}
	
	public static void changeLocation(GeoCoord loc){
		latitude = loc.getLat();
		longitude = loc.getLon();
	}
	
	public static ForecastIO build(){
		JsonObject forecastInfo = new JsonObject();
		forecastInfo.add("latitude", latitude);
		forecastInfo.add("longitude", longitude);
		forecastInfo.add("timezone", timezone);
		forecastInfo.add("offset", offset);
		
		forecastInfo.add("currently", datapoints.get(0));
		
		JsonObject hourlyForecast = new JsonObject();
		hourlyForecast.add("summary", datapoints.get(0).get("summary"));
		hourlyForecast.add("icon", datapoints.get(0).get("summary"));
		
		JsonArray hourlyArray = new JsonArray();
		for(int i = 0; i < datapoints.size(); i++){
			hourlyArray.add(datapoints.get(i));
		}
		
		hourlyForecast.add("data", hourlyArray);
		forecastInfo.add("hourly", hourlyForecast);
		
		forecastInfo.add("flags", buildFlagsObject());
		
		ForecastIO forecast = new ForecastIO(GlobalValues.WEATHER_KEY);
		forecast.getForecast(forecastInfo);
		
		return forecast;
	}
	
	private static JsonObject buildFlagsObject(){
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
		flags.add("units", "ca");
		
		return flags;
	}
}
