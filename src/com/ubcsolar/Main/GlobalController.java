/**
 * This class is the program Controller, and the main interface between
 * the UI and the model. 
 * All notifications are sent here, and it sends them out
 * to registered classes. (important for eventual threading) 
 * Holds the references to the other controllers, so all UI requests will go through here. 
 */

package com.ubcsolar.Main;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import com.ubcsolar.car.CarController;
import com.ubcsolar.common.Listener;
import com.ubcsolar.common.LogType;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.database.DatabaseController;
import com.ubcsolar.map.MapController;
import com.ubcsolar.notification.*;
import com.ubcsolar.sim.SimController;
import com.ubcsolar.ui.GUImain;
import com.ubcsolar.ui.LoadingFrameController;
import com.ubcsolar.weather.WeatherController;

public class GlobalController {
								//listener in pos. 1 is waiting for the trigger in pos. 1, etc. 
	@SuppressWarnings("unused")
	private GUImain mainWindow; //the root panel for the UI
	private MapController myMapController; //the Map controller
	private CarController myCarController; //the Car controller
	private SimController mySimController; //the Sim controller
	private DatabaseController myDatabaseController; //the database controller
	private WeatherController myWeatherController; //the Weather controller.
	private LoadingFrameController myLoadingFrameController; //loading frame controller
	public final ImageIcon iconImage; //the icon for the program
	private Map<Class<? extends Notification>, List<Listener>> triggerNotifyMap;
		
	/**
	 * constructor. 
	 * @param doBuildUI - if True, creates a UI. If false, starts the rest of the program, but no UI. 
	 * useful if you want to do automated tests or other automation with no user input
	 * @throws IOException 
	 */
	public GlobalController(boolean doBuildUI) throws IOException{
		iconImage = new ImageIcon("res/windowIcon.png");
		
		triggerNotifyMap = new HashMap<Class<? extends Notification>, List<Listener>>();		
		
		//TODO many of these are interrelated: the main window asks for the Database Controller. 
		//If it's 'null' (as in, didn't build properly), the program crashes. It shouldn't. 
		myMapController = new MapController(this);
		myCarController = new CarController(this);
		mySimController = new SimController(this);
		myWeatherController = new WeatherController(this);
		myLoadingFrameController = new LoadingFrameController(this);
		
		
		try {
			myDatabaseController = new DatabaseController(this);
		} catch (IOException e) {
			//Means that it couldn't create the DB file (currently a .csv)
			SolarLog.write(LogType.ERROR, System.currentTimeMillis(), "IO Error when creating DB, check file name and location");
			this.sendNotification(new ExceptionNotification(e, "IO Error when creating DB, check file name and location"));
		}
		
		if(doBuildUI){
			mainWindow = new GUImain(this); //See note below about starting this as a Runnable.
		}
		
		//TODO start Window in it's own thread. Here's the original boilerplate code 
		/*
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							GUImain window = new GUImain(this);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});*/
	}
	
/**
 * Kluge method for the Runnable needed for the UI. 
 * @return
 */
	public GlobalController getGlobalController(){
		return this;
	}
	
	/**
	 * Adds the listener to the registry for that notification. If a notification comes in,
	 * it will be sent to every class that registered for it. 
	 * Note: Uses the '==' or .equals() methods for check, so extended notification classes are treated
	 * as their own. 
	 * @param l - the class to receive the notification
	 * @param n - the notification that the class is looking for. 
	 */
	public synchronized void register(Listener l, Class<? extends Notification> n){		
		/* Design decision here: It appears that setting up a map 
		 * and making/adding to the list each time may be slower 
		 * at registering than the old dual-list system. 
		 * But it should be faster at sending them out(don't have to traverse the whole list 
		 * every time for every notification). 
		 * Each window only registers once, but I anticipate lots of sending notifications 
		 * (especially as the program grows in size!) so trade-off is likely worth it. 
		 */
		
		/*
		 * Second design decision note: OldRegister used '.instanceOf' to compare classes,
		 * so that one could register for an abstract parent class and get 
		 * notified for every instance of concrete sub-classes. 
		 * However Map uses .equals() or '==' to compare the keys, so it can't handle extended classes. 
		 * It might be possible to add that ability in in the future, but would likely come with a
		 * performance hit. The only place so far I wanted to use it was the Database, but it's not that
		 * hard to change it to register for each concrete subclass. 
		 */
		
		if(triggerNotifyMap.containsKey(n)){
			triggerNotifyMap.get(n).add(l);
		}
		else{
			List<Listener> temp = new LinkedList<Listener>();
			//Gotta make sure to initialize it.
			//You may consider implementing an ArrayList or Vector instead. I though performance would be worse
			//but a brief search of StackOverflow suggests it might actually be better. 
			
			temp.add(l);
			triggerNotifyMap.put(n, temp); //Will have one value. 
		}
		SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(),l.getClass() + " registered for " + n);
	}
	
	/**
	 * sends the notification to all classes that have registered to receive it. 
	 * @param n - the notification being sent. 
	 */
	public synchronized void sendNotification(Notification n){
		SolarLog.write(LogType.NOTIFICATION, n.getTimeCreated(), n.getMessage());

		//Can turn this on if you need to see when notifications go out.
		//DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		//Calendar cal = Calendar.getInstance();
		//System.out.println(dateFormat.format(cal.getTime()) + "- Global Controller got a notification " + n.getClass() );
		
		//oldSendNotification(n);
		//Hoping this will be much faster than the alternative. 

		
		List<Listener> temp = this.triggerNotifyMap.get(n.getClass());
		if(temp != null){
			/*use an 'enhanced for loop' to use the iterator. (get on LinkedLists is O(n),
			but the iterator on all list types should be O(1) for each list item) */
			for(Listener l : this.triggerNotifyMap.get(n.getClass())){ 
			l.notify(n); 
			}
		}
		else{
			/* If you reached here, either there is no entry for the notification class
			 * or there has been no list created for it (either way, no one wants it) 
			 * May not be a bug, but would be good to know if the setup registration/notifications are
			 * out of order.*/
			SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(),
					"When sent, no object wanted: " + n.getClass());
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
	public LoadingFrameController getMyLoadingFrameController(){
		return this.myLoadingFrameController;
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
