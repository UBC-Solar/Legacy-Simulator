package com.ubcsolar.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.dvdme.ForecastIOLib.FIODataBlock;
import com.github.dvdme.ForecastIOLib.ForecastIO;

public class ForecastReport extends DataUnit {
	
	private final static int numOfHours=4;
	
	private final static String temperatureCSVHeaderRow= "tempHr_0, tempHr_1, tempHr_2,tempHr_3";
	private final static String cloudCoverCSVHeaderRow="cloudHr_0, cloudHr_1,cloudHr_2,cloudHr_3";
	private final static String precipitationCSVHeaderRow="PrecipTypeHr_0,, PrecipTypeHr_1,,PrecipTypeHr_2,,PrecipTypeHr_3,";
	private final static String dewPointCSVHeaderRow="DewPointHr_0, DewPointHr_1,DewPointHr_2,DewPointHr_3";
	private final static String windSpeedCSVHeaderRow="WindSpeedHr_0, WindSpeedHr_1,WindSpeedHr_2,WindSpeedHr_3";

	public final static String classCSVHeaderRow ="pointNum" + "," + temperatureCSVHeaderRow +","
			+ cloudCoverCSVHeaderRow +"," + precipitationCSVHeaderRow +"," 
			+ dewPointCSVHeaderRow +"," +windSpeedCSVHeaderRow; //TODO
	
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
		
		//if (forecasts instanceof ForecastIO){  
		
			if (forecasts.size() != 0){
				
				for ( ForecastIO forecast : forecasts){
					FIODataBlock temp = new FIODataBlock(forecast.getHourly());
					
					toReturn.append(entryNum +",");
					
					for (int i=0;i<numOfHours;i++){
						toReturn.append(temp.datapoint(i).temperature() +",");
						System.out.println("temperature is :" +temp.datapoint(i).temperature());
					}
					
					for (int i=0;i<numOfHours;i++){
						toReturn.append((int) (temp.datapoint(i).cloudCover()*100) + "%" +",");
						System.out.println("cloud % is:" +temp.datapoint(i).cloudCover()*100);
					}
					
					for (int i=0;i<numOfHours;i++){
						toReturn.append(temp.datapoint(i).precipType() +",");
						toReturn.append((int)(temp.datapoint(i).precipProbability()*100) + "%"+",");
						System.out.println("Precipitation Type:" +temp.datapoint(i).precipType());
						System.out.println("Chance of precipitation is:" +temp.datapoint(i).precipProbability()*100 +"%");
					}
					
					for (int i=0;i<numOfHours;i++){
						toReturn.append(temp.datapoint(i).dewPoint() +",");
						System.out.println("DewPoint is:" +temp.datapoint(i).dewPoint());
					}
					
					for (int i=0;i<numOfHours;i++){
						toReturn.append(temp.datapoint(i).windSpeed()+",");
						System.out.println("Wind Speed is:" +temp.datapoint(i).windSpeed());
					}
					
					toReturn.append("\r\n");
					entryNum++;
				}
			}
		//}
		
	//	else if(forecasts instanceof FIODataBlock){
			
	//	}

		return toReturn.toString();
	}
	
	/**
	 * gets the column headings as a csv row
	 * @return the row as a string
	 */
	public String getCSVHeaderRow()
	{
		return classCSVHeaderRow;
	}
	
	/**
	 * if the CSV output is multiline rather than a single line
	 * @return 
	 */
	public boolean returnsEntireTable ()
	{
		return true;
	}

	
	private final List<ForecastIO> forecasts;
	private final long timeCreated;
	private final String routeForecastsWereCreatedFor;

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

}
