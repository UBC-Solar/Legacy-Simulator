package com.ubcsolar.ui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;

import com.eclipsesource.json.JsonObject;
import com.github.dvdme.ForecastIOLib.FIODataPoint;
import com.github.dvdme.ForecastIOLib.ForecastIO;
import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.Main.GlobalValues;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.LogType;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.exception.NoLoadedRouteException;
import com.ubcsolar.map.MapController;
import com.ubcsolar.weather.ForecastIOFactory;
import com.ubcsolar.weather.WeatherController;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.DefaultListModel;
import javax.swing.JButton;

public class BetterCustomForecastWindow extends JFrame{
	
	private GlobalController mySession;
	private JSpinner distanceSpinner;
	private MapController myMap;
	private WeatherController myWeather;
	private JList<JsonObject> forecastList;
	private DefaultListModel<JsonObject> listModel;
	private double currTime;
	
	public BetterCustomForecastWindow(GlobalController mySession) {
		
		this.mySession = mySession;
		this.myMap = mySession.getMapController();
		this.myWeather = mySession.getMyWeatherController();
		this.currTime = System.currentTimeMillis()/1000;
		this.setBounds(500, 250, 400, 400);
		setTitleAndLogo();
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0, 0, 0};
		gridBagLayout.rowHeights = new int[] {0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0};
		getContentPane().setLayout(gridBagLayout);
		
		JLabel lblDistanceAlongRoute = new JLabel("Distance along route (km):");
		GridBagConstraints gbc_lblDistanceAlongRoute = new GridBagConstraints();
		gbc_lblDistanceAlongRoute.anchor = GridBagConstraints.EAST;
		gbc_lblDistanceAlongRoute.insets = new Insets(0, 0, 5, 5);
		gbc_lblDistanceAlongRoute.gridx = 1;
		gbc_lblDistanceAlongRoute.gridy = 0;
		getContentPane().add(lblDistanceAlongRoute, gbc_lblDistanceAlongRoute);
		
		distanceSpinner = new JSpinner();
		distanceSpinner.setModel(new SpinnerNumberModel(0, 0, myMap.findTotalDistanceAlongLoadedRoute(), 1));
		GridBagConstraints gbc_distanceSpinner = new GridBagConstraints();
		gbc_distanceSpinner.insets = new Insets(0, 0, 5, 0);
		gbc_distanceSpinner.gridx = 2;
		gbc_distanceSpinner.gridy = 0;
		getContentPane().add(distanceSpinner, gbc_distanceSpinner);
		
		JButton btnAdd = new JButton("Add");
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.insets = new Insets(0, 0, 5, 5);
		gbc_btnAdd.gridx = 0;
		gbc_btnAdd.gridy = 1;
		getContentPane().add(btnAdd, gbc_btnAdd);
		
		btnAdd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				handleAddClick();
			}
		});
		
		JButton btnRemove = new JButton("Remove");
		GridBagConstraints gbc_btnRemove = new GridBagConstraints();
		gbc_btnRemove.insets = new Insets(0, 0, 5, 5);
		gbc_btnRemove.gridx = 1;
		gbc_btnRemove.gridy = 1;
		getContentPane().add(btnRemove, gbc_btnRemove);
		
		btnRemove.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0){
				handleRemoveClick();
			}
		});
		
		JButton btnCopy = new JButton("Copy");
		GridBagConstraints gbc_btnCopy = new GridBagConstraints();
		gbc_btnCopy.insets = new Insets(0, 0, 5, 0);
		gbc_btnCopy.gridx = 2;
		gbc_btnCopy.gridy = 1;
		getContentPane().add(btnCopy, gbc_btnCopy);
		
		btnCopy.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0){
				handleCopyClick();
			}
		});
		
		
		listModel = new DefaultListModel<JsonObject>();
		
		forecastList = new JList<JsonObject>(listModel);
		GridBagConstraints gbc_forecastList = new GridBagConstraints();
		gbc_forecastList.insets = new Insets(0, 0, 0, 5);
		gbc_forecastList.fill = GridBagConstraints.BOTH;
		gbc_forecastList.gridx = 1;
		gbc_forecastList.gridy = 2;
		getContentPane().add(forecastList, gbc_forecastList);
		
		ForecastDatapointRenderer datapointRenderer = new ForecastDatapointRenderer();
		forecastList.setCellRenderer(datapointRenderer);
		forecastList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JButton btnFinish = new JButton("Finish");
		GridBagConstraints gbc_btnFinish = new GridBagConstraints();
		gbc_btnFinish.gridx = 2;
		gbc_btnFinish.gridy = 2;
		getContentPane().add(btnFinish, gbc_btnFinish);
		
		btnFinish.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0){
				handleFinishClick();
			}
		});
	}
	
	private void handleAddClick(){
		JFrame frame = new FakeForecastAddWindow(mySession, listModel, currTime);
		frame.setVisible(true);
	}
	
	private void handleRemoveClick(){
		listModel.remove(forecastList.getSelectedIndex());
	}
	
	private void handleCopyClick(){
		int selectedIndex = forecastList.getSelectedIndex();
		if(selectedIndex == -1){
			return;
		}
		JFrame frame = new ChangeHoursWindow(listModel.getElementAt(selectedIndex), listModel);
		frame.setVisible(true);
	}
	
	private void handleFinishClick() {
		double distance = (double) distanceSpinner.getValue();
		GeoCoord location = findNearestPoint(distance);
		if (listModel.size() > 0) {
			List<JsonObject> datapoints = new ArrayList<JsonObject>();
			for (int i = 0; i < listModel.size(); i++) {
				datapoints.add(listModel.getElementAt(i));
			}
			ForecastIOFactory.addDatapoints(datapoints);
			ForecastIOFactory.changeLocation(location);
			ForecastIO forecast = ForecastIOFactory.build();
			try {
				myWeather.loadCustomForecast(forecast);
			} catch (NoLoadedRouteException e) {
				// TODO Auto-generated catch block
				this.handleError("No Route Loaded, unable to add custom forecast");
				SolarLog.write(LogType.ERROR, System.currentTimeMillis(),
						"Tried to load custom forecast, but no route loaded");
			}
		}
		this.dispose();
	}
	
	private void setTitleAndLogo() {
		this.setIconImage(GlobalValues.iconImage.getImage()); //centrally stored image for easy update (SPOC!)
		this.setTitle("Custom Forecast Report");
	}
	
	private GeoCoord findNearestPoint(double distance){
		double travelDistance = 0.0;
		List<GeoCoord> trailMarkers = mySession.getMapController().getAllPoints().getTrailMarkers();
		int trailMarkerIndex = 1;
		while(travelDistance < distance && trailMarkerIndex < trailMarkers.size()){
			travelDistance += trailMarkers.get(trailMarkerIndex-1).calculateDistance(
					trailMarkers.get(trailMarkerIndex));
			trailMarkerIndex++;
		}
		return trailMarkers.get(trailMarkerIndex-1);
	}
	
	private void handleError(String message){
		JOptionPane.showMessageDialog(this, message);
	}

}
