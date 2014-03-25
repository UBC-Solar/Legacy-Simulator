package com.ubcsolar.map;

import java.io.IOException;

import com.ubcsolar.common.Notification;
import com.ubcsolar.ui.GlobalController;

public class MapController {
	
	private DataHolder current;
	private GlobalController mySession;
	public MapController(GlobalController toAdd){
		mySession = toAdd;
		
	}
	public void load(String filename) throws IOException{
		System.out.println("Loading " + filename);
		current = new DataHolder(filename, this);	
		sendNotification(new NewMapLoadedNotification(filename));
	}
	
	
	public void sendNotification(Notification n){
		System.out.println("sending notifcation " + n.getClass());
		mySession.notify(n);
		
	}
	
	//TODO implement

	public String getLoadedMapName(){ 
		if(current == null){
			return null;
		}
		else{
		return "hello world";
		}
		
	}

}
