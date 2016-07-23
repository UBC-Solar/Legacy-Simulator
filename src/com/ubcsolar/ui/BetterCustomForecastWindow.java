package com.ubcsolar.ui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.eclipsesource.json.JsonObject;
import com.github.dvdme.ForecastIOLib.FIODataPoint;
import com.github.dvdme.ForecastIOLib.ForecastIO;
import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.Main.GlobalValues;
import com.ubcsolar.map.MapController;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;

public class BetterCustomForecastWindow extends JFrame{
	
	private GlobalController mySession;
	private JSpinner distanceSpinner;
	private MapController myMap;
	private JList<JsonObject> forecastList;
	private DefaultListModel<JsonObject> listModel;
	
	public BetterCustomForecastWindow(GlobalController mySession) {
		
		this.mySession = mySession;
		this.myMap = mySession.getMapController();
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
		
		JButton btnCopy = new JButton("Copy");
		GridBagConstraints gbc_btnCopy = new GridBagConstraints();
		gbc_btnCopy.insets = new Insets(0, 0, 5, 0);
		gbc_btnCopy.gridx = 2;
		gbc_btnCopy.gridy = 1;
		getContentPane().add(btnCopy, gbc_btnCopy);
		
		
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
		
		JButton btnFinish = new JButton("Finish");
		GridBagConstraints gbc_btnFinish = new GridBagConstraints();
		gbc_btnFinish.gridx = 2;
		gbc_btnFinish.gridy = 2;
		getContentPane().add(btnFinish, gbc_btnFinish);
	}
	
	private void handleAddClick(){
		JFrame frame = new FakeForecastAddWindow(mySession, listModel);
		frame.setVisible(true);
	}
	
	private void setTitleAndLogo() {
		this.setIconImage(GlobalValues.iconImage.getImage()); //centrally stored image for easy update (SPOC!)
		this.setTitle("Custom Forecast Report");
	}

}
