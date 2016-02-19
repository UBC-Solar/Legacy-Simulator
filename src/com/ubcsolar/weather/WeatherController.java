package com.ubcsolar.weather;

import java.util.ArrayList;
import java.util.List;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.common.DistanceUnit;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.ModuleController;
import com.ubcsolar.common.Route;
import com.ubcsolar.notification.ExceptionNotification;
import com.ubcsolar.notification.Notification;

public class WeatherController extends ModuleController {

	
	public WeatherController(GlobalController toAdd) {
		super(toAdd);
	}
	
	/**
	 * 
	 * @param numOfKMBetweenForecasts - we only have 1000 calls, so we probably can't get a forecast for every point
	 * in the Route. 
	 */
	public void loadForecastsForRoute(int numOfKMBetweenForecasts){
		Route currentlyLoadedRoute = this.mySession.getMapController().getAllPoints();
		if(currentlyLoadedRoute == null){
			mySession.sendNotification(new ExceptionNotification(new NullPointerException(), "Tried to get forecast but route was null"));
			return;
		}
		List<GeoCoord> toGet = this.calculatePointsForForecast(numOfKMBetweenForecasts, currentlyLoadedRoute.getTrailMarkers());
		
		ForecastFactory forecastGetter = new ForecastFactory();
		System.out.println("GOT: " + forecastGetter.getForecasts(toGet).size() + " FORECASTS");
		
		
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
