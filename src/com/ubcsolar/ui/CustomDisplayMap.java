/**
 * This class wraps a JMapViewer
 */

package com.ubcsolar.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.MapObjectImpl;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.Style;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

import com.ubcsolar.common.LocationReport;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.PointOfInterest;
import com.ubcsolar.common.Route;
import com.ubcsolar.notification.NewMapLoadedNotification;

public class CustomDisplayMap extends JMapViewer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private MapMarker carCurrentLocation;

	public CustomDisplayMap() {
		super();
	}

	public void changeDrawnRoute(Route newRouteToLoad){
		this.removeAllMapPolygons();
		this.removeAllMapMarkers();
		this.addNewRouteToMap(newRouteToLoad);
	}
	
	public void addNewCarLocationToMap(LocationReport newLocation){
		this.removeMapMarker(carCurrentLocation);
		Style testStyle = new Style(Color.BLACK, Color.RED, null, MapObjectImpl.getDefaultFont());
		MapMarkerDot newLocationDot = new MapMarkerDot(null,"THE CAR", new Coordinate(newLocation.getLocation().getLat(), newLocation.getLocation().getLon()), testStyle);
		this.addMapMarker(newLocationDot);
		this.carCurrentLocation = newLocationDot; //so we can remove it next time. 
	}
	
	public void addNewRouteToMap(Route newRouteToLoad){
		List<Coordinate> listForPolygon = new ArrayList<Coordinate>(newRouteToLoad.getTrailMarkers().size());
		for(GeoCoord geo : newRouteToLoad.getTrailMarkers()){
			listForPolygon.add(new Coordinate(geo.getLat(), geo.getLon()));
		}
		
		//adding this in to make it a single line, otherwise it draws a line from end to start.
		//There may be a better way of doing this...
		for(int i = newRouteToLoad.getTrailMarkers().size()-1; i>=0; i--){
			GeoCoord toAdd = newRouteToLoad.getTrailMarkers().get(i);
			listForPolygon.add(new Coordinate(toAdd.getLat(), toAdd.getLon()));
		}
		
		this.addMapPolygon(new MapPolygonImpl(listForPolygon));
		//for(int i = 0; i<newRouteToLoad.getPointsOfIntrest().size(); i++){
		for(PointOfInterest temp : newRouteToLoad.getPointsOfIntrest()){
			GeoCoord newSpot = temp.getLocation();
			String name = temp.getName().split(",")[0]; //don't need the whole "city, state, country, continent, earth" name
			this.addMapMarker(new MapMarkerDot(name, new Coordinate(newSpot.getLat(), newSpot.getLon())));
		}
		
		this.repaint();
		
	}
	

}
