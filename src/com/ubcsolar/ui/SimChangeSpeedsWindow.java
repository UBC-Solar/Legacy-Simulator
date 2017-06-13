package com.ubcsolar.ui;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.Route;
import com.ubcsolar.common.SimulationReport;
import com.ubcsolar.exception.NoLoadedRouteException;
import com.ubcsolar.map.MapController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SimChangeSpeedsWindow extends JFrame {

    private GlobalController mySession;
    private SimulationAdvancedWindow myParent;
    private SimulationReport lastSimReport;
    private Route currentRoute;
    private JPanel contentPane; //the root content holder
    private MapController myMapController;
    
	private ArrayList<JLabel> lblDistances = new ArrayList<JLabel>();
	private ArrayList<GridBagConstraints> gbc_lblDistances = new ArrayList<GridBagConstraints>();
	private ArrayList<JSpinner> spinnerSpeeds = new ArrayList<JSpinner>();
	private ArrayList<GridBagConstraints> gbc_spinnerSpeeds = new ArrayList<GridBagConstraints>();
	private ArrayList<GeoCoord> importantPoints = new ArrayList<GeoCoord>();

    public SimChangeSpeedsWindow(GlobalController mySession, SimulationAdvancedWindow parent,
                                 SimulationReport lastSimReport, Route currentRoute) {
	
        this.myParent = parent;
        this.mySession = mySession;
        this.myMapController = mySession.getMapController();
        this.lastSimReport = lastSimReport;
        this.currentRoute = currentRoute;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 974, 780);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(4, 4, 4, 4));
        setContentPane(contentPane);
        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[]{0, 0, 0};
        gbl_contentPane.rowHeights = new int[]{30, 0, 0, 0, 0, 0, 0};
        gbl_contentPane.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
        gbl_contentPane.rowWeights = new double[]{0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0};
        contentPane.setLayout(gbl_contentPane);
        
        JLabel lblTitle = new JLabel("Title");
        GridBagConstraints gbc_lblTitle = new GridBagConstraints();
        gbc_lblTitle.insets = new Insets(0, 0, 5, 5);
        gbc_lblTitle.gridx = 0;
        gbc_lblTitle.gridy = 0;
        contentPane.add(lblTitle, gbc_lblTitle);
        
        generateDistanceMarkers();
        
        JButton btnAddNewDistance = new JButton("add new distance");
        GridBagConstraints gbc_btnAddNewDistance = new GridBagConstraints();
        gbc_btnAddNewDistance.insets = new Insets(0, 0, 0, 5);
        gbc_btnAddNewDistance.gridx = 0;
        gbc_btnAddNewDistance.gridy = importantPoints.size() + 2;
        contentPane.add(btnAddNewDistance, gbc_btnAddNewDistance);
		
		SimChangeSpeedsWindow thisWindow = this;
		
		btnAddNewDistance.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFrame frame = new CustomSpeedsWindow(mySession, thisWindow, lastSimReport, currentRoute);
				frame.setVisible(true);
			}	
		});
        
        JButton btnCreateNewSpeedProfile = new JButton("create new speed profile");
        GridBagConstraints gbc_btnCreateNewSpeedProfile = new GridBagConstraints();
        gbc_btnCreateNewSpeedProfile.insets = new Insets(0, 0, 0, 5);
        gbc_btnCreateNewSpeedProfile.gridx = 1;
        gbc_btnCreateNewSpeedProfile.gridy = importantPoints.size() + 2;
        contentPane.add(btnCreateNewSpeedProfile, gbc_btnCreateNewSpeedProfile);
        
        btnCreateNewSpeedProfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				createNewSpeedProfile();
			}
		});
    }
    
    private void generateDistanceMarkers() {

    
    	Map<GeoCoord, Map<Integer, Double>> speedReport = lastSimReport.getManuallyRequestedSpeeds();
    	ArrayList<GeoCoord> points = currentRoute.getTrailMarkers();

    	importantPoints.add(points.get(1));//because the first point always has to have 0 speed
    	importantPoints.add(points.get(points.size() / 4));
    	importantPoints.add(points.get(points.size() / 2));
    	importantPoints.add(points.get(points.size() * 3 / 4));
    	importantPoints.add(points.get(points.size() - 1));
    	
    	for(int i = 0; i < importantPoints.size(); i++) {
    		try {
    			GeoCoord currPoint = importantPoints.get(i);
    			
				lblDistances.add(i, new JLabel("" + myMapController.findDistanceAlongLoadedRoute(currPoint) + " km"));
				gbc_lblDistances.add(i, new GridBagConstraints());
				gbc_lblDistances.get(i).insets = new Insets(0, 0, 5, 5);
		        gbc_lblDistances.get(i).gridx = 0;
		        gbc_lblDistances.get(i).gridy = i + 2;
		        contentPane.add(lblDistances.get(i), gbc_lblDistances.get(i));
		        
		        spinnerSpeeds.add(i, new JSpinner());
		        gbc_spinnerSpeeds.add(i, new GridBagConstraints());
		        gbc_spinnerSpeeds.get(i).insets = new Insets(0, 0, 5, 0);
		        gbc_spinnerSpeeds.get(i).gridx = 1;
		        gbc_spinnerSpeeds.get(i).gridy = i + 2;
		        spinnerSpeeds.get(i).setValue((int)speedReport.get(currPoint).get(1).doubleValue());//TODO: change second get to use lapNum instead of hardcoded 1
		        
		        contentPane.add(spinnerSpeeds.get(i), gbc_spinnerSpeeds.get(i));
				
			} catch (NoLoadedRouteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    
    private void createNewSpeedProfile() {
    	ArrayList<GeoCoord> allPoints = currentRoute.getTrailMarkers();
    	Map<GeoCoord, Map<Integer, Double>> newSpeedProfile = new LinkedHashMap<GeoCoord, Map<Integer, Double>>();
    	
    	Map<Integer, Double> zeroIndexMap = new LinkedHashMap<Integer,Double>();
    	zeroIndexMap.put(1, 0.0);
    	newSpeedProfile.put(allPoints.get(0), zeroIndexMap);
    	
    	int importantPointIndex = 0;
    	
    	for(int i = 1; i < allPoints.size(); i++) {
    		if(importantPointIndex < importantPoints.size() - 1) {
    			if(allPoints.get(i) == importantPoints.get(importantPointIndex + 1)) {
    				importantPointIndex++;
    			}
    		}
    		
    		Map<Integer, Double> lapSpeeds = new LinkedHashMap<Integer,Double>();
    		double currSpeed = (double) ((Integer)spinnerSpeeds.get(importantPointIndex).getValue()).intValue();
    		lapSpeeds.put(1, currSpeed);
    		newSpeedProfile.put(allPoints.get(i), lapSpeeds);
    		
    	}
    	
    	for(int i = 0; i < allPoints.size(); i++) {
    		System.out.println("speed at point " + i + " is " + newSpeedProfile.get(allPoints.get(i)));
    	}
    	
    	//TODO: do something other than print this speed profile
    	//e.g. => this.parent.runFancyNewSim(newSpeedProfile)
    }
}
