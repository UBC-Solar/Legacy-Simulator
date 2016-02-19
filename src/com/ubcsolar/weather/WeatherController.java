package com.ubcsolar.weather;

import com.ubcsolar.Main.GlobalController;
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
