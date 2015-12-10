package com.ubcsolar.notification;

import com.ubcsolar.common.Route;

public class NewMapLoadedNotification extends Notification {

	private final Route loadedRoute;
	private final String loadedFileName;
	
	
	public NewMapLoadedNotification(String filename, Route route){
		this.loadedFileName = filename;
		this.loadedRoute = route;
	}

	public Route getRoute(){
		return this.loadedRoute;
	}
	
	@Override
	public String getMessage(){
		return "Loaded map name is now: " + loadedRoute.getTitle();
	}
	
	
	public String getMapLoadedName(){
		return loadedFileName;
	}
}
