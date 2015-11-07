/**
 * This class is the program Controller, and the main interface between
 * the UI and the model. 
 * All notifications are sent here, and it sends them out
 * to registered classes. (important for eventual threading) 
 * Holds the references to the other controllers, so all UI requests will go through here. 
 */

package com.ubcsolar.ui;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import com.ubcsolar.car.CarController;
import com.ubcsolar.common.Listener;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.database.DatabaseController;
import com.ubcsolar.common.LogType;
import com.ubcsolar.map.MapController;
import com.ubcsolar.notification.*;
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
	private DatabaseController myDatabaseController; //the database controller
	private WeatherController myWeatherController; //the Weather controller.
	public final ImageIcon iconImage; //the icon for the program
	private Map<Class<? extends Notification>, List<Listener>> triggerNotifyMap;
		
	/**
	 * constructor. 
	 * @param mainWindow - the root window for the UI
	 * (Note: could change that order of execution to do a GlobalController first, then the GUIMain
	 */
	public GlobalController(GUImain mainWindow){
		iconImage = new ImageIcon("res/windowIcon.png");
		this.mainWindow = mainWindow;
		triggerNotifyMap = new HashMap<Class<? extends Notification>, List<Listener>>();
		//TODO: turn the 2 lists into a KVP<Class<?extends Notification>, ArrayList<Listener>> 
		//AKA a table indexed by the notifications. Look up the notification type, send it to all
		//listeners registered for it. 
		
		//Currently structured as a 1:1 list; the Class in position 3 of the Listeners list
		//is listening for the trigger in position 3 of the triggers list. 
		//there are much more elegant and efficient ways of doing that. 
		listOfListeners = new ArrayList<Listener>();
		listOfTriggers = new ArrayList<Class<? extends Notification>>();
		myMapController = new MapController(this);
		myCarController = new CarController(this);
		mySimController = new SimController(this);
		myWeatherController = new WeatherController(this);
		
		try {
			myDatabaseController = new DatabaseController(this);
		} catch (IOException e) {
			// Means that it couldn't create the DB file (currently a .csv)
			SolarLog.write(LogType.ERROR, System.currentTimeMillis(), "IO Error when creating DB, check file name and location");
			this.sendNotification(new ExceptionNotification(e, "IO Error when creating DB, check file name and location"));
		}
	}
	
	/**
	 * adds the listener to the registry for that notification. If a notification comes in,
	 * it will be sent to every class that registered for it. 
	 * @param l - the class to receive the notification
	 * @param n - the notification that the class is looking for. 
	 */
	public synchronized void register(Listener l, Class<? extends Notification> n){
		//The structure for storing these is a little kludgy, see the explanation above
		//TODO update the structures for these: Should use a Map<Triggerclasses, listOfClassesToNotify>, 
		//and then we can just pull up the exception directly rather than going therough the entire list
		//every time there is a notification (which could be a performance hit)
		
		//oldRegister(l, n);
		
		
		//Design decision here: It appears that setting up a map 
		//and making/adding to the list each time may be slower
		//at registering than the old dual-list system.
		//But it should be faster at sending them out(don't have to traverse the whole list
		//every time for every notification).
		//Each window only registers once, but I anticipate lots of sending notifications
		//(especially as the program grows in size!) so trade-off is likely worth it. 
		if(triggerNotifyMap.containsKey(n)){
			triggerNotifyMap.get(n).add(l);
		}
		else{
			List<Listener> temp = new LinkedList<Listener>();
			//Gotta make sure to initialize it.
			temp.add(l);
			triggerNotifyMap.put(n, temp); //Will have one value. 
		}
		System.out.println(l.getClass() + " registered for " + n);
		System.out.println("Total number registered: " + listOfListeners.size());
		System.out.println("Map Size: " + triggerNotifyMap.size());
	}
	
	/*trying to upgrade the registration system to be able to use
	 * a Map for performance reasons. This is the old way and I know it works.   
	 */
	private void oldRegister(Listener l, Class<? extends Notification> n) {
		listOfListeners.add(l);
		listOfTriggers.add(n);
	}

	/**
	 * sends the notification to all classes that have registered to receive it. 
	 * @param n - the notification being sent. 
	 */
	public synchronized void sendNotification(Notification n){
		SolarLog.write(LogType.NOTIFICATION, n.getTimeCreated(), n.getMessage());
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		//Can turn this on if you need to see when notifications go out.
		//System.out.println(dateFormat.format(cal.getTime()) + "- Global Controller got a notification " + n.getClass() );
		
		//oldSendNotification(n);
		//Hoping this will be much faster than the alternative. 
		/*TODO verify that a LinkedList iterator is O(n), and 
		not O(n^2) (as it would be for (for int i=0; i++)*/
		
		List<Listener> temp = this.triggerNotifyMap.get(n.getClass());
		if(temp != null){
			for(Listener l : this.triggerNotifyMap.get(n.getClass())){
			l.notify(n); //turn on when ready to replace old way below. 
			//don't want to run both at once. 
			}
		}
		else{
			/*TODO add check for sending notifications that aren't registered for yet. 
			May not be a bug, but would be good to know if the setup registration/notifications are 
			out of order.*/
			SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(),
					"When sent, no object wanted: " + n.getClass());
			
			}


	}
	
	
	/* Trying to implement the notification system
	 * using a Map for performance reasons. This is the old way which I know works. 
	 */
	private void oldSendNotification(Notification n) {
		for(int i=0; i<listOfTriggers.size(); i++){
			if(listOfTriggers.get(i).isInstance(n)){
				listOfListeners.get(i).notify(n);
			}
		}
		
	}

	//THESE METHODS ALLOW US TO GET THE EXISTING CONTROLLERS FROM ANY OTHER CLASS. 
	//It ensures we only ever have one instantiated at a time. 
	//Could probably turn them into Singleton methods, but this allows us to control them a bit. 
	//May have to modify this architecture to get threading to work properly; My initial 
	//plan was to have every controller in their own thread. 
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
	public DatabaseController getMyDataBaseController(){
		return this.myDatabaseController;
	}
	
	/**
	 * allows for graceful shutdown
	 */
	public void exit() {
		//TODO do something better with a disconnect failure. 
		try {
			this.myDatabaseController.saveAndDisconnect();
		} catch (IOException e) {
			SolarLog.write(LogType.ERROR, System.currentTimeMillis(), "IOException disconnecting from Database");
			this.sendNotification(new ExceptionNotification(e, "IOException disconnecting from Database"));
			e.printStackTrace();
		}
		SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(), "System Quitting");
		SolarLog.printOut();
		System.exit(0);
		
	}


	
	
}
