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
		List<GetForecastTask> forecasts = new ArrayList<GetForecastTask>();
		for(GeoCoord g : spots){
			GetForecastTask tempTask = new GetForecastTask(g);
			forecasts.add(tempTask);
			Thread temp = new Thread(tempTask);
			temp.start();
		}
		
		for(GetForecastTask g : forecasts){
			while(g.getTheForecast() == null){
				System.out.println("WAAAITTTINGFORFORECAST");
			}
			toReturn.add(g.getTheForecast());
		}
		System.out.println("Factory - Spots in: " + spots.size() + " forecasts: " + toReturn.size());
		return toReturn;
	}

	
	/**
	 * Method to use to check for network connection. 
	 * For documentation/reasoning, see http://stackoverflow.com/questions/1139547/detect-internet-connection-using-java
	 * 
	 * @return
	 * @throws IOException
	 */
	 private boolean isInternetReachable() throws IOException{
           

            	//make a URL to a known source
             URL url = new URL(GlobalValues.URL_TO_CHECK_INTERNET_WITH);

             //open a connection to that source
             
             HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();
             urlConnect.setConnectTimeout(GlobalValues.MAX_TIME_MS_WAIT_FOR_URL);
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
	
	 private class GetForecastTask implements Runnable{
		 private ForecastIO myForecast = null;
		 private GeoCoord target;
		 
		 public GetForecastTask(GeoCoord target){
			 super();
			 this.target = target;
		 }
		 
		 @Override
		public void run() {
			myForecast = new ForecastIO("" + target.getLat(), "" + target.getLon(), GlobalValues.WEATHER_KEY);
		}
		 
		public ForecastIO getTheForecast(){
			return myForecast;
		}
		 
	 }
}
