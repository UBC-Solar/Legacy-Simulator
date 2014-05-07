/**
 * This class is the program Controller, and the main interface between
 * the UI and the model. 
 * All notifications sent here, and it sends them
 * to registered classes. 
 * Holds the references to the other controllers, so all UI requests will go through here. 
 */

package com.ubcsolar.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.swing.ImageIcon;

import com.ubcsolar.car.CarController;
import com.ubcsolar.common.Listener;
import com.ubcsolar.common.Log;
import com.ubcsolar.common.LogType;
import com.ubcsolar.map.MapController;
import com.ubcsolar.notification.Notification;
import com.ubcsolar.sim.SimController;
import com.ubcsolar.weather.WeatherController;

public class GlobalController {
	private List<Listener> listOfListeners; //listeners, waiting for a trigger listed in triggers/. 
	private List<Class<? extends Notification>> listOfTriggers; //list of Notifcations that Listeners are waiting for. 
								//listener in pos. 1 is waiting for the trigger in pos. 1, etc. 
	private GUImain mainWindow; //the root panel for the UI
	private MapController myMapController; //the Map controller
	private CarController myCarController; //the Car controller
	private SimController mySimController; //the Sim controller
	private WeatherController myWeatherController; //the Weather controller.
	public final ImageIcon iconImage;
	


	
	
	/**
	 * constructor. 
	 * @param mainWindow - the root window for the UI
	 */
	public GlobalController(GUImain mainWindow){
		iconImage = new ImageIcon("res/squareLogo.png");
		this.mainWindow = mainWindow;
		//TODO: turn the 2 lists into a KVP<Class<?extends Notification>, ArrayList<Listener>> 
		//AKA a table indexed by the notifications. Look up the notification type, send it to all
		//listeners registered for it. 
		listOfListeners = new ArrayList<Listener>();
		listOfTriggers = new ArrayList<Class<? extends Notification>>();
		myMapController = new MapController(this);
		myCarController = new CarController(this);
		mySimController = new SimController(this);
		myWeatherController = new WeatherController(this);

		
	}
	
	/**
	 * adds the listener to the registry for that notification. If a notification comes in,
	 * it will be sent to every class that registered for it. 
	 * @param l - the class to receive the notification
	 * @param n - the notification that the class is looking for. 
	 */
	public synchronized void register(Listener l, Class<? extends Notification> n){
		listOfListeners.add(l);
		listOfTriggers.add(n);
		System.out.println(l.getClass() + " registered for " + n);
		System.out.println("Total number registered: " + listOfListeners.size());
	}
	
	/**
	 * sends the notification to all classes that have registered to receive it. 
	 * @param n - the notification being sent. 
	 */
	public synchronized void sendNotification(Notification n){
		Log.write(LogType.NOTIFICATION, n.getTime(), n.getMessage());
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		
		System.out.println(dateFormat.format(cal.getTime()) + "- Global Controller got a notification " + n.getClass() );
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

	
	/**
	 * allows for graceful shutdown
	 */
	public void exit() {
		Log.printOut();
		System.exit(0);
		
	}


	
	
}
