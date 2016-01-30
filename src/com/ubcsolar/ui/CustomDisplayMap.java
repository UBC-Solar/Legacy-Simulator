/**
 * This class wraps a JMapViewer
 */

package com.ubcsolar.ui;

import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;

import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.Route;
import com.ubcsolar.notification.NewMapLoadedNotification;

public class CustomDisplayMap extends JMapViewer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	public CustomDisplayMap() {
		super();
	}

	

	private void drawNewMap(NewMapLoadedNotification notification) {
		/*Coordinate vancouver = new Coordinate(49.282,-123.12);
		Coordinate merritt = new Coordinate(50.1119,-120.78);
		Coordinate thirdPoint = new Coordinate(49.282, -120.78);
		mainPanel.addMapMarker(new MapMarkerDot(vancouver)); //Vancouver
		mainPanel.addMapMarker(new MapMarkerDot(merritt)); //merritt
		mainPanel.addMapMarker(new MapMarkerDot(thirdPoint)); //third point to make a triangle
		List<Coordinate> toAdd = new ArrayList<Coordinate>(3);
		toAdd.add(vancouver);
		toAdd.add(merritt);
		toAdd.add(thirdPoint);
		mainPanel.addMapPolygon(new MapPolygonImpl(toAdd));
		System.out.println(n.getRoute().getTrailMarkers().get(0));
		*/
		Route temp = notification.getRoute();
		List<Coordinate> listForPolygon = new ArrayList<Coordinate>(temp.getTrailMarkers().size());
		for(GeoCoord geo : temp.getTrailMarkers()){
			listForPolygon.add(new Coordinate(geo.getLat(), geo.getLon()));
		}
		
		//adding this in to make it a single line, otherwise it draws a line from end to start. 
		for(int i = temp.getTrailMarkers().size()-1; i>=0; i--){
			GeoCoord toAdd = temp.getTrailMarkers().get(i);
			listForPolygon.add(new Coordinate(toAdd.getLat(), toAdd.getLon()));
		}
		
		this.addMapPolygon(new MapPolygonImpl(listForPolygon));
		
	}

}
