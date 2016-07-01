package com.ubcsolar.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.github.dvdme.ForecastIOLib.FIODataBlock;
import com.github.dvdme.ForecastIOLib.ForecastIO;

public class ForecastReport extends DataUnit {
	private final List<ForecastIO> forecasts;
	private final long timeCreated;
	private final String routeForecastsWereCreatedFor;
	private final String MAP_NAME_JSON_KEY = "mapName";
	private final String FC_LIST_SIZE_KEY = "NumOfForecasts";
	private final String TIME_CREATED_KEY = "TimeCreated";

	/**
	 * 
	 * @param forecasts
	 * @param routeName the name of the route, or NULL if it was deleted.
	 * @param timeCreated
	 */
	public ForecastReport(List<ForecastIO> forecasts, String routeName, long timeCreated) {
		this.forecasts = new ArrayList<ForecastIO>(forecasts);
		this.timeCreated = timeCreated;
		this.routeForecastsWereCreatedFor = routeName;
	}
	
	public ForecastReport(List<ForecastIO> forecasts, String routeName) {
		this.forecasts = new ArrayList<ForecastIO>(forecasts);
		this.timeCreated = System.currentTimeMillis();
		this.routeForecastsWereCreatedFor = routeName;
	}


	@Override
	public double getTimeCreated() {	
		return timeCreated;
	}

	@Override
	public Map<String, ? extends Object> getAllValues() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getRouteNameForecastsWereCreatedFor() {
		return routeForecastsWereCreatedFor;
	}

	public List<ForecastIO> getForecasts() {
		return forecasts;
	}

	public JSONObject toJSON(){
		JSONObject temp = new JSONObject();
		if(this.routeForecastsWereCreatedFor == null){
			temp.put(MAP_NAME_JSON_KEY, "null");
		}
		else{
			temp.put(MAP_NAME_JSON_KEY, routeForecastsWereCreatedFor);
		}
		temp.put(FC_LIST_SIZE_KEY, this.forecasts.size());
		temp.put(TIME_CREATED_KEY,this.timeCreated);
		for(int i = 0; i<this.forecasts.size(); i++){
			temp.put(String.valueOf(i),this.forecasts.get(i).getRawResponse());
		}
		return temp;
	}
	
	public ForecastReport(JSONObject jsonForecastReport){
		String mapName = jsonForecastReport.getString(this.MAP_NAME_JSON_KEY);
		if(mapName.equals("null")){
			this.routeForecastsWereCreatedFor = null; 
		}else{
			this.routeForecastsWereCreatedFor = mapName;
		}
		this.timeCreated = jsonForecastReport.getLong(this.TIME_CREATED_KEY);
		ArrayList<ForecastIO> retreivedForecasts = new ArrayList<ForecastIO>();
		for(int i = 0; i<jsonForecastReport.getInt(this.FC_LIST_SIZE_KEY); i++){
			retreivedForecasts.add(new ForecastIO(jsonForecastReport.getString(String.valueOf(i))));
		}
		this.forecasts = retreivedForecasts;
	}
	
	/**
	 * turns the class fields into an entry for a csv file
	 * see returnsEntireTable for info on row versus table
	 * @return the row as a string
	 */
	public String getCSVEntry()
	{
		StringBuilder toReturn =new StringBuilder( "");
		int entryNum = 0;
		double tempDistance=0;
		GeoCoord lastPoint = null;
		double runningTotalDistance = 0;
		
		System.out.println("size of forecast:"+forecasts.size());

			if (forecasts.size() != 0){
				
				for ( ForecastIO forecast : forecasts){
					toReturn.append(entryNum +",,");
					toReturn.append(WeatherPrinter.getCSVEntryForForecastIO(forecast));
					toReturn.append("\r\n");
					entryNum++;
				}
			}
			
		return toReturn.toString();
	}
	
	/**
	 * gets the column headings as a csv row
	 * @return the row as a string
	 */
	public String getCSVHeaderRow()
	{
		StringBuilder toReturn =new StringBuilder( "");

		if (forecasts.size()== 0){
			return toReturn.toString(); 
		}
		
		FIODataBlock temp = new FIODataBlock(forecasts.get(0).getHourly());
		
		int numOfHours = temp.datablockSize();
		
		String DataPointHeader = WeatherPrinter.getCSVHeaderRowForForecastIO();
		
		toReturn.append("pointNum" );
		
		for (int i=0;i<numOfHours;i++){
			toReturn.append(",Hour_"+ i+","+ DataPointHeader);
		}
		
		return toReturn.toString();
	}
	
	/**
	 * if the CSV output is multiline rather than a single line
	 * @return 
	 */
	public boolean returnsEntireTable (){
		return true;
	}
	
	
}
