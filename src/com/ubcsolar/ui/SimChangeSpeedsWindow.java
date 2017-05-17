package com.ubcsolar.ui;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.Route;
import com.ubcsolar.common.SimulationReport;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class SimChangeSpeedsWindow extends JFrame {

    private GlobalController mySession;
    private SimulationAdvancedWindow myParent;
    private SimulationReport lastSimReport;
    private Route currentRoute;
    private JPanel contentPane; //the root content holder

    public SimChangeSpeedsWindow(GlobalController mySession, SimulationAdvancedWindow parent,
                                 SimulationReport lastSimReport, Route currentRoute) {
        this.myParent = parent;// TODO for loading Frame
        this.mySession = mySession;
        this.lastSimReport = lastSimReport;
        this.currentRoute = currentRoute;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 974, 780);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(4, 4, 4, 4));
        setContentPane(contentPane);
        GridBagLayout gbl_contentPane = new GridBagLayout();
        gbl_contentPane.columnWidths = new int[]{0, 0, 0};
        gbl_contentPane.rowHeights = new int[]{30, 0, 0, 0, 0, 0};
        gbl_contentPane.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
        gbl_contentPane.rowWeights = new double[]{0.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
        contentPane.setLayout(gbl_contentPane);
        
        JLabel lblTitle = new JLabel("Title");
        GridBagConstraints gbc_lblTitle = new GridBagConstraints();
        gbc_lblTitle.insets = new Insets(0, 0, 5, 5);
        gbc_lblTitle.gridx = 0;
        gbc_lblTitle.gridy = 0;
        contentPane.add(lblTitle, gbc_lblTitle);
        
        JLabel lblDistance = new JLabel("1/5 distance");
        GridBagConstraints gbc_lblDistance = new GridBagConstraints();
        gbc_lblDistance.insets = new Insets(0, 0, 5, 5);
        gbc_lblDistance.gridx = 0;
        gbc_lblDistance.gridy = 1;
        contentPane.add(lblDistance, gbc_lblDistance);
        
        JLabel lblDistance_1 = new JLabel("2/5 distance");
        GridBagConstraints gbc_lblDistance_1 = new GridBagConstraints();
        gbc_lblDistance_1.insets = new Insets(0, 0, 5, 5);
        gbc_lblDistance_1.gridx = 0;
        gbc_lblDistance_1.gridy = 2;
        contentPane.add(lblDistance_1, gbc_lblDistance_1);
        
        JLabel lblDistance_2 = new JLabel("3/5 distance");
        GridBagConstraints gbc_lblDistance_2 = new GridBagConstraints();
        gbc_lblDistance_2.insets = new Insets(0, 0, 5, 5);
        gbc_lblDistance_2.gridx = 0;
        gbc_lblDistance_2.gridy = 3;
        contentPane.add(lblDistance_2, gbc_lblDistance_2);
        
        JButton btnAddNewDistance = new JButton("add new distance");
        GridBagConstraints gbc_btnAddNewDistance = new GridBagConstraints();
        gbc_btnAddNewDistance.insets = new Insets(0, 0, 0, 5);
        gbc_btnAddNewDistance.gridx = 0;
        gbc_btnAddNewDistance.gridy = 4;
        contentPane.add(btnAddNewDistance, gbc_btnAddNewDistance);

        ArrayList<GeoCoord> importantPoints = new ArrayList<GeoCoord>();
        generateDistanceMarkers(importantPoints);
    }
    
    private void generateDistanceMarkers(ArrayList<GeoCoord> importantPoints) {
    	ArrayList<JLabel> lblDistances = new ArrayList<JLabel>();
    	ArrayList<GridBagConstraints> gbc_lblDistances = new ArrayList<GridBagConstraints>();
    	Map<GeoCoord, Map<Integer, Double>> speedReport = lastSimReport.getManuallyRequestedSpeeds();
    	ArrayList<GeoCoord> points = currentRoute.getTrailMarkers();

    	importantPoints.add(points.get(0));
    	importantPoints.add(points.get(points.size() / 4));
    	importantPoints.add(points.get(points.size() / 2));
    	importantPoints.add(points.get(points.size() * 3 / 4));
    	importantPoints.add(points.get(points.size() - 1));
    }
}
