package com.ubcsolar.ui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.map.MapController;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JList;

public class BetterCustomForecastWindow extends JFrame{
	
	private GlobalController mySession;
	private JSpinner distanceSpinner;
	private MapController myMap;
	
	public BetterCustomForecastWindow(GlobalController mySession) {
		
		this.mySession = mySession;
		this.myMap = mySession.getMapController();
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{0.0};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0};
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
		
		JList forecastList = new JList();
		GridBagConstraints gbc_forecastList = new GridBagConstraints();
		gbc_forecastList.insets = new Insets(0, 0, 0, 5);
		gbc_forecastList.fill = GridBagConstraints.BOTH;
		gbc_forecastList.gridx = 1;
		gbc_forecastList.gridy = 1;
		getContentPane().add(forecastList, gbc_forecastList);
	}

}
