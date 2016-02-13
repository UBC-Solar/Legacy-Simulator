/**
 * This class is forms part of the interface between the UI and the model. 
 * All Map notifications will be sent through here, and all UI requests to the model
 * will come through here. 
 *  
 */


//TODO: check threading. Is the controller threading the sub classes?
package com.ubcsolar.map;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.JDOMException;
import org.xml.sax.SAXException;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.common.LocationReport;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.ModuleController;
import com.ubcsolar.common.Route;
import com.ubcsolar.notification.ExceptionNotification;
import com.ubcsolar.notification.NewLocationReportNotification;
import com.ubcsolar.notification.NewMapLoadedNotification;
import com.ubcsolar.notification.Notification;

public class MapController extends ModuleController{

	JdomkmlInterface myJDOMMap;
	GPSFromPhoneReceiver gpsBlueToothConnection;

	public MapController(GlobalController toAdd) throws IOException{
		super(toAdd);	
	}
	
	
	/**
	 * registers for any notifications it needs to hear
	 */
	@Override
	public void register() {

	}
	
	/**
	 * loads a kml route file
	 * @param filename - must be a valid KML file. Referential or full network paths acceptable
	 * @throws IOException - if unable to load file. 
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws JDOMException 
	 */
	public void load(File fileToLoad) throws IOException, SAXException, ParserConfigurationException, JDOMException{
		
		myJDOMMap = new JdomkmlInterface(fileToLoad);
		sendNotification(new NewMapLoadedNotification(myJDOMMap.getLoadedFileName(), myJDOMMap.getRoute()));
		//Decided against automatically sending all data points. 
		//If the UI element wants them, it can specifiy it. 
		//getAllPoints(); -83.77817,39.89031
		this.recordNewCarLocation(new LocationReport(new GeoCoord(41.89031, -81.77817,317.757354), "Raven", "Phone"));
	}
	
	public Route getAllPoints(){
		if(this.myJDOMMap == null){
			return null;
		}
		return this.myJDOMMap.getRoute();
	}
	
	/**
	 * gets the name of the currently loaded map. Shouldn't really be needed, 
	 * as notifications will be sent out. 
	 * @return filename - the full network path name of the file.
	 */
	public String getLoadedMapName(){ 
		if(myJDOMMap == null){
			return null;
		}
		//TODO: change it from network path to just file name
		else{
			return myJDOMMap.getLoadedFileName();
		}
		
	}

	/**
	 * Receives any notifications it registered for here. 
	 */
	@Override
	public void notify(Notification n) { 
		//don't imagine Map needs to know any notifications. 
		
	}
	
	public void connectToCellPhone(){
		try{
		gpsBlueToothConnection = new GPSFromPhoneReceiver(this, "Raven", "PhoneGPS");
		gpsBlueToothConnection.run();
		}
		catch(NullPointerException e){
			this.mySession.sendNotification(new ExceptionNotification(e, "NULL Error starting GPS BT connection. Check that it's not already running"));
			e.printStackTrace();
		}
	}
	
	public void recordNewCarLocation(LocationReport carLocationReported){
		sendNotification(new NewLocationReportNotification(carLocationReported));
	}

}
