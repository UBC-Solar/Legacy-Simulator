/**
 * This class is forms part of the interface between the UI and the model. 
 * All Map notifications will be sent through here, and all UI requests to the model
 * will come through here. 
 *  
 */


//TODO: check threading. Is the controller threading the sub classes?
package com.ubcsolar.map;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.ubcsolar.common.ModuleController;
import com.ubcsolar.notification.RouteDataAsRequestedNotification;
import com.ubcsolar.notification.NewMapLoadedNotification;
import com.ubcsolar.notification.Notification;
import com.ubcsolar.ui.GlobalController;

public class MapController extends ModuleController{

	
	private DataHolder currentRoute; //the dataHolder

	public MapController(GlobalController toAdd) {
		super(toAdd);
	}
	
	
	/**
	 * registers for any notifications it needs to hear
	 */
	@Override
	public void register() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * loads a kml route file
	 * @param filename - must be a valid KML file. Referential or full network paths acceptable
	 * @throws IOException - if unable to load file. 
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 */
	public void load(String filename) throws IOException, SAXException, ParserConfigurationException{
		System.out.println("Loading " + filename);
		currentRoute = new DataHolder(filename, this);	
		sendNotification(new NewMapLoadedNotification(filename));
		//Decided against automatically sending all data points. 
		//If the UI element wants them, it can specifiy it. 
		//getAllPoints();
	}
	
	public void getAllPoints(){
		sendNotification(new RouteDataAsRequestedNotification(currentRoute.getAllPoints()));
		
	}
	
	

	/**
	 * gets the name of the currently loaded map. Shouldn't really be needed, 
	 * as notifications will be sent out. 
	 * @return filename - the full network path name of the file.
	 */
	public String getLoadedMapName(){ 
		if(currentRoute == null){
			return null;
		}
		//TODO: chenge it from network path to just file name
		else{
			return currentRoute.getFileName();
		}
		
	}

	/**
	 * Receives any notifications it registered for here. 
	 */
	@Override
	public void notify(Notification n) {
		// TODO place to call things if something changes. 
		//don't imagine Map needs to know any notifications. 
		
	}

}
