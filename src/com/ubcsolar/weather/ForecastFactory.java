package com.ubcsolar.weather;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.github.dvdme.ForecastIOLib.ForecastIO;
import com.ubcsolar.common.GeoCoord;

import com.ubcsolar.Main.GlobalValues;
/*
 * Made this it's own class in case there are config settings we need to play with when we create the 
 * internet connection/use the ForcecastIO library. 
 * 
 * Also so that we can set it to run in it's own thread. 
 */
public class ForecastFactory {
	private final String API_KEY = GlobalValues.WEATHER_KEY;

	public ArrayList<ForecastIO> getForecasts(List<GeoCoord> spots) throws IOException{
		isInternetReachable(); //will throw an exception if it's not.
		ArrayList<ForecastIO> toReturn = new ArrayList<ForecastIO>(spots.size());
		for(GeoCoord g : spots){
			ForecastIO forecastIOCurr = new ForecastIO("" + g.getLat(), "" + g.getLon(), ForecastIO.UNITS_SI, ForecastIO.LANG_ENGLISH, API_KEY);
			if(forecastIOCurr.getLatitude() == null){
				throw new IOException("Latitude was null, implies bad network conection");
			}
			toReturn.add(forecastIOCurr);
		}
		System.out.println("Factory - Spots in: " + spots.size() + " forecasts: " + toReturn.size());
		return toReturn;
	}

	 private boolean isInternetReachable() throws IOException{
           

            	//make a URL to a known source
             URL url = new URL(GlobalValues.URL_TO_CHECK_INTERNET_WITH);

             //open a connection to that source
             HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();
 		 	try{
             //trying to retrieve data from the source. If there
             //is no connection, this line will fail
             Object objData = urlConnect.getContent();   
            }
            catch(IOException e){throw e;}
            finally{
            	urlConnect.disconnect();
            }
         return true;
     }
	
}
