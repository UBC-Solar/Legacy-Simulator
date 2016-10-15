package com.ubcsolar.weather;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.eclipsesource.json.ParseException;
import com.github.dvdme.ForecastIOLib.FIODataBlock;
import com.github.dvdme.ForecastIOLib.FIODataPoint;
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
	 * @throws NoLoadedRouteException 
	 */
	public void downloadNewForecastsForRoute(int numOfKMBetweenForecasts) throws IOException, NoLoadedRouteException{
		Route currentlyLoadedRoute = this.mySession.getMapController().getAllPoints();
		if(currentlyLoadedRoute == null){
			mySession.sendNotification(new ExceptionNotification(new NullPointerException(), "Tried to get forecast but route was null"));
			return;
		}
		List<GeoCoord> toGet = this.calculatePointsForForecast(numOfKMBetweenForecasts, currentlyLoadedRoute.getTrailMarkers());
		
		ForecastFactory forecastGetter = new ForecastFactory();
		try{
			retrievedForecasts = forecastGetter.getForecasts(toGet);
			if(comboForecasts.size() == 0){
				comboForecasts = new ArrayList<ForecastIO>(retrievedForecasts);
			}else{
				
				for(int i = 0; i < customForecasts.size(); i++){
					ForecastIO temp = fillEmptyHours(customForecasts.get(i));
					customForecasts.set(i, temp);
				}
				addCustomForecasts();
			}
			ForecastReport theReport = new ForecastReport(comboForecasts, this.mySession.getMapController().getLoadedMapName());
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
		customForecasts.add(fillEmptyHours(customForecast));
		addCustomForecasts();
		ForecastReport theReport = new ForecastReport(comboForecasts, this.mySession.getMapController().getLoadedMapName());
		lastCustomReport = theReport;
		this.mySession.sendNotification(new NewForecastReport(theReport));
	}
	
	
	/** 
	 * Creates a custom forecast from a list of Json datapoints and a location. If there 
	 * 		are less datapoints than there are in the existing forecasts, this will interpolate 
	 * 		the custom datapoints with datapoints from existing forecasts, so that the custom forecast
	 * 		has the same number of hours worth of data as the existing forecasts.
	 * 		This is necessary to prevent bugs when drawing the downloaded and custom forecasts
	 * 		at the same time. This function DOES NOT retroactively expand old custom forecasts
	 * 		if a new forecast with greater size is added.
	 * @param datapoints: the list of custom forecast datapoints to put in the custom forecast
	 * @param location: a GeoCoord giving the location of the custom forecast
	 * @throws NoLoadedRouteException
	 */
	public void loadCustomForecast(List<JsonObject> datapoints, GeoCoord location) throws NoLoadedRouteException{
		if(!(comboForecasts.size() == 0)){
			ForecastIO nearest = comboForecasts.get(0);
			GeoCoord currLoc = new GeoCoord(nearest.getLatitude(), nearest.getLongitude(), 0);
			double minDistance = Math.abs(location.calculateDistance(currLoc));
			for(int i = 1; i < comboForecasts.size(); i++){
				ForecastIO currForecast = comboForecasts.get(i);
				currLoc = new GeoCoord(currForecast.getLatitude(), 
						currForecast.getLongitude(), 0);
				double currDistance = Math.abs(currLoc.calculateDistance(location));
				if(currDistance < minDistance){
					minDistance = currDistance;
					nearest = currForecast;
				}else
					break;
			}
			List<Long> customTimes = new ArrayList<Long>();
			for(int i = 0; i < datapoints.size(); i++){
				customTimes.add(Long.parseLong(datapoints.get(i).get("time").toString()));
			}
			if(nearest != null){//if this if statement is skipped, something's wrong
				System.out.println("latitude: " + location.getLat() + " longitude: " + location.getLon());
				System.out.println("we got here");
				JsonObject prevHourly = nearest.getHourly();
				JsonArray hourlyData = (JsonArray)prevHourly.get("data");
				for(int i = 0; i < hourlyData.size(); i++){
					JsonObject currHour = (JsonObject) hourlyData.get(i);
					String thisTimeStr = currHour.get("time").toString();
					long time = Long.parseLong(thisTimeStr); 
					if(!customTimes.contains(time)){
						datapoints.add(currHour);
						customTimes.add(time);
						
					}
				}
			}
			
		}
		ForecastIOFactory.addDatapoints(datapoints);
		ForecastIOFactory.changeLocation(location);
		ForecastIO customForecast = ForecastIOFactory.build();
		try{
			loadCustomForecast(customForecast);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public ForecastIO fillEmptyHours(ForecastIO oldForecast){
		
		if(!(comboForecasts.size() == 0)){
			JsonObject oldHourly = oldForecast.getHourly();
			JsonArray oldHourlyData = (JsonArray)oldHourly.get("data");
			GeoCoord location = new GeoCoord(oldForecast.getLatitude(), oldForecast.getLongitude(), 0);
			ForecastIO nearest = comboForecasts.get(0);
			GeoCoord currLoc = new GeoCoord(nearest.getLatitude(), nearest.getLongitude(), 0);
			double minDistance = Math.abs(location.calculateDistance(currLoc));
			for(int i = 1; i < comboForecasts.size(); i++){
				ForecastIO currForecast = comboForecasts.get(i);
				currLoc = new GeoCoord(currForecast.getLatitude(), 
						currForecast.getLongitude(), 0);
				double currDistance = Math.abs(currLoc.calculateDistance(location));
				if(currDistance < minDistance){
					minDistance = currDistance;
					nearest = currForecast;
				}else
					break;
			}
			List<JsonObject> datapoints = new ArrayList<JsonObject>();
			for(int i = 0; i < oldHourlyData.size(); i++){
				datapoints.add((JsonObject)oldHourlyData.get(i));
			}
			List<Long> customTimes = new ArrayList<Long>();
			for(int i = 0; i < oldHourlyData.size(); i++){
				customTimes.add(Long.parseLong(((JsonObject)oldHourlyData.get(i)).get("time").toString()));
			}
			if(nearest != null){//if this if statement is skipped, something's wrong
				System.out.println("latitude: " + location.getLat() + " longitude: " + location.getLon());
				System.out.println("we got here");
				JsonObject prevHourly = nearest.getHourly();
				JsonArray hourlyData = (JsonArray)prevHourly.get("data");
				for(int i = 0; i < hourlyData.size(); i++){
					JsonObject currHour = (JsonObject) hourlyData.get(i);
					String thisTimeStr = currHour.get("time").toString();
					long time = Long.parseLong(thisTimeStr); 
					if(!customTimes.contains(time)){
						datapoints.add(currHour);
						customTimes.add(time);
					}
				}
			}
			ForecastIOFactory.addDatapoints(datapoints);
			ForecastIOFactory.changeLocation(location);
			ForecastIO customForecast = ForecastIOFactory.build();
			return customForecast;
		}else
			return oldForecast;
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
		}else{ //TODO it's a duct tape solution for disappearing the green dot on map when the 48H forecast was not loaded
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
		//if there is only 1 forecast, you can't do any interpolation, so it just returns that one
		if(comboForecasts.size() == 1){
			return comboForecasts.get(0);
		}
		int startIndex = this.getIndexOfStartForecast(comboForecasts, target);
		ForecastIO startForecast = this.comboForecasts.get(startIndex);
		
		//check to see if the target point is off the end of the forecast list. 
		if((startIndex >= comboForecasts.size()-1) || (startIndex <= 0)){
			GeoCoord firstSpot = new GeoCoord(startForecast.getLatitude(),startForecast.getLongitude(),0.0);
			ForecastIO secondForecast;
			if(startIndex >= comboForecasts.size()-1){
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
	 * Interpolates between the start forecast and the end forecast
	 */
	private ForecastIO interpolateForecast(ForecastIO startForecast, 
			ForecastIO endForecast, GeoCoord currentLoc) {
		if(endForecast == null){
			endForecast = startForecast;
		}
		if(startForecast == null){
			startForecast = endForecast;
		}
		
		double startDistance = GeoCoord.haversine(startForecast.getLatitude(), 
				startForecast.getLongitude(), currentLoc.getLat(), currentLoc.getLon());
		double endDistance = GeoCoord.haversine(endForecast.getLatitude(), 
				endForecast.getLongitude(), currentLoc.getLat(), currentLoc.getLon());
		if(startDistance<endDistance){
			double startWeight = 1 / (endDistance + startDistance) * endDistance;
			ForecastIO interpolated = createInterpolatedForecast(startForecast, endForecast, 
					startWeight, currentLoc);
			return interpolated;
		}
		else{
			double endWeight;
			if(Math.abs(endDistance)<0.0001 && Math.abs(startDistance)<0.0001){
				endWeight = 0;
			}else{
				endWeight = 1 / (endDistance + startDistance) * startDistance;
			}
			ForecastIO interpolated = createInterpolatedForecast(endForecast, startForecast, 
					endWeight, currentLoc);
			return interpolated;
		}
	}
	
	private ForecastIO createInterpolatedForecast(ForecastIO closer, ForecastIO farther, 
			double closeWeight, GeoCoord currentLoc){
		double farWeight = 1 - closeWeight;
		JsonValue closeHourlyValue = closer.getHourly().get("data");
		JsonArray closeHourly = new JsonArray();
		try{
			closeHourly = (JsonArray) closeHourlyValue;
		}catch(java.lang.ClassCastException e){
			System.out.println("Your jsons aren't formatted right");
			e.printStackTrace();
		}
		
		JsonValue farHourlyValue = farther.getHourly().get("data");
		JsonArray farHourly = new JsonArray();
		try{
			farHourly = (JsonArray) farHourlyValue;
		}catch(java.lang.ClassCastException e){
			System.out.println("Your jsons aren't formatted right");
			e.printStackTrace();
		}
		
		int numHours;
		if(closeHourly.size() < farHourly.size()){
			numHours = closeHourly.size();
		}else{
			numHours = farHourly.size();
		}
		if(numHours == 0){
			System.out.println("There's no data here!! (in interpolate method)");
		}
		FIODataPointFactory factory = new FIODataPointFactory();
		List<JsonObject> datapoints = new ArrayList<JsonObject>();
		boolean errorOccurred = false;
		for(int i = 0; i < numHours; i++){
			JsonObject closeHourCurr = (JsonObject) closeHourly.get(i);
			JsonObject farHourCurr = (JsonObject) farHourly.get(i);
			
			int time;
			try{
				time = Integer.parseInt(farHourCurr.get("time").toString());
			}catch(java.lang.NullPointerException e){
				System.out.println("this forecast was missing a timestamp, all your times are probably wrong now");
				time = 0;
			}
			
			double temp;
			try{
				temp = parseJsonDouble(farHourCurr.get("temperature"))*farWeight +
						parseJsonDouble(closeHourCurr.get("temperature"))*closeWeight;
				}
			catch(java.lang.NullPointerException e){
				errorOccurred = true;
				temp = 0;
			}
			double cldCover;
			try{
				cldCover = parseJsonDouble(farHourCurr.get("cloudCover"))*farWeight +
						parseJsonDouble(closeHourCurr.get("cloudCover"))*closeWeight;
			}
			catch(java.lang.NullPointerException e){
				errorOccurred = true;
				cldCover = 0;
			}
			double dewPoint;
			try{
				dewPoint = parseJsonDouble(farHourCurr.get("dewPoint"))*farWeight +
						parseJsonDouble(closeHourCurr.get("dewPoint"))*closeWeight;
				}
			catch(java.lang.NullPointerException e){
				errorOccurred = true;
				dewPoint = 0;
			}
			double humidity;
			try{
				humidity = parseJsonDouble(farHourCurr.get("humidity"))*farWeight +
						parseJsonDouble(closeHourCurr.get("humidity"))*closeWeight;
				}
			catch(java.lang.NullPointerException e){
				errorOccurred = true;
				humidity = 0;
			}
			double strmBearing;
			try{
				strmBearing = parseJsonDouble(farHourCurr.get("nearestStormBearing"))*farWeight +
						parseJsonDouble(closeHourCurr.get("nearestStormBearing"))*closeWeight;
				}
			catch(java.lang.NullPointerException e){
				errorOccurred = true;
				strmBearing = 0;
			}
			double strmDistance;
			try{
				strmDistance = parseJsonDouble(farHourCurr.get("nearestStormDistance"))*farWeight +
						parseJsonDouble(closeHourCurr.get("nearestStormDistance"))*closeWeight;
				}
			catch(java.lang.NullPointerException e){
				errorOccurred = true;
				strmDistance = 0;
			}
			double windBearing;
			try{
				windBearing = parseJsonDouble(farHourCurr.get("windBearing"))*farWeight +
						parseJsonDouble(closeHourCurr.get("windBearing"))*closeWeight;
				}
			catch(java.lang.NullPointerException e){
				errorOccurred = true;
				windBearing = 0;
			}
			double windSpeed;
			try{
				windSpeed = parseJsonDouble(farHourCurr.get("windSpeed"))*farWeight +
						parseJsonDouble(closeHourCurr.get("windSpeed"))*closeWeight;
			}
			catch(java.lang.NullPointerException e){
				errorOccurred = true;
				windSpeed = 0;
			}
			double precipProb;
			try{
				precipProb = parseJsonDouble(farHourCurr.get("precipProbability"))*farWeight +
						parseJsonDouble(closeHourCurr.get("precipProbability"))*closeWeight;
				}
			catch(java.lang.NullPointerException e){
				errorOccurred = true;
				precipProb = 0;
			}
			double precipIntensity;
			try{
				precipIntensity = parseJsonDouble(farHourCurr.get("precipIntensity"))*farWeight +
						parseJsonDouble(closeHourCurr.get("precipIntensity"))*closeWeight;
			}catch(java.lang.NullPointerException e){
				errorOccurred = true;
				precipIntensity = 0;
			}
			String precipType;
			try{
				precipType = closeHourCurr.get("precipType").toString();
			}catch(java.lang.NullPointerException e){
				errorOccurred = true;
				precipType = "";
			}
			
			factory.time(time).cloudCover(cldCover).dewPoint(dewPoint).humidity(humidity).
				precipProb(precipProb).precipType(precipType).temperature(temp).windBearing(windBearing).
				windSpeed(windSpeed).stormBearing(strmBearing).stormDistance(strmDistance).precipIntensity(precipIntensity);
		
			datapoints.add(factory.build());
		}
		/*if(errorOccurred){
			System.out.println("One of the forecasts was missing a field (probably storm related)");
		}*/
		ForecastIOFactory.addDatapoints(datapoints);
		ForecastIOFactory.changeLocation(currentLoc);
		return ForecastIOFactory.build();
	}
	
	/**
	 * convenience method to parse doubles from Json fields
	 * @param field
	 * @return
	 */
	private double parseJsonDouble(JsonValue field){
		return Double.parseDouble(field.toString());
	}
	
	/**
	 * Returns the forecast that is closest to the given point 
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
	 * POST: comboForecasts will contain the ForecastIOs that should be put in the ForecastReport
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
		
		double latitude = location.getLat();
		double longitude = location.getLon();
		JsonObject forecastInfo = new JsonObject();
		forecastInfo.add("latitude", latitude);
		forecastInfo.add("longitude", longitude);
		forecastInfo.add("currently", initial.getCurrently());
		forecastInfo.add("hourly", initial.getHourly());
		forecastInfo.add("flags", initial.getFlags());
		ForecastIO forecast = new ForecastIO(GlobalValues.WEATHER_KEY);
		forecast.getForecast(forecastInfo);
		
		return forecast;
	}
	
	
	
	/**
	 * Returns the ForecastIO for the requested location. If doInterprolation is true,
	 * interpolates based on the two closes forecasts. If false, will attempt
	 * to download a brand new forecastIO (and if that fails, will just give an interpolated one)
	 * @param target
	 * @param doInterpolation
	 * @return the forecastIO for the requested location. 
	 * 
	 * @throws IOException if no internet available
	 * @throws NoForecastReportException if no forecasts have been downloaded/added
	 */
	public ForecastIO getForecastForSpecificPoint(GeoCoord target, Boolean doInterprolation) throws IOException, NoForecastReportException{
		if(!doInterprolation && isInternetReachable()){
			return new ForecastIO("" + target.getLat(), "" + target.getLon(), GlobalValues.WEATHER_KEY);
		}
		
		if(comboForecasts.size() == 0){
			throw new NoForecastReportException();
		}
		
		
		return this.interprolateForecast(target);
		
		
	}
	
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
    catch(IOException e){return false;}
    finally{
    	urlConnect.disconnect();
    }
 return true;
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
