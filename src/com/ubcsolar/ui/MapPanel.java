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

public class MapPanel extends JPanel implements Listener {
	
	private GUImain parent;
	
	public MapPanel(GUImain main) {
	parent = main;
	this.setBorder(BorderFactory.createLineBorder(Color.black));
	
	JLabel lblMap = new JLabel("Map");
	this.add(lblMap);
	
	JButton btnAdvanced = new JButton("Advanced");
	btnAdvanced.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			parent.launchMap();
		}
	});

	this.add(btnAdvanced);
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

