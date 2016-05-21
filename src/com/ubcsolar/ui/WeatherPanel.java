package com.ubcsolar.ui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.common.Listener;
import com.ubcsolar.notification.NewMetarReportLoadedNotification;
import com.ubcsolar.notification.NewTafReportLoadedNotification;
import com.ubcsolar.notification.Notification;
import java.awt.Insets;

public class WeatherPanel extends JPanel implements Listener {
	private GlobalController mySession;
	private JLabel lblWeatherHeader;
	private JLabel lblRain;
	private JLabel lblCloud;
	private GUImain parent;
	private JLabel lblWind;
	
	
	public WeatherPanel(GlobalController session, GUImain parent){
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{125, 0};
		setLayout(gridBagLayout);
		this.parent = parent;

		lblWeatherHeader = new JLabel("Weather");
		GridBagConstraints headerConstraints = new GridBagConstraints();
		headerConstraints.insets = new Insets(0, 0, 5, 0);
		headerConstraints.gridx = 1;
		headerConstraints.gridy = 0;
		headerConstraints.weighty = 1;
		headerConstraints.anchor = GridBagConstraints.NORTH;
		this.add(lblWeatherHeader, headerConstraints);
		
		lblWind = new JLabel("Wind:");
		GridBagConstraints gbc_lblWind = new GridBagConstraints();
		gbc_lblWind.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblWind.insets = new Insets(0, 0, 5, 5);
		gbc_lblWind.gridx = 0;
		gbc_lblWind.gridy = 1;
		add(lblWind, gbc_lblWind);
		
		lblRain = new JLabel("Precipitation: ");
		GridBagConstraints gbc_lblRain = new GridBagConstraints();
		gbc_lblRain.insets = new Insets(0, 0, 5, 5);
		gbc_lblRain.gridx = 0;
		gbc_lblRain.gridy = 2;
		gbc_lblRain.weighty = 0.7;
		gbc_lblRain.fill = GridBagConstraints.HORIZONTAL;
		this.add(lblRain, gbc_lblRain);
		
		lblCloud = new JLabel("Cloud: ");
		GridBagConstraints gbc_lblCloud = new GridBagConstraints();
		gbc_lblCloud.insets = new Insets(0, 0, 5, 5);
		gbc_lblCloud.gridx = 0;
		gbc_lblCloud.gridy = 3;
		gbc_lblCloud.weighty = 0.7;
		gbc_lblCloud.fill = GridBagConstraints.HORIZONTAL;
		this.add(lblCloud, gbc_lblCloud);
		
		JButton btnAdvanced = new JButton("Advanced");
		GridBagConstraints buttonConstraints = new GridBagConstraints();
		buttonConstraints.insets = new Insets(0, 0, 0, 5);
		buttonConstraints.gridx = 0;
		buttonConstraints.gridy = 4;
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
		this.lblCloud.setText("Taf: " + string);
		
	}


	private void updateMetarLabel(String status) {
		this.lblRain.setText("METAR Loaded: " + status);
		
	}


	@Override
	public void register() {
		mySession.register(this, NewMetarReportLoadedNotification.class);
		mySession.register(this, NewTafReportLoadedNotification.class);
	}

}
