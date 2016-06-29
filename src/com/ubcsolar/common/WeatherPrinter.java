package com.ubcsolar.common;

import com.github.dvdme.ForecastIOLib.FIODataBlock;
import com.github.dvdme.ForecastIOLib.FIODataPoint;
import com.github.dvdme.ForecastIOLib.ForecastIO;



public class WeatherPrinter {
	
	public final static String weatherDataPointHeaderRow ="Temp, Cloud_Cover, Precip Type, Precip Chance, Dew point, Wind Speed";

	private static int numOfHours;
			
	public static String getCSVEntryForForecastIO(ForecastIO forecast){
		
		
		StringBuilder toReturn =new StringBuilder("");

	
		FIODataBlock temp = new FIODataBlock(forecast.getHourly());
		
		numOfHours = temp.datablockSize();
		
		for (int i=0;i<numOfHours;i++){
			
			if (i > 0){
				toReturn.append(",");
			}
			
			toReturn.append(","+ getCSVRowForFIODataPoint(temp.datapoint(i)));
		}
		
		
		return toReturn.toString();
	}
	
	
	public static String getCSVHeaderRowForForecastIO(){
		
		StringBuilder toReturn =new StringBuilder("");
		
		for (int i=0 ; i<numOfHours; i++){
			
			if(i>0){
				toReturn.append(",");
			}
			
			toReturn.append("Hour_"+i);
			toReturn.append(","+weatherDataPointHeaderRow);
		}
		return toReturn.toString();

	}
	
	
	public static String getCSVRowForFIODataPoint(FIODataPoint datapoint){
		
		StringBuilder toReturn =new StringBuilder( "");
	
		toReturn.append(datapoint.temperature() +",");
		System.out.println("temperature is :" +datapoint.temperature());
	
		toReturn.append((int) (datapoint.cloudCover()*100) + "%" +",");
		System.out.println("cloud % is:" +datapoint.cloudCover()*100);

		toReturn.append(datapoint.precipType() +",");
		toReturn.append((int)(datapoint.precipProbability()*100) + "%"+",");
		System.out.println("Precipitation Type:" +datapoint.precipType());
		System.out.println("Chance of precipitation is:" +datapoint.precipProbability()*100 +"%");

		toReturn.append(datapoint.dewPoint() +",");
		System.out.println("DewPoint is:" +datapoint.dewPoint());
		
		toReturn.append(datapoint.windSpeed());
		System.out.println("Wind Speed is:" +datapoint.windSpeed());
		
		return toReturn.toString();
		
	}
	
	public static String getCSVHeaderRowForDataPoint(){
		return weatherDataPointHeaderRow;
	}
	

	
}
