package com.ubcsolar.ui;

import javax.swing.JPanel;

import com.ubcsolar.common.Listener;
import com.ubcsolar.notification.NewMetarReportLoadedNotification;
import com.ubcsolar.notification.NewTafReportLoadedNotification;
import com.ubcsolar.notification.Notification;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.BorderLayout;
import java.awt.Panel;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WeatherPanel extends JPanel implements Listener {
	private GlobalController mySession;
	private JLabel lblWeatherHeader;
	private Label lblMetar;
	private Label lblTaf;
	private GUImain parent;
	public WeatherPanel(GlobalController session, GUImain parent){
		setLayout(new BorderLayout(0, 0));
		this.parent = parent;
		Panel panel = new Panel();
		add(panel, BorderLayout.NORTH);
		
		lblWeatherHeader = new JLabel("Weather");
		panel.add(lblWeatherHeader);
		
		Panel panel_1 = new Panel();
		add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(44dlu;default)"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		lblMetar = new Label("Metar: ");
		panel_1.add(lblMetar, "2, 4, default, top");
		
		lblTaf = new Label("Taf: ");
		panel_1.add(lblTaf, "2, 6");
		
		JButton btnAdvanced = new JButton("Advanced");
		panel_1.add(btnAdvanced, "2, 8");
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
			updateMetarLabel("" + n.getTime());
		}
		else if(n.getClass() == NewTafReportLoadedNotification.class){
			updateTafLabel("" + n.getTime());
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
