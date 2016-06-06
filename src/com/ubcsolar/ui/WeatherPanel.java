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
import java.awt.BorderLayout;

public class WeatherPanel extends JPanel implements Listener {
	private GlobalController mySession;
	private JLabel lblWeatherHeader;
	private GUImain parent;
	private JPanel panel;
	private JPanel panel_1;
	private JPanel panel_2;
	private JLabel lblCloud;
	private JLabel lblWind;
	private JLabel lblRain;
	private JLabel CloudPercent;
	private JLabel WindSpeed;
	private JLabel Rainfall;
	
	
	public WeatherPanel(GlobalController session, GUImain parent){
		this.parent = parent;
		setLayout(new BorderLayout(0, 0));
		
		panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		
				lblWeatherHeader = new JLabel("Weather");
				panel.add(lblWeatherHeader);
				
				panel_1 = new JPanel();
				add(panel_1, BorderLayout.SOUTH);
				
				JButton btnAdvanced = new JButton("Advanced");
				panel_1.add(btnAdvanced);
				
				panel_2 = new JPanel();
				add(panel_2, BorderLayout.CENTER);
				GridBagLayout gbl_panel_2 = new GridBagLayout();
				gbl_panel_2.columnWidths = new int[]{0, 0, 0};
				gbl_panel_2.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
				gbl_panel_2.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
				gbl_panel_2.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
				panel_2.setLayout(gbl_panel_2);
				
				lblCloud = new JLabel("Cloud:");
				GridBagConstraints gbc_lblCloud = new GridBagConstraints();
				gbc_lblCloud.insets = new Insets(0, 0, 5, 5);
				gbc_lblCloud.gridx = 0;
				gbc_lblCloud.gridy = 1;
				panel_2.add(lblCloud, gbc_lblCloud);
				
				CloudPercent = new JLabel("NONE");
				GridBagConstraints gbc_CloudPercent = new GridBagConstraints();
				gbc_CloudPercent.insets = new Insets(0, 0, 5, 0);
				gbc_CloudPercent.gridx = 1;
				gbc_CloudPercent.gridy = 1;
				panel_2.add(CloudPercent, gbc_CloudPercent);
				
				lblWind = new JLabel("Wind:");
				GridBagConstraints gbc_lblWind = new GridBagConstraints();
				gbc_lblWind.insets = new Insets(0, 0, 5, 5);
				gbc_lblWind.gridx = 0;
				gbc_lblWind.gridy = 4;
				panel_2.add(lblWind, gbc_lblWind);
				
				WindSpeed = new JLabel("NONE");
				GridBagConstraints gbc_WindSpeed = new GridBagConstraints();
				gbc_WindSpeed.insets = new Insets(0, 0, 5, 0);
				gbc_WindSpeed.gridx = 1;
				gbc_WindSpeed.gridy = 4;
				panel_2.add(WindSpeed, gbc_WindSpeed);
				
				lblRain = new JLabel("Precipitation:");
				GridBagConstraints gbc_lblRain = new GridBagConstraints();
				gbc_lblRain.insets = new Insets(0, 0, 0, 5);
				gbc_lblRain.gridx = 0;
				gbc_lblRain.gridy = 7;
				panel_2.add(lblRain, gbc_lblRain);
				
				Rainfall = new JLabel("NONE");
				GridBagConstraints gbc_Rainfall = new GridBagConstraints();
				gbc_Rainfall.gridx = 1;
				gbc_Rainfall.gridy = 7;
				panel_2.add(Rainfall, gbc_Rainfall);
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
