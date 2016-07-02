package com.ubcsolar.weather;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.eclipsesource.json.JsonObject;
import com.github.dvdme.ForecastIOLib.ForecastIO;
import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.Main.GlobalValues;
import com.ubcsolar.common.ForecastReport;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.LogType;
import com.ubcsolar.common.ModuleController;
import com.ubcsolar.common.Route;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.exception.InconsistentForecastMapStateException;
import com.ubcsolar.exception.NoForecastReportException;
import com.ubcsolar.exception.NoLoadedRouteException;
import com.ubcsolar.map.MapController;
import com.ubcsolar.notification.ExceptionNotification;
import com.ubcsolar.notification.NewForecastReport;
import com.ubcsolar.notification.NewMapLoadedNotification;
import com.ubcsolar.notification.Notification;

public class WeatherController extends ModuleController {
	private ForecastReport lastDownloadedReport = null;
	private ForecastReport lastCustomReport = null;
	private List<ForecastIO> retrievedForecasts;
	private List<ForecastIO> customForecasts = new ArrayList<ForecastIO>();
	private List<ForecastIO> comboForecasts = new ArrayList<ForecastIO>();
	private MapController myMapController;
	
	public WeatherController(GlobalController toAdd) {
		super(toAdd);
		myMapController = toAdd.getMapController();
	}
	
	/**
	 * 
	 * @param numOfKMBetweenForecasts - we only have 1000 calls, so we probably can't get a forecast for every point
	 * in the Route. 
	 * @throws IOException 
	 */
	public void downloadNewForecastsForRoute(int numOfKMBetweenForecasts) throws IOException{
		Route currentlyLoadedRoute = this.mySession.getMapController().getAllPoints();
		if(currentlyLoadedRoute == null){
			mySession.sendNotification(new ExceptionNotification(new NullPointerException(), "Tried to get forecast but route was null"));
			return;
		}
		List<GeoCoord> toGet = this.calculatePointsForForecast(numOfKMBetweenForecasts, currentlyLoadedRoute.getTrailMarkers());
		
		ForecastFactory forecastGetter = new ForecastFactory();
		try{
			retrievedForecasts = forecastGetter.getForecasts(toGet);
			ForecastReport theReport = new ForecastReport(retrievedForecasts, this.mySession.getMapController().getLoadedMapName());
			lastDownloadedReport = theReport;
			this.mySession.sendNotification(new NewForecastReport(theReport));
		}
		catch(IOException e){
			SolarLog.write(LogType.ERROR, System.currentTimeMillis(), "Tried to load Forecasts, but IOException thrown (bad internet likely)");
			throw e;
		}

	}
	
	public void downloadCurrentLocationForecast(GeoCoord location) throws NoLoadedRouteException{
		Route currentRoute = mySession.getMapController().getAllPoints();
		List<GeoCoord> toGet = new ArrayList<GeoCoord>();
		toGet.add(location);
		ForecastFactory forecastGetter = new ForecastFactory();
		ForecastIO currentForecast;
		try {
			currentForecast = forecastGetter.getForecasts(toGet).get(0);
			ForecastIO forecastOnRoute = copyAtLocation(currentForecast,
					currentRoute.getClosestPointOnRoute(location));
			loadCustomForecast(forecastOnRoute);
		} catch (IOException e) {
			SolarLog.write(LogType.ERROR, System.currentTimeMillis(), e.getMessage());
		}
	}
	
	/**
	 * Adds a new custom forecast to the list of custom forecasts. This can be done multiple
	 * times by calling the method repeatedly. Will not overwrite legitimate downloaded reports
	 * stored in retrievedForecasts, but will overwrite what is displayed in the WeatherAdvancedWindow.
	 * Clearing will revert to the downloaded forecast
	 * 
	 * @param customForecast: the custom forecast report to be added to the list of custom forecasts.
	 * Usually produced through the FakeForecastWindow.
	 * @throws NoLoadedRouteException 
	 */
	
	public void loadCustomForecast(ForecastIO customForecast) throws NoLoadedRouteException{
		customForecasts.add(customForecast);
		addCustomForecasts();
		ForecastReport theReport = new ForecastReport(comboForecasts, this.mySession.getMapController().getLoadedMapName());
		lastCustomReport = theReport;
		this.mySession.sendNotification(new NewForecastReport(theReport));
	}
	
	/**
	 * Clears all custom forecasts and resends the most recent downloaded report, provided it exists
	 */
	
	public void clearCustomForecasts(){
		customForecasts = new ArrayList<ForecastIO>();
		if(retrievedForecasts == null){
			comboForecasts = new ArrayList<ForecastIO>();
		}else{
			comboForecasts = new ArrayList<ForecastIO>(retrievedForecasts);
		}
		if(lastDownloadedReport != null){
			this.mySession.sendNotification(new NewForecastReport(lastDownloadedReport));
		}else{ //TODO it's a duct tape solution for dissapearing the green dot on map when the 48H forecast was not loaded
			List<ForecastIO> forecast = new ArrayList<ForecastIO>();
			ForecastReport theReport = new ForecastReport(forecast, this.mySession.getMapController().getLoadedMapName());
			this.mySession.sendNotification(new NewForecastReport(theReport));
			this.mySession.getGUIMain().clearWeather();
		}
	}
	
	/*
	 * returns a report with forecasts for every point in the route's breadcrumbs list, interprolating 
	 * between loaded reports. Does not refresh downloaded weather reports. 
	 */
	public ForecastReport getSimmedForecastForEveryPointfForLoadedRoute() throws NoForecastReportException, NoLoadedRouteException{
		if(lastDownloadedReport == null){
			throw new NoForecastReportException();
		}
		Route currentlyLoadedRoute = this.mySession.getMapController().getAllPoints();
		if(currentlyLoadedRoute == null){
			throw new NoLoadedRouteException();
		}
		
		//assumes that the first forecast is the first point. 
		
		List<ForecastIO> currentForecasts = lastDownloadedReport.getForecasts();
		List<ForecastIO> theForecastList = new ArrayList<ForecastIO>(currentlyLoadedRoute.getTrailMarkers().size());
		
		for(GeoCoord g : currentlyLoadedRoute.getTrailMarkers()){
			int indexOfStart = this.getIndexOfStartForecast(lastDownloadedReport.getForecasts(), g);
			if(indexOfStart == lastDownloadedReport.getForecasts().size()-1){
				theForecastList.add(this.interpolateForecast(currentForecasts.get(indexOfStart), null, g));
			}
			else{
			theForecastList.add(this.interpolateForecast(currentForecasts.get(indexOfStart), currentForecasts.get(indexOfStart+1),g));
			}
		}
		
		ForecastReport toReturn = new ForecastReport(theForecastList, "SIMULATED " + this.mySession.getMapController().getLoadedMapName());
		return toReturn;
		
		
	}
	
	
	/**
	 * interpolates a forecast based on the two closest forecasts. 
	 * @param target
	 * @return
	 * @throws NoForecastReportException
	 */
	private ForecastIO interprolateForecast(GeoCoord target) throws NoForecastReportException{
		if(comboForecasts == null){
			throw new NoForecastReportException();
		}
		int startIndex = this.getIndexOfStartForecast(comboForecasts, target);
		ForecastIO startForecast = this.comboForecasts.get(startIndex);
		
		//check to see if the target point is off the end of the forecast list. 
		if((startIndex >= comboForecasts.size()-1) || (startIndex<= 0)){
			GeoCoord firstSpot = new GeoCoord(startForecast.getLatitude(),startForecast.getLongitude(),0.0);
			ForecastIO secondForecast;
			if(startIndex>= comboForecasts.size()-1){
				secondForecast = this.comboForecasts.get(startIndex - 1);
			}else{ //i.e is 0
				secondForecast = this.comboForecasts.get(startIndex + 1);
			}
			GeoCoord scndSpot = new GeoCoord(secondForecast.getLatitude(),secondForecast.getLongitude(),0.0);
			double distanceBetweenFCs = firstSpot.calculateDistance(scndSpot);
			double distanceBetweenTargetAndSecond = scndSpot.calculateDistance(target);
			
			
			//can't interpolate past the end of the forecasts.
			if(distanceBetweenTargetAndSecond > distanceBetweenFCs){
				return this.comboForecasts.get(startIndex);  
			}
			else{
				return this.interpolateForecast(startForecast, secondForecast, target);
			}			
		}
		//determine which point is next (one left or one right?)
		ForecastIO oneLeft = this.comboForecasts.get(startIndex - 1);
		GeoCoord oneLeftSpot = new GeoCoord(oneLeft.getLatitude(), oneLeft.getLongitude(), 0.0);
		ForecastIO oneRight = this.comboForecasts.get(startIndex + 1);
		GeoCoord oneRightSpot = new GeoCoord(oneRight.getLatitude(), oneRight.getLongitude(),0.0); 
		if(target.calculateDistance(oneLeftSpot) < target.calculateDistance(oneRightSpot)){
			return this.interpolateForecast(startForecast, oneLeft, target);
		}
		else{
			return this.interpolateForecast(startForecast, oneRight, target);
		}
				
	}
	/*
	 * Supposed to interpolate between the start forecast and next forecast. Currently just returns the closest. 
	 */
	private ForecastIO interpolateForecast(ForecastIO startForecast, ForecastIO endForecast, GeoCoord currentLoc) {
		if(endForecast == null){
			return startForecast;
		}
		
		double startDistance = GeoCoord.haversine(startForecast.getLatitude(), startForecast.getLongitude(), currentLoc.getLat(), currentLoc.getLon());
		double endDistance = GeoCoord.haversine(endForecast.getLatitude(), endForecast.getLongitude(), currentLoc.getLat(), currentLoc.getLon());
		if(startDistance<endDistance){
			return startForecast;
		}
		else{
			return endForecast;
		}
	}

	
	/**
	 * Returns the forecast tha is closest to the given point 
	 * @param toSearch
	 * @param g
	 * @return
	 */
	private int getIndexOfStartForecast(List<ForecastIO> toSearch, GeoCoord g){
		int lowestIndex = -1;
		double minDistance = 999999999999999.0;
		for(int i = 0; i<toSearch.size(); i++){
			ForecastIO fc = toSearch.get(i);
			double distance = GeoCoord.haversine(fc.getLatitude(), fc.getLongitude(), g.getLat(), g.getLon());
			if(distance<minDistance){
				lowestIndex = i;
				minDistance = distance;
			}
		}
		
		//If we're between forecasts, the closest may be the start, but it also my be closer to end. 
		//This checks it.
		
		if(lowestIndex>=2 && lowestIndex<= toSearch.size()-2){
			double distanceToPrevious = GeoCoord.haversine(toSearch.get(lowestIndex-1).getLatitude(),
					toSearch.get(lowestIndex-1).getLongitude(), g.getLat(), g.getLon());
			double distanceToNext = GeoCoord.haversine(toSearch.get(lowestIndex+1).getLatitude(),
					toSearch.get(lowestIndex+1).getLongitude(), g.getLat(), g.getLon());
			if(distanceToPrevious<distanceToNext){ //G is in the second half of the interval
													//so closest to second post, which means start is last one. 
				return lowestIndex-1;
			}
		}
		
		
		
		
		return lowestIndex;
	}
	
	
	/*
	 * Starting at the first point, creates a list of points that are at least numOfKMBetween km apart
	 * as the crow flies (i.e if the route does a spiral of diamater 1km, and you asked for 2km, you will only get
	 * the start point. No reason to get two forecasts if the two points are less than that apart)/
	 */
	private List<GeoCoord> calculatePointsForForecast(int numOfKMBetween, List<GeoCoord> trailMarkers) {
		GeoCoord start = trailMarkers.get(0);
		ArrayList<GeoCoord> toReturn = new ArrayList<GeoCoord>(trailMarkers.size()/numOfKMBetween);
		toReturn.add(start);
		for(GeoCoord g : trailMarkers){
			if(start.calculateDistance(g) > numOfKMBetween){
				toReturn.add(g);
				start = g;
			}
		}
		System.out.println("Controller - Selected " + toReturn.size() + " points from " + trailMarkers.size());
		return toReturn;
	}

	/**
	 * will receive all notifications it has registered for here.
	 * The 'shoulder tap'
	 */
	@Override
	public void notify(Notification n) {
		if(n.getClass() == NewMapLoadedNotification.class){ //example notification handler
			this.lastDownloadedReport = null; //remove it. 
			SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(), "Deleted old forecasts because new route loaded");
			this.mySession.sendNotification(new NewForecastReport(new ForecastReport(new ArrayList<ForecastIO>(), null)));	
			
		}
	}

	/**
	 * registers to receive notifications
	 */
	@Override
	public void register() {

		this.mySession.register(this, NewMapLoadedNotification.class); //example line.
		
	}
	
	/**
	 * interpolates any currently loaded custom forecasts with previously downloaded forecasts.
	 * If any of the custom forecasts are given the same location as previously downloaded
	 * forecasts, they will overwrite the downloaded forecasts
	 * 
	 * @return the list of ForecastIOs that should be put in the ForecastReport
	 */
	private void addCustomForecasts() throws NoLoadedRouteException{
		if(retrievedForecasts == null){
			comboForecasts = new ArrayList<ForecastIO>();
		}else{
			comboForecasts = new ArrayList<ForecastIO>(retrievedForecasts);
		}
		for(int i = 0; i < customForecasts.size(); i++){
			if(comboForecasts.size() > 1){
				for(int j = 0; j < comboForecasts.size()-1; j++){
					GeoCoord currCustom = new GeoCoord(customForecasts.get(i).getLatitude(), 
							customForecasts.get(i).getLongitude(), 0.0);
					GeoCoord currCombo = new GeoCoord(comboForecasts.get(j).getLatitude(), 
							comboForecasts.get(j).getLongitude(), 0.0);
					GeoCoord nextCombo = new GeoCoord(comboForecasts.get(j+1).getLatitude(), 
							comboForecasts.get(j+1).getLongitude(), 0.0);
					if(myMapController.findDistanceAlongLoadedRoute(currCustom) == 
							myMapController.findDistanceAlongLoadedRoute(currCombo)){
						comboForecasts.remove(j);
						comboForecasts.add(j, customForecasts.get(i));
						break;
					}
					if(myMapController.findDistanceAlongLoadedRoute(currCustom) > 
							myMapController.findDistanceAlongLoadedRoute(currCombo) &&
							myMapController.findDistanceAlongLoadedRoute(currCustom) < 
							myMapController.findDistanceAlongLoadedRoute(nextCombo)){
						comboForecasts.add(j+1, customForecasts.get(i));
						break;
					}
					if(j == comboForecasts.size() - 2){
						comboForecasts.add(customForecasts.get(i));
					}
				}
			}else if(comboForecasts.size() == 1){
				GeoCoord currCustom = new GeoCoord(customForecasts.get(i).getLatitude(), 
						customForecasts.get(i).getLongitude(), 0.0);
				GeoCoord currCombo = new GeoCoord(comboForecasts.get(0).getLatitude(), 
						comboForecasts.get(0).getLongitude(), 0.0);
				if(myMapController.findDistanceAlongLoadedRoute(currCustom) == 
						myMapController.findDistanceAlongLoadedRoute(currCombo)){
					comboForecasts.remove(0);
					comboForecasts.add(0, customForecasts.get(i));
				}
				else if(myMapController.findDistanceAlongLoadedRoute(currCustom) > 
						myMapController.findDistanceAlongLoadedRoute(currCombo)){ 
					comboForecasts.add(customForecasts.get(i));
					
				}else{
					comboForecasts.add(0, customForecasts.get(i));
				}	
			}else{
				comboForecasts.add(customForecasts.get(i));
			}
		}
		//creates a duplicate of earliest point at 0 distance if there is no data
		//for distance 0, because otherwise the WeatherAdvancedWindow breaks
		GeoCoord firstPoint = new GeoCoord(comboForecasts.get(0).getLatitude(), 
				comboForecasts.get(0).getLongitude(), 0.0);
		if(myMapController.findDistanceAlongLoadedRoute(firstPoint) > 0){
			ForecastIO forecast = copyAtLocation(comboForecasts.get(0),
					myMapController.getAllPoints().getTrailMarkers().get(0));
			comboForecasts.add(0,forecast);
		}
	}
	
	private ForecastIO copyAtLocation(ForecastIO initial, GeoCoord location){
		
		//GeoCoord location = myMapController.getAllPoints().getTrailMarkers().get(0);
		double latitude = location.getLat();
		double longitude = location.getLon();
		JsonObject forecastInfo = new JsonObject();
		forecastInfo.add("latitude", latitude);
		forecastInfo.add("longitude", longitude);
		forecastInfo.add("currently", initial.getCurrently());
		forecastInfo.add("hourly", initial.getHourly());
		forecastInfo.add("daily", initial.getDaily());
		forecastInfo.add("flags", initial.getFlags());
		ForecastIO forecast = new ForecastIO(GlobalValues.WEATHER_KEY);
		forecast.getForecast(forecastInfo);
		
		return forecast;
	}
	
	
	
	/**
	 * Returns the ForecastIO for the requested location. If doInterprolation is true,
	 * interprolates based on the two closes forecasts. If false, will attempt
	 * to download a brand new forecastIO. 
	 * @param target
	 * @param doInterprolation
	 * @return the forecastIO for the requested location. 
	 * 
	 * @throws IOException if no internet available
	 * @throws NoForecastReportException if no forecasts have been downloaded/added
	 */
	public ForecastIO getForecastForSpecificPoint(GeoCoord target, Boolean doInterprolation) throws IOException, NoForecastReportException{
		if(!doInterprolation){
			return new ForecastIO("" + target.getLat(), "" + target.getLon(), GlobalValues.WEATHER_KEY);
		}
		
		if(this.lastCustomReport == null || this.lastCustomReport.getForecasts().size() == 0){
			throw new NoForecastReportException();
		}
		
		
		return this.interprolateForecast(target);
		
		
	}
	
	public void loadForecastFromFile(File fileToLoadFrom) throws IOException, FileNotFoundException, InconsistentForecastMapStateException{
		ForecastReport temp = this.mySession.getMyDataBaseController().getCachedForecastReport(fileToLoadFrom);
		String forecastRouteName = temp.getRouteNameForecastsWereCreatedFor();
		String currentlyLoadedRouteName = this.mySession.getMapController().getLoadedMapName();
		if(!forecastRouteName.equalsIgnoreCase(currentlyLoadedRouteName)){
			throw new InconsistentForecastMapStateException(forecastRouteName, currentlyLoadedRouteName);
		}
		if(temp != null){
			this.lastDownloadedReport = temp;
			this.retrievedForecasts = temp.getForecasts();
			this.customForecasts = new ArrayList<ForecastIO>();
		}
		this.mySession.sendNotification(new NewForecastReport(temp));
	}

}
