package com.ubcsolar.ui;

import javax.swing.JPanel;

import com.ubcsolar.common.Listener;
import com.ubcsolar.notification.Notification;

public class WeatherPanel extends JPanel implements Listener {
	private GlobalController mySession;
	public WeatherPanel(GlobalController session){
		mySession = session;
		register();
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
