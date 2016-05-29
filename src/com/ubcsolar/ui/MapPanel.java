package com.ubcsolar.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ubcsolar.common.Listener;
import com.ubcsolar.common.LogType;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.notification.Notification;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.BorderLayout;

public class MapPanel extends JPanel implements Listener {
	
	private GUImain parent;
	
	public MapPanel(GUImain main) {
	parent = main;
	this.setBorder(BorderFactory.createLineBorder(Color.black));
	setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		
		JLabel label = new JLabel("Map");
		panel.add(label);
		
		JButton button = new JButton("Advanced");
		panel.add(button);
		
		JPanel panel_1 = new JPanel();
		add(panel_1, BorderLayout.CENTER);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		JLabel lblMapLoaded = new JLabel("Map Loaded: ");
		GridBagConstraints gbc_lblMapLoaded = new GridBagConstraints();
		gbc_lblMapLoaded.gridx = 3;
		gbc_lblMapLoaded.gridy = 6;
		panel_1.add(lblMapLoaded, gbc_lblMapLoaded);
	register(); //do last, in case a notification is sent before we're done building.
	
	}
	@Override
	public void notify(Notification n) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void register() {
		// TODO Auto-generated method stub
		
	}
}

