package com.ubcsolar.ui;

import java.util.ArrayList;
import java.util.List;

import com.ubcsolar.common.Notification;
import com.ubcsolar.map.MapController;

public class GlobalController {
	private List<Listener> listOfListeners; //listeners, waiting for a trigger listed in triggers/. 
	private List<Class<? extends Notification>> listOfTriggers; //list of Notifcations that Listeners are waiting for. 
								//listener in pos. 1 is waiting for the trigger in pos. 1, etc. 
	private GUImain mainWindow; 
	private MapController myMapController;
	
	
	
	
	public GlobalController(GUImain mainWindow){
		this.mainWindow = mainWindow;
		myMapController = new MapController(this);
		listOfListeners = new ArrayList<Listener>();
		listOfTriggers = new ArrayList<Class<? extends Notification>>();
		
		
	}
	
	public void register(Listener l, Class<? extends Notification> n){
		listOfListeners.add(l);
		listOfTriggers.add(n);
		System.out.println("Got a register request for " + l.getClass());
	}
	public void notify(Notification n){
		System.out.println("Global Controller got a notification");
		for(int i=0; i<listOfTriggers.size(); i++){
			if(listOfTriggers.get(i) == n.getClass()){
				listOfListeners.get(i).notify(n);
			}
		}
	}
	
	public MapController getMapController(){
		return myMapController;
	}
	
	
}
