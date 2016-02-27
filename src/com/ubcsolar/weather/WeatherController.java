package com.ubcsolar.weather;

import java.util.ArrayList;
import java.util.List;

import com.github.dvdme.ForecastIOLib.ForecastIO;
import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.common.DistanceUnit;
import com.ubcsolar.common.ForecastReport;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.ModuleController;
import com.ubcsolar.common.Route;
import com.ubcsolar.exception.NoForecastReportException;
import com.ubcsolar.exception.NoLoadedRouteException;
import com.ubcsolar.notification.ExceptionNotification;
import com.ubcsolar.notification.NewForecastReport;
import com.ubcsolar.notification.Notification;

public class WeatherController extends ModuleController {
	ForecastReport lastDownloadedReport = null;
	
	public WeatherController(GlobalController toAdd) {
		super(toAdd);
	}
	
	/**
	 * 
	 * @param numOfKMBetweenForecasts - we only have 1000 calls, so we probably can't get a forecast for every point
	 * in the Route. 
	 */
	public void downloadNewForecastsForRoute(int numOfKMBetweenForecasts){
		Route currentlyLoadedRoute = this.mySession.getMapController().getAllPoints();
		if(currentlyLoadedRoute == null){
			mySession.sendNotification(new ExceptionNotification(new NullPointerException(), "Tried to get forecast but route was null"));
			return;
		}
		List<GeoCoord> toGet = this.calculatePointsForForecast(numOfKMBetweenForecasts, currentlyLoadedRoute.getTrailMarkers());
		
		ForecastFactory forecastGetter = new ForecastFactory();
		List<ForecastIO> retrievedForecasts = forecastGetter.getForecasts(toGet);
		ForecastReport theReport = new ForecastReport(retrievedForecasts, this.mySession.getMapController().getLoadedMapName());
		lastDownloadedReport = theReport;
		this.mySession.sendNotification(new NewForecastReport(theReport));
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
				theForecastList.add(this.interprolateForecast(currentForecasts.get(indexOfStart), null, g));
			}
			else{
			theForecastList.add(this.interprolateForecast(currentForecasts.get(indexOfStart), currentForecasts.get(indexOfStart+1),g));
			}
		}
		
		ForecastReport toReturn = new ForecastReport(theForecastList, "SIMULATED " + this.mySession.getMapController().getLoadedMapName());
		return toReturn;
		
		
	}
	
	/*
	 * Supposed to interprolate between the start forecast and next forecast. Currently just returns the closest. 
	 */
	private ForecastIO interprolateForecast(ForecastIO startForecast, ForecastIO endForecast, GeoCoord currentLoc) {
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
			if(start.calculateDistance(g, DistanceUnit.KILOMETERS) > numOfKMBetween){
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
		
		/*if(n.getClass() == NewMapLoadedNotification.class){ //example notification handler
			//Do something
		}*/
		
	}

	/**
	 * registers to receive notifications
	 */
	@Override
	public void register() {

		//this.mySession.register(this, NewMapLoadedNotification.class); //example line.
		
	}

}
