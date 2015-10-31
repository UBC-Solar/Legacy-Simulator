package com.ubcsolar.ui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.ubcsolar.common.Listener;
import com.ubcsolar.notification.NewMetarReportLoadedNotification;
import com.ubcsolar.notification.NewTafReportLoadedNotification;
import com.ubcsolar.notification.Notification;

public class WeatherPanel extends JPanel implements Listener {
	private GlobalController mySession;
	private JLabel lblWeatherHeader;
	private JLabel lblMetar;
	private JLabel lblTaf;
	private GUImain parent;
	
	
	public WeatherPanel(GlobalController session, GUImain parent){
		setLayout(new GridBagLayout());
		this.parent = parent;

		lblWeatherHeader = new JLabel("Weather");
		GridBagConstraints headerConstraints = new GridBagConstraints();
		headerConstraints.gridy = 0;
		headerConstraints.weighty = 1;
		headerConstraints.anchor = GridBagConstraints.NORTH;
		this.add(lblWeatherHeader, headerConstraints);
		
		lblMetar = new JLabel("Metar: ");
		GridBagConstraints metarConstraints = new GridBagConstraints();
		metarConstraints.gridy = 1;
		metarConstraints.weighty = 0.7;
		metarConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(lblMetar, metarConstraints);
		
		lblTaf = new JLabel("Taf: ");
		GridBagConstraints tafConstraints = new GridBagConstraints();
		tafConstraints.gridy = 2;
		tafConstraints.weighty = 0.7;
		tafConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.add(lblTaf, tafConstraints);
		
		JButton btnAdvanced = new JButton("Advanced");
		GridBagConstraints buttonConstraints = new GridBagConstraints();
		buttonConstraints.gridy = 3;
		buttonConstraints.weighty = 0.7;
		this.add(btnAdvanced, buttonConstraints);
		btnAdvanced.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchWeather();
			}
		});
		
		mySession = session;
		register();
	}
	
	
	protected void launchWeather() {
			parent.launchWeather();
		
		
	}


	@Override
	public void notify(Notification n) {
		if(n.getClass() == NewMetarReportLoadedNotification.class){
			updateMetarLabel("" + n.getTimeCreated());
		}
		else if(n.getClass() == NewTafReportLoadedNotification.class){
			updateTafLabel("" + n.getTimeCreated());
		}
		
		
	}
	private void updateTafLabel(String string) {
		this.lblTaf.setText("Taf: " + string);
		
	}


	private void updateMetarLabel(String status) {
		this.lblMetar.setText("METAR Loaded: " + status);
		
	}


	@Override
	public void register() {
		mySession.register(this, NewMetarReportLoadedNotification.class);
		mySession.register(this, NewTafReportLoadedNotification.class);
	}

}
