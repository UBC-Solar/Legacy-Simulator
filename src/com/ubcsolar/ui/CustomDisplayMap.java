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
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;

import com.ubcsolar.common.LocationReport;
import com.github.dvdme.ForecastIOLib.ForecastIO;
import com.ubcsolar.common.ForecastReport;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.PointOfInterest;
import com.ubcsolar.common.Route;
import com.ubcsolar.notification.NewMapLoadedNotification;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CustomDisplayMap extends JMapViewer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private MapMarker carCurrentLocation; //car's current location
	private boolean showCarLocation; //initial value set to equal checkbox
	private List<MapMarker> forecasts; //route forecasts
	private boolean showForecasts;//initial value set to equal checkbox
	private List<MapMarker> routePOIs; //POIs (Mostly cities) along the route
	private boolean showPOIs; //initial value set to equal checkbox
	private MapPolygon routeBreadcrumbs; //the line that shows the trail
	private boolean showRouteBreadcrumbs; //initial value set to equal checkbox
	
	public CustomDisplayMap() {
		super();
		JCheckBox chckbxForecasts = new JCheckBox("Forecasts");
		chckbxForecasts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showForecasts = chckbxForecasts.isSelected();
				refreshMap();
			}
		});
		chckbxForecasts.setSelected(true);
		showForecasts = chckbxForecasts.isSelected();
		chckbxForecasts.setBounds(46, 7, 82, 23);
		add(chckbxForecasts);
		
		JCheckBox chckbxCities = new JCheckBox("Cities");
		chckbxCities.setSelected(true);
		showPOIs = chckbxCities.isSelected();
		chckbxCities.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showPOIs = chckbxCities.isSelected();
				refreshMap();
			}
		});
		chckbxCities.setBounds(134, 7, 73, 23);
		add(chckbxCities);
		
		JCheckBox chckbxCarLocation = new JCheckBox("Car Location");
		chckbxCarLocation.setSelected(true);
		showCarLocation = chckbxCarLocation.isSelected();
		chckbxCarLocation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCarLocation = chckbxCarLocation.isSelected();
				refreshMap();
			}
		});
		chckbxCarLocation.setBounds(209, 7, 120, 23);
		add(chckbxCarLocation);
		
		JCheckBox chckbxRoute = new JCheckBox("Route");
		chckbxRoute.setSelected(true);
		showRouteBreadcrumbs = chckbxRoute.isSelected();
		chckbxRoute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showRouteBreadcrumbs = chckbxRoute.isSelected();
				refreshMap();
			}
		});
		chckbxRoute.setBounds(331, 7, 63, 23);
		add(chckbxRoute);
	}

	public void changeDrawnRoute(Route newRouteToLoad){
		this.removeAllMapPolygons();
		this.removeAllMapMarkers();
		this.addNewRouteToMap(newRouteToLoad);
	}
	
	public void addNewCarLocationToMap(LocationReport newLocation){
		this.removeMapMarker(carCurrentLocation); //not sure if this should be here if we repaint anyway
		Style testStyle = new Style(Color.BLACK, Color.RED, null, MapObjectImpl.getDefaultFont());
		MapMarkerDot newLocationDot = new MapMarkerDot(null,"THE CAR", new Coordinate(newLocation.getLocation().getLat(), newLocation.getLocation().getLon()), testStyle);
		this.carCurrentLocation = newLocationDot; //so we can remove it next time. 
		this.refreshMap();
	}
	
	public void addNewRouteToMap(Route newRouteToLoad){
		List<Coordinate> listForPolygon = new ArrayList<Coordinate>(newRouteToLoad.getTrailMarkers().size());
		//remove the old one 
		this.removeMapPolygon(this.routeBreadcrumbs);
		if(this.routePOIs != null){
			for(MapMarker m : routePOIs){
				this.removeMapMarker(m);
			}
		}
		
		for(GeoCoord geo : newRouteToLoad.getTrailMarkers()){
			listForPolygon.add(new Coordinate(geo.getLat(), geo.getLon()));
		}
		
		//adding this in to make it a single line, otherwise it draws a line from end to start.
		//There may be a better way of doing this...
		for(int i = newRouteToLoad.getTrailMarkers().size()-1; i>=0; i--){
			GeoCoord toAdd = newRouteToLoad.getTrailMarkers().get(i);
			listForPolygon.add(new Coordinate(toAdd.getLat(), toAdd.getLon()));
		}
		
		this.routeBreadcrumbs = new MapPolygonImpl(listForPolygon);
		
		this.routePOIs = new ArrayList<MapMarker>(newRouteToLoad.getPointsOfIntrest().size());
		for(PointOfInterest temp : newRouteToLoad.getPointsOfIntrest()){
			GeoCoord newSpot = temp.getLocation();
			String name = temp.getName().split(",")[0]; //don't need the whole "city, state, country, continent, earth" name
			routePOIs.add(new MapMarkerDot(name, new Coordinate(newSpot.getLat(), newSpot.getLon())));
		}
		this.refreshMap(); //will paint it on if it's supposed to be there
		this.repaint();
		
	}
	
	public void addForecastsToMap(ForecastReport theReport){
		Style forecastStyle = new Style(Color.BLACK, Color.GREEN, null, MapObjectImpl.getDefaultFont());
		
		if(this.forecasts != null){
			for(MapMarker m : forecasts){
				this.removeMapMarker(m);
			}
		}
		
		forecasts = new ArrayList<MapMarker>(theReport.getForecasts().size());
		for(int i= 0; i<theReport.getForecasts().size(); i++){
			ForecastIO fc = theReport.getForecasts().get(i);
			Coordinate location = new Coordinate(fc.getLatitude(), fc.getLongitude());
			String name = "" + i;
			MapMarkerDot newLocationDot = new MapMarkerDot(null,name,location, forecastStyle);
			
			forecasts.add(newLocationDot);
		}
		this.refreshMap();
	}
	
	public void refreshMap(){
		/* there's two ways to do this; one is compare each object and try
		 * to add or remove it according to it's true/false value. 
		 * The other way is to remove all of them and re-add everything that's supposed
		 * to be there. 
		 * The second choice is more computationally expensive, but less risk for bugs
		 * (can't forget about anything). 
		 * Should performance become an issue, this is a good method to optimize. 
		 */
		this.removeAllMapMarkers();
		this.removeAllMapPolygons();
		this.removeAllMapRectangles();

		if(this.showRouteBreadcrumbs && routeBreadcrumbs != null){
			this.addMapPolygon(routeBreadcrumbs);
		}
		
		if(this.showPOIs && routePOIs != null){
			for(MapMarker m : routePOIs){
				this.addMapMarker(m);
			}
		}
		
		if(this.showForecasts && forecasts != null){
			for(MapMarker m : forecasts){
				this.addMapMarker(m);
			}
		}
	
		if(this.showCarLocation && carCurrentLocation != null){
			this.addMapMarker(carCurrentLocation);
		}
	}
	
}
