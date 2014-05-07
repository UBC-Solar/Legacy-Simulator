package com.ubcsolar.map;

import com.ubcsolar.notification.Notification;



public class NewMapLoadedNotification extends Notification {

	public final String mapLoadedName;
	
	public NewMapLoadedNotification(String name){
		super();
		this.mapLoadedName = name;
	}

	
	@Override
	public String getMessage(){
		return "Loaded map name is now: " + mapLoadedName;
	}
	
	
	public String getMapLoadedName(){
		return mapLoadedName;
	}
}
