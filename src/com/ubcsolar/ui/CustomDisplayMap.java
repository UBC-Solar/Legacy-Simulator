/**
 * This class wraps a JMapViewer
 */

package com.ubcsolar.ui;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.DefaultMapController;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.MapObjectImpl;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.MemoryTileCache;
import org.openstreetmap.gui.jmapviewer.Style;
import org.openstreetmap.gui.jmapviewer.Tile;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

import com.ubcsolar.common.LocationReport;
import com.ubcsolar.common.LogType;
import com.github.dvdme.ForecastIOLib.FIODataBlock;
import com.github.dvdme.ForecastIOLib.ForecastIO;
import com.ubcsolar.Main.GlobalValues;
import com.ubcsolar.common.ForecastReport;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.PointOfInterest;
import com.ubcsolar.common.Route;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.notification.NewMapLoadedNotification;

import javax.imageio.ImageIO;
import javax.swing.JCheckBox;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.UIManager;

public class CustomDisplayMap extends JMapViewer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Color defaultColorForThings = new Color(204, 0, 204);
	private final Font defaultFontForThings = new Font("Tahoma", Font.BOLD, 13);

	private MapMarker carCurrentLocation; // car's current location
	private boolean showCarLocation; // initial value set to equal checkbox
	private boolean showSpeeds;
	private List<MapMarker> speeds;
	private List<MapMarker> forecasts; // route forecasts
	private List<GeoCoord> points;
	private boolean showForecasts;// initial value set to equal checkbox
	private List<MapMarker> routePOIs; // POIs (Mostly cities) along the route
	private boolean showPOIs; // initial value set to equal checkbox
	private MapPolygon routeBreadcrumbs; // the line that shows the trail
	private boolean showRouteBreadcrumbs; // initial value set to equal checkbox
	private JRadioButton rdbtnSateliteoffline;
	private JRadioButton rdbtnSattelite;
	private JRadioButton rdbtnDefaultMapOffline;
	private JRadioButton rdbtnDefaultMap;

	public CustomDisplayMap() {
		super(new saveToDiskCache(), 8); // 8 is used in the default constructor
		new DefaultMapController(this); // not called in the second
										// constructor... must be a bug?
		// super();
		// this.setTileSource(new
		// OfflineOsmTileSource("File:///Users/Noah/Desktop/testMapFiles/",1,2));
		JCheckBox chckbxForecasts = new JCheckBox("Forecasts");

		chckbxForecasts.setForeground(defaultColorForThings);
		chckbxForecasts.setFont(defaultFontForThings);
		chckbxForecasts.setOpaque(false);
		chckbxForecasts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showForecasts = chckbxForecasts.isSelected();
				refreshMap();
			}
		});
		chckbxForecasts.setSelected(true);
		showForecasts = chckbxForecasts.isSelected();
		chckbxForecasts.setBounds(339, 10, 97, 23);
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
		chckbxCities.setForeground(defaultColorForThings);
		chckbxCities.setFont(defaultFontForThings);
		chckbxCities.setOpaque(false);

		chckbxCities.setBounds(62, 10, 72, 23);
		add(chckbxCities);

		JCheckBox chckbxCarLocation = new JCheckBox("Car Location");
		chckbxCarLocation.setForeground(defaultColorForThings);
		chckbxCarLocation.setFont(defaultFontForThings);
		chckbxCarLocation.setOpaque(false);
		chckbxCarLocation.setSelected(true);
		showCarLocation = chckbxCarLocation.isSelected();
		chckbxCarLocation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCarLocation = chckbxCarLocation.isSelected();
				refreshMap();
			}
		});
		chckbxCarLocation.setBounds(136, 10, 116, 23);
		add(chckbxCarLocation);

		JCheckBox chckbxRoute = new JCheckBox("Route");
		chckbxRoute.setForeground(defaultColorForThings);
		chckbxRoute.setFont(defaultFontForThings);
		chckbxRoute.setOpaque(false);
		chckbxRoute.setSelected(true);
		showRouteBreadcrumbs = chckbxRoute.isSelected();
		chckbxRoute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showRouteBreadcrumbs = chckbxRoute.isSelected();
				refreshMap();
			}
		});
		chckbxRoute.setBounds(254, 10, 83, 23);
		add(chckbxRoute);

		rdbtnSattelite = new JRadioButton("Satellite");
		rdbtnSattelite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateTileSource(MapSource.MAPQUEST_SAT);
			}
		});

		JCheckBox chckbxSpeeds = new JCheckBox("Recommended Speeds\r\n");
		chckbxSpeeds.setForeground(defaultColorForThings);
		chckbxSpeeds.setBounds(445, 10, 171, 23);
		chckbxSpeeds.setFont(defaultFontForThings);
		chckbxSpeeds.setOpaque(false);
		chckbxSpeeds.setSelected(true);
		showSpeeds = chckbxSpeeds.isSelected();
		chckbxSpeeds.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				showSpeeds = chckbxSpeeds.isSelected();
				refreshMap();
			}
		});
		add(chckbxSpeeds);

		rdbtnSattelite.setForeground(defaultColorForThings);
		rdbtnSattelite.setFont(defaultFontForThings);
		rdbtnSattelite.setOpaque(false);
		rdbtnSattelite.setBounds(10, 259, 82, 23);
		add(rdbtnSattelite);

		rdbtnDefaultMapOffline = new JRadioButton("Map (offline)");
		rdbtnDefaultMapOffline.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateTileSource(MapSource.OSM_MAP_OFFLINE);
			}
		});
		rdbtnDefaultMapOffline.setForeground(defaultColorForThings);
		rdbtnDefaultMapOffline.setFont(defaultFontForThings);
		rdbtnDefaultMapOffline.setOpaque(false);
		rdbtnDefaultMapOffline.setBounds(10, 233, 124, 23);
		add(rdbtnDefaultMapOffline);

		rdbtnDefaultMap = new JRadioButton("Map");
		rdbtnDefaultMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateTileSource(MapSource.OSM_MAP);
			}
		});
		rdbtnDefaultMap.setForeground(defaultColorForThings);
		rdbtnDefaultMap.setFont(defaultFontForThings);
		rdbtnDefaultMap.setOpaque(false);
		rdbtnDefaultMap.setBounds(10, 207, 52, 23);
		add(rdbtnDefaultMap);

		rdbtnSateliteoffline = new JRadioButton("Satellite (offline)");
		rdbtnSateliteoffline.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateTileSource(MapSource.MAPQUEST_SAT_OFFLINE);
			}
		});
		rdbtnSateliteoffline.setForeground(defaultColorForThings);
		rdbtnSateliteoffline.setFont(defaultFontForThings);
		rdbtnSateliteoffline.setOpaque(false);
		rdbtnSateliteoffline.setBounds(10, 285, 140, 23);
		add(rdbtnSateliteoffline);

		JLabel lblTileSoure = new JLabel("Tile Soure");
		lblTileSoure.setForeground(defaultColorForThings);
		lblTileSoure.setFont(defaultFontForThings);
		lblTileSoure.setBounds(10, 184, 82, 20);
		add(lblTileSoure);
		this.updateTileSource(MapSource.OSM_MAP); // default tiles
		this.points = new ArrayList<GeoCoord>();
	}

	public void changeDrawnRoute(Route newRouteToLoad) {
		this.removeAllMapPolygons();
		this.removeAllMapMarkers();
		this.addNewRouteToMap(newRouteToLoad);
		//empty map just so it can compile, real map will be passed later
	}

	public void addNewCarLocationToMap(LocationReport newLocation) {
		this.removeMapMarker(carCurrentLocation); // not sure if this should be
													// here if we repaint anyway
		Style testStyle = new Style(Color.BLACK, Color.RED, null, this.defaultFontForThings);
		MapMarkerDot newLocationDot = new MapMarkerDot(null, "THE CAR",
				new Coordinate(newLocation.getLocation().getLat(), newLocation.getLocation().getLon()), testStyle);
		this.carCurrentLocation = newLocationDot; // so we can remove it next
													// time.
		this.refreshMap();
	}

	public void addNewRouteToMap(Route newRouteToLoad) {
		this.points = new ArrayList<GeoCoord>(newRouteToLoad.getTrailMarkers());
		List<Coordinate> listForPolygon = new ArrayList<Coordinate>(newRouteToLoad.getTrailMarkers().size());
		// remove the old one
		this.removeMapPolygon(this.routeBreadcrumbs);
		if (this.routePOIs != null) {
			for (MapMarker m : routePOIs) {
				this.removeMapMarker(m);
			}
		}

		for (GeoCoord geo : newRouteToLoad.getTrailMarkers()) {
			listForPolygon.add(new Coordinate(geo.getLat(), geo.getLon()));
		}

		// adding this in to make it a single line, otherwise it draws a line
		// from end to start.
		// There may be a better way of doing this...
		for (int i = newRouteToLoad.getTrailMarkers().size() - 1; i >= 0; i--) {
			GeoCoord toAdd = newRouteToLoad.getTrailMarkers().get(i);
			listForPolygon.add(new Coordinate(toAdd.getLat(), toAdd.getLon()));
		}

		this.routeBreadcrumbs = new MapPolygonImpl(listForPolygon);

		this.routePOIs = new ArrayList<MapMarker>(newRouteToLoad.getPointsOfIntrest().size());
		for (PointOfInterest temp : newRouteToLoad.getPointsOfIntrest()) {
			GeoCoord newSpot = temp.getLocation();
			String name = temp.getName().split(",")[0]; // don't need the whole
														// "city, state,
														// country, continent,
														// earth" name
			routePOIs.add(new MapMarkerDot(name, new Coordinate(newSpot.getLat(), newSpot.getLon())));
		}
		this.refreshMap(); // will paint it on if it's supposed to be there
		this.repaint();

	}

	public void addForecastsToMap(ForecastReport theReport) {
		Style forecastStyle = new Style(Color.black, Color.GREEN, null, this.defaultFontForThings);

		if (this.forecasts != null) {
			for (MapMarker m : forecasts) {
				this.removeMapMarker(m);
			}
		}

		forecasts = new ArrayList<MapMarker>(theReport.getForecasts().size());
		for (int i = 0; i < theReport.getForecasts().size(); i++) {
			ForecastIO fc = theReport.getForecasts().get(i);
			Coordinate location = new Coordinate(fc.getLatitude(), fc.getLongitude());
			String name = new FIODataBlock(fc.getHourly()).icon();
			MapMarkerDot newLocationDot = new MapMarkerDot(null, name, location, forecastStyle);
			// newLocationDot.
			forecasts.add(newLocationDot);
		}
		this.refreshMap();
	}

	// *****************************************************BETA******************************************************


	public void addSpeedsToMap(Map<GeoCoord, Map<Integer,Double>> speed_profile) {
		Style forecastStyle = new Style(Color.black, Color.BLUE, null, this.defaultFontForThings);
		//int i = 0;
		int filter_constant = 100;
		double
		filter_distance = 0.5;
		GeoCoord last_marker = null;
		double last_speed = 0;
		
		if (this.speeds != null) {
			for (MapMarker m : this.speeds) {
				this.removeMapMarker(m);
			}
		}
		
		this.speeds = new ArrayList<MapMarker>();
		/*
		for (GeoCoord g : speed_profile.keySet()) {
			if (last_marker == null || g.calculateDistance(last_marker) > filter_distance) {
				Coordinate location = new Coordinate(g.getLat(), g.getLon());
				String speed = speed_profile.get(g).get(1).toString();
				MapMarkerDot newLocationDot = new MapMarkerDot(null, speed , location, forecastStyle);
				this.speeds.add(newLocationDot);
				last_marker = g;
			}
			
		}*/
		
		/*filter by index
		for (int i = 0; i < this.points.size(); i++) { 
			GeoCoord g = points.get(i);
			if (i%filter_constant == 0 || speed_profile.get(g).get(1) != last_speed) {
				Coordinate location = new Coordinate(g.getLat(), g.getLon());
				String speed = speed_profile.get(g).get(1).toString();
				last_speed = speed_profile.get(g).get(1);
				MapMarkerDot newLocationDot = new MapMarkerDot(null, speed, location, forecastStyle);
				this.speeds.add(newLocationDot);
			}
		}*/
		/*filter by distance*//*
		if (points.size() > 0) {
			last_marker = points.get(0);
			Coordinate location = new Coordinate(points.get(0).getLat(), points.get(0).getLon());
			last_speed = speed_profile.get(points.get(0)).get(1);
			String speed = Double.toString(last_speed);
			MapMarkerDot newLocationDot = new MapMarkerDot(null, speed, location, forecastStyle);
			this.speeds.add(newLocationDot);
			
			for (int i = 1; i < this.points.size(); i++) {
				GeoCoord g = points.get(i);
				if (g.calculateDistance(last_marker) > filter_distance || speed_profile.get(g).get(1) != last_speed) {
					last_marker = new GeoCoord(g.getLat(), g.getLon(), g.getElevation());
					location = new Coordinate(g.getLat(), g.getLon());
					speed = speed_profile.get(g).get(1).toString();
					last_speed = speed_profile.get(g).get(1);
					newLocationDot = new MapMarkerDot(null, speed, location, forecastStyle);
					this.speeds.add(newLocationDot);
				}
			}
		}*/
		
		
		/*filter by distance*/
		int i = 0;
		
		for (GeoCoord g : speed_profile.keySet()) {
			//System.out.println(g.toString());
			if (i%filter_constant == 0 || speed_profile.get(g).get(1) != last_speed) {
				Coordinate location = new Coordinate(g.getLat(), g.getLon());
				String speed = speed_profile.get(g).get(1).toString();
				last_speed = speed_profile.get(g).get(1);
				MapMarkerDot newLocationDot = new MapMarkerDot(null, speed, location, forecastStyle);
				this.speeds.add(newLocationDot);
			}
			i++;
		}
		this.refreshMap();
	}
	// **************************************************************************************************************

	public void refreshMap() {
		/*
		 * there's two ways to do this; one is compare each object and try to
		 * add or remove it according to it's true/false value. The other way is
		 * to remove all of them and re-add everything that's supposed to be
		 * there. The second choice is more computationally expensive, but less
		 * risk for bugs (can't forget about anything). Should performance
		 * become an issue, this is a good method to optimize.
		 */
		this.removeAllMapMarkers();
		this.removeAllMapPolygons();
		this.removeAllMapRectangles();

		if (this.showRouteBreadcrumbs && routeBreadcrumbs != null) {
			this.addMapPolygon(routeBreadcrumbs);
		}

		if (this.showPOIs && routePOIs != null) {
			for (MapMarker m : routePOIs) {
				this.addMapMarker(m);
			}
		}

		if (this.showSpeeds && speeds != null) {
			for (MapMarker m : speeds) {
				this.addMapMarker(m);
			}
		}

		if (this.showForecasts && forecasts != null) {
			for (MapMarker m : forecasts) {
				this.addMapMarker(m);
			}
		}

		if (this.showCarLocation && carCurrentLocation != null) {
			this.addMapMarker(carCurrentLocation);
		}
	}

	private void deselectAllComboBoxes() {
		this.rdbtnDefaultMapOffline.setSelected(false);
		this.rdbtnDefaultMap.setSelected(false);
		this.rdbtnSateliteoffline.setSelected(false);
		this.rdbtnSattelite.setSelected(false);
	}

	private void updateTileSource(MapSource newSource) {
		switch (newSource) {
		case OSM_MAP:
			this.deselectAllComboBoxes();
			this.rdbtnDefaultMap.setSelected(true);
			SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(),
					"Tile source changed to standard map tiles");
			this.setTileSource(new OsmTileSource.Mapnik());
			this.getTileCache().clear();
			break;
		case OSM_MAP_OFFLINE:
			this.deselectAllComboBoxes();
			rdbtnDefaultMapOffline.setSelected(true);
			SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(),
					"Tile Source switched to offline standard map tiles");
			this.setTileSource(
					new OfflineOsmTileSource("File:///" + GlobalValues.DEFAULT_TILE_SAVE_LOCATION + "mapnik/", 1, 19));
			this.getTileCache().clear();
			break;
		case MAPQUEST_SAT:
			this.deselectAllComboBoxes();
			this.rdbtnSattelite.setSelected(true);
			SolarLog.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(),
					"Tile source changed to BING Ariel tiles");
			this.setTileSource(new BingAerialTileSource());
			this.getTileCache().clear();
			break;
		case MAPQUEST_SAT_OFFLINE:
			this.deselectAllComboBoxes();
			this.rdbtnSateliteoffline.setSelected(true);
			this.setTileSource(new OfflineOsmTileSource(
					"File:///" + GlobalValues.DEFAULT_TILE_SAVE_LOCATION + "Bing Aerial Maps/", 1, 19));
			this.getTileCache().clear();
			break;
		}
	}

	private static class saveToDiskCache extends MemoryTileCache {
		@Override
		public Tile getTile(TileSource source, int x, int y, int z) {
			Tile justGotten = super.getTile(source, x, y, z);
			if (justGotten == null) {
				return null;
			}
			if (justGotten.getSource().getName().equalsIgnoreCase("offline")) {
				return justGotten;
			}
			String placeToSave = justGotten.getSource().getName() + "/";
			placeToSave += justGotten.getZoom() + "/";
			placeToSave += justGotten.getXtile() + "/";
			// placeToSave += tile.getYtile() + "/"; //this is the tile name I
			// think
			String totalFilePath = GlobalValues.DEFAULT_TILE_SAVE_LOCATION + placeToSave;
			File saveSpot = new File(totalFilePath);
			if (!saveSpot.exists()) {
				saveSpot.mkdirs();
			}
			String filename = totalFilePath + justGotten.getYtile() + ".png";
			File outputfile = new File(filename);
			if (!outputfile.exists() && justGotten.isLoaded()) {
				try {
					// retrieve image
					BufferedImage bi = justGotten.getImage();
					ImageIO.write(bi, "png", outputfile);
				} catch (IOException e) {
					SolarLog.write(LogType.ERROR, System.currentTimeMillis(),
							"Unable to save tile image, IOException thrown");
				}
			}
			return justGotten;
		}
	}

	private enum MapSource {
		OSM_MAP, OSM_MAP_OFFLINE, MAPQUEST_SAT, MAPQUEST_SAT_OFFLINE
	}
}
