package com.ubcsolar.weather;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.github.dvdme.ForecastIOLib.ForecastIO;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.LogType;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.Main.GlobalValues;
/*
 * Made this it's own class in case there are config settings we need to play with when we create the 
 * internet connection/use the ForcecastIO library. 
 * 
 * Also so that we can set it to run in it's own thread. 
 */
public class ForecastFactory {
	public ArrayList<ForecastIO> getForecasts(List<GeoCoord> spots) throws IOException{
		isInternetReachable(); //will throw an exception if it's not.
		ArrayList<ForecastIO> toReturn = new ArrayList<ForecastIO>(spots.size());
		List<GetForecastTask> forecasts = new ArrayList<GetForecastTask>();
		
		ExecutorService es = Executors.newCachedThreadPool();   

		// all tasks have finished or the time has been reached.
		for(GeoCoord g : spots){
			GetForecastTask tempTask = new GetForecastTask(g);
			forecasts.add(tempTask);
			 es.execute(tempTask);
		}
		es.shutdown(); //stops accepting new ones, but will execute given ones. 
		boolean finshed =true;
		try {
			finshed = es.awaitTermination(2, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			SolarLog.write(LogType.ERROR, System.currentTimeMillis(), "Error in Threadpool while getting forecasts");
			e.printStackTrace();
			return new ArrayList<ForecastIO>(); //return empty list, don't trust anything
		}
		
		if(finshed == false){
			SolarLog.write(LogType.ERROR, System.currentTimeMillis(), "threadpool timed out while getting forecasts");
			return new ArrayList<ForecastIO>();//don't trust anything, return empty list
		}
		//when it gets here every task will be done.
		
		
		
		for(GetForecastTask g : forecasts){
			while(g.getTheForecast() == null){
				 //shouldn't do this (executorService should block until this)
				SolarLog.write(LogType.ERROR, System.currentTimeMillis(), "Forecast not proplerly loaded in the ThreadPool(all of them are supposed to be done)");
			}
			toReturn.add(g.getTheForecast());
		}
		SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(), "Factory - Spots in: " + spots.size() + " forecasts: " + toReturn.size());
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
 		 		urlConnect.getContent();   
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
