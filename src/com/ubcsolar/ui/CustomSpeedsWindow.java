package com.ubcsolar.ui;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.Route;
import com.ubcsolar.common.SimulationReport;
import com.ubcsolar.exception.NoLoadedRouteException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomSpeedsWindow extends JFrame {
	private GlobalController mySession;
    private SimChangeSpeedsWindow myParent;
    private JPanel contentPane; //the root content holder
    private SimulationReport lastSimReport;
    private Route currentRoute;
    
    private ArrayList<GeoCoord> enteredPoints = new ArrayList<GeoCoord>();
    private ArrayList<JSpinner> spinnerDistances = new ArrayList<JSpinner>();
    private ArrayList<GridBagConstraints> gbc_spinnerDistances = new ArrayList<GridBagConstraints>();
    private ArrayList<JSpinner> spinnerSpeeds = new ArrayList<JSpinner>();
    private ArrayList<GridBagConstraints> gbc_spinnerSpeeds = new ArrayList<GridBagConstraints>();

    public CustomSpeedsWindow(GlobalController mySession, SimChangeSpeedsWindow parent, SimulationReport lastSimReport, Route currentRoute) {
        this.mySession = mySession;
        this.myParent = parent;
        this.lastSimReport = lastSimReport;
        this.currentRoute = currentRoute;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 974, 780);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[]{0, 0, 0};
        gbl_contentPane.rowHeights = new int[]{30, 0, 0, 0, 0, 0};
        gbl_contentPane.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
        gbl_contentPane.rowWeights = new double[]{0.0, 1.0, 1.0, 1.0, 1.0, 1.0};
        contentPane.setLayout(gbl_contentPane);
        
        JLabel lblDistance = new JLabel("Distance (km)");
        GridBagConstraints gbc_lblDistance = new GridBagConstraints();
        gbc_lblDistance.insets = new Insets(0, 0, 5, 0);
        gbc_lblDistance.gridx = 0;
        gbc_lblDistance.gridy = 0;
        contentPane.add(lblDistance, gbc_lblDistance);
        
        generateDistanceSpinners();
        
        JLabel lblSpeed = new JLabel("Speed (km/h)");
        GridBagConstraints gbc_lblSpeed = new GridBagConstraints();
        gbc_lblSpeed.insets = new Insets(0, 0, 5, 0);
        gbc_lblSpeed.gridx = 1;
        gbc_lblSpeed.gridy = 0;
        contentPane.add(lblSpeed, gbc_lblSpeed);
    }
    
    private void generateDistanceSpinners() {
    	Map<GeoCoord, Map<Integer, Double>> speedReport = lastSimReport.getManuallyRequestedSpeeds();
    	ArrayList<GeoCoord> points = currentRoute.getTrailMarkers();

    	enteredPoints.add(points.get(1));//because the first point always has to have 0 speed
    	enteredPoints.add(points.get(points.size() / 4));
    	enteredPoints.add(points.get(points.size() / 2));
    	enteredPoints.add(points.get(points.size() * 3 / 4));
    	enteredPoints.add(points.get(points.size() - 1));
    	
    	for(int i = 0; i < enteredPoints.size(); i++) {
			GeoCoord currPoint = enteredPoints.get(i);
			
			spinnerDistances.add(i, new JSpinner());
			gbc_spinnerDistances.add(i, new GridBagConstraints());
			gbc_spinnerDistances.get(i).insets = new Insets(0, 0, 5, 5);
	        gbc_spinnerDistances.get(i).gridx = 0;
	        gbc_spinnerDistances.get(i).gridy = i + 1;
	        contentPane.add(spinnerDistances.get(i), gbc_spinnerDistances.get(i));
	        
	        spinnerSpeeds.add(i, new JSpinner());
	        gbc_spinnerSpeeds.add(i, new GridBagConstraints());
	        gbc_spinnerSpeeds.get(i).weightx = 10.0;
	        gbc_spinnerSpeeds.get(i).insets = new Insets(0, 0, 5, 0);
	        gbc_spinnerSpeeds.get(i).gridx = 1;
	        gbc_spinnerSpeeds.get(i).gridy = i + 1;
	        spinnerSpeeds.get(i).setValue((int)speedReport.get(currPoint).get(1).doubleValue());//TODO: change second get to use lapNum instead of hardcoded 1		        
	        contentPane.add(spinnerSpeeds.get(i), gbc_spinnerSpeeds.get(i));
	        
	        spinnerDistances.get(i).addChangeListener(new DistanceSliderListener()); //for real time change to graph
    	}
    }
    
    private class DistanceSliderListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent arg0) {
			getNearestDistances();
			
		}
    }
    
    private void getNearestDistances() {
    	
    }
    
    private void createNewSpeedPortfolio() {
    	
    }
}
