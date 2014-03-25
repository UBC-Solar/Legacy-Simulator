package com.ubcsolar.ui;

import java.util.ArrayList;
import java.util.List;

import com.ubcsolar.car.CarController;
import com.ubcsolar.common.Listener;
import com.ubcsolar.common.Notification;
import com.ubcsolar.map.MapController;
import com.ubcsolar.sim.SimController;
import com.ubcsolar.weather.WeatherController;

public class GlobalController {
	private List<Listener> listOfListeners; //listeners, waiting for a trigger listed in triggers/. 
	private List<Class<? extends Notification>> listOfTriggers; //list of Notifcations that Listeners are waiting for. 
								//listener in pos. 1 is waiting for the trigger in pos. 1, etc. 
	private GUImain mainWindow; 
	private MapController myMapController;
	private CarController myCarController;
	private SimController mySimController;
	private WeatherController myWeatherController;
	
	
	
	
	public GlobalController(GUImain mainWindow){
		this.mainWindow = mainWindow;
		myMapController = new MapController(this);
		myCarController = new CarController(this);
		mySimController = new SimController(this);
		myWeatherController = new WeatherController(this);
		
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

	public SimController getMySimController() {
		return mySimController;
	}


	public CarController getMyCarController() {
		return myCarController;
	}


	public WeatherController getMyWeatherController() {
		return myWeatherController;
	}


	
	
}
