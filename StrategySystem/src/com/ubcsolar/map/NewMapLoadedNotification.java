package com.ubcsolar.map;

import com.ubcsolar.common.Notification;

public class NewMapLoadedNotification extends Notification {

	public final String mapLoadedName;
	
	public NewMapLoadedNotification(String name){
		super();
		this.mapLoadedName = name;
	}

	public String getMapLoadedName(){
		return mapLoadedName;
	}
}
