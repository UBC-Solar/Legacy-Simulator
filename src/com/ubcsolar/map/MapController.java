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
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.JDOMException;
import org.xml.sax.SAXException;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.common.LocationReport;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.ModuleController;
import com.ubcsolar.common.Route;
import com.ubcsolar.exception.NoLoadedRouteException;
import com.ubcsolar.notification.ExceptionNotification;
import com.ubcsolar.notification.NewLocationReportNotification;
import com.ubcsolar.notification.NewMapLoadedNotification;
import com.ubcsolar.notification.Notification;

public class MapController extends ModuleController{

	JdomkmlInterface myJDOMMap;
	GPSFromPhoneReceiver gpsBlueToothConnection;
	LocationReport lastReported;
	Map<GeoCoord, Double> distanceAlongRoute;

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
		//getAllPoints();
	}
	
	/**
	 * 
	 * @return
	 */
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
	 * 
	 * @return the last received LocationReport, or null if never has gotten one. 
	 */
	public LocationReport getLastReportedLocation(){
		return this.lastReported;
	}
	/**
	 * Receives any notifications it registered for here. 
	 */
	@Override
	public void notify(Notification n) { 
		//don't imagine Map needs to know any notifications. 
		
	}
	
	public void connectToCellPhone(String comPort){
		try{
		gpsBlueToothConnection = new GPSFromPhoneReceiver(comPort, this, "Raven", "PhoneGPS");
		gpsBlueToothConnection.run();
		}
		catch(NullPointerException e){
			this.mySession.sendNotification(new ExceptionNotification(e, "NULL Error starting GPS BT connection. Check that it's not already running"));
			e.printStackTrace();
		}
	}
	
	public void disconnectCellPhone(){
		gpsBlueToothConnection.stop();
	}
	
	public void recordNewCarLocation(LocationReport carLocationReported){
		this.lastReported = carLocationReported;
		sendNotification(new NewLocationReportNotification(carLocationReported));
	}
	
	/** 
	 *  Finds the distance along the route (NOT as the crow flies) from the start of the
	 * route to the breadcrumb point closest to the requested location. 
	 * Ignores the distance from breadcrumb to target. This assumes the breadcrumbs 
	 * are numerous enough not to cause a significant issue. (i.e more than 1 per km)
	 * i.e if you load a Toronto->Halifax route and target is Vancouver, method will return 
	 * km '0.0' aka Toronto (instead of -3000km).
	 * i.e if you load a Vancouver->Toronto map and target is calgary, method will return
	 * the km for a point somewhere in southern Alberta (along the route).   
	 * @param location
	 * @return
	 * @throws NoLoadedRouteException
	 */
	public double findDistanceAlongLoadedRoute(GeoCoord location) throws NoLoadedRouteException{
		GeoCoord newTarget = this.findClosestPointOnRoute(location);
		return this.findDistanceAlongLoadedRouteExact(newTarget);
		
	}
	/**
	 * Finds the distance along the route (NOT as the crow flies) from the start of the
	 * route to the given location. 
	 * @param location: location in question. This location MUST be one of the trail markers
	 * along the route in order for the following method to work correctly. Otherwise, the method
	 * will give you the total distance along the route.
	 * @return distance along route from start of route to location
	 * @throws NoLoadedRouteException 
	 */
	
	private double findDistanceAlongLoadedRouteExact(GeoCoord location) throws NoLoadedRouteException{
		
		Route currentRoute = getAllPoints();
		if(currentRoute == null){
			throw new NoLoadedRouteException();
		}
		List<GeoCoord> trailMarkers = currentRoute.getTrailMarkers();
		if(location.calculateDistance(trailMarkers.get(0))==0){
			return 0.0;
		}
		int trailMarkerIndex = 1;
		double travelDistance = 0.0;
		while(trailMarkerIndex < trailMarkers.size() &&
				location.calculateDistance(trailMarkers.get(trailMarkerIndex)) != 0){
			travelDistance += trailMarkers.get(trailMarkerIndex-1).calculateDistance(
					trailMarkers.get(trailMarkerIndex));
			trailMarkerIndex++;
		}
		return travelDistance;
	}
	
	/**
	 * Finds the closest point (breadcrumb) on the currently loaded map. 
	 * @param target
	 * @return
	 */
	public GeoCoord findClosestPointOnRoute(GeoCoord target){
		/*
		 * Noah note: June 20 2016, This algo needs optimization (runs in O(n) currently)
		 * but we'll need to build an index to do something like binary search.
		 * The problem is that to do binary search, you need to be able to split
		 * the list into two around an arbitrary point. If you pick
		 * two index points that are side-by-side, you could almost check to see which 
		 * one is closer to the target point, and then take that side of the list. 
		 * 
		 * However, it'd be possible to have a route (shaped like a lightbulb) where
		 * the wrong side would be selected (target point would be 
		 * closer to the incorrect pivot point)
		 * 
		 * Might be doable if you index/sort the list with distance from start point.
		 * 
		 */
		
		List<GeoCoord> breadcrumbs = myJDOMMap.getRoute().getTrailMarkers();
		int closestPointIndex = -1;
		double minDistance = Double.MAX_VALUE; //anything is guaranteed to be less
		
		for(int i = 0; i<breadcrumbs.size(); i++){
			if(target.calculateDistance(breadcrumbs.get(i))<minDistance){
				closestPointIndex = i;
				minDistance = target.calculateDistance(breadcrumbs.get(i));
			}
		}
		
		return breadcrumbs.get(closestPointIndex);
		
		
	}
	
	/**
	 * Calculates the total distance of the route as driven, as opposed to an 
	 * 'as-the-crow-flies' calculation from start to end. 
	 * @return
	 */
	public double findTotalDistanceAlongLoadedRoute(){
		List<GeoCoord> trailMarkers = getAllPoints().getTrailMarkers();
		int trailMarkerIndex = 1;
		double travelDistance = 0.0;
		while(trailMarkerIndex < trailMarkers.size()){
			travelDistance += trailMarkers.get(trailMarkerIndex-1).calculateDistance(
					trailMarkers.get(trailMarkerIndex));
			trailMarkerIndex++;
		}
		return travelDistance;
	}

	
	public boolean hasMapLoaded(){
		return this.myJDOMMap == null;
	}
}
