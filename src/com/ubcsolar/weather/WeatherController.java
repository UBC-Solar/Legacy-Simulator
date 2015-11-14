package com.ubcsolar.weather;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.common.ModuleController;
import com.ubcsolar.notification.NewMapLoadedNotification;
import com.ubcsolar.notification.Notification;

public class WeatherController extends ModuleController {

	public WeatherController(GlobalController toAdd) {
		super(toAdd);
	}

	
	public void loadMetars(String filename){
		ReadMETAR.ReadMETAR(filename, this);
		System.out.println("Order to load metars received");
	}
	
	public void loadTafs(String filename){
		//ReadTAFS.ReadTAFS(filename, this);
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
