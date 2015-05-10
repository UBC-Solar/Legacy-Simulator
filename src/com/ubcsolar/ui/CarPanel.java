/**
 * The Car subpanel on the main UI
 */
package com.ubcsolar.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;

import com.ubcsolar.common.Listener;
import com.ubcsolar.notification.CarUpdateNotification;
import com.ubcsolar.notification.Notification;

public class CarPanel extends JPanel implements Listener {
	private GlobalController mySession;
	private JLabel lblCarspeed;
	private JLabel lblCar;
	private GUImain parent;
	
	
	public CarPanel(GlobalController session, GUImain parent) {
		mySession = session;
		this.parent = parent;
		setLayout(new GridBagLayout());
		
		lblCar = new JLabel("Car");
		GridBagConstraints carConstraints = new GridBagConstraints();
		carConstraints.gridy = 0;
		carConstraints.weighty = 1;
		carConstraints.anchor = GridBagConstraints.NORTH;
		add(lblCar, carConstraints);
		
		lblCarspeed = new JLabel("No speed yet");
		GridBagConstraints speedConstraints = new GridBagConstraints();
		speedConstraints.gridy = 1;
		speedConstraints.weighty = 0.7;
		speedConstraints.fill = GridBagConstraints.HORIZONTAL;
		add(lblCarspeed, speedConstraints);
		
		JButton btnSettings = new JButton("Settings");
		GridBagConstraints settingsConstraints = new GridBagConstraints();
		settingsConstraints.gridy = 2;
		settingsConstraints.weighty = 0.7;
		add(btnSettings, settingsConstraints);
		btnSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchPerformance();
			}
		});
		
		if(mySession != null) { //NullPointerException protection. And now they'll show up in a 
								//WYSIWYG editor. 
		initializeValues();
		register();
		}
	}
	

	private void initializeValues() {
		updateCarSpeedLabel(mySession.getMyCarController().getLastReportedSpeed());
		
	}


	/**
	 * Asks the parent to launch the Performance window. 
	 */
	public void launchPerformance() {
		parent.launchPerformance();
	}
	
	
	/*
	public void initialize(){
		JLabel lblCar = new JLabel("Car");
		lblCar.setHorizontalAlignment(SwingConstants.CENTER);
		super.add(lblCar);
	}*/
	
	
	/**
	 * updates the car Speed label with the right message
	 * @param newSpeed - the new speed of the car
	 */
	private void updateCarSpeedLabel(int speed){
		this.lblCarspeed.setText("Car Speed: " + speed + " km/h");
	}
	
	
	@Override
	public void notify(Notification n) {
		if(n.getClass() == CarUpdateNotification.class) {
			updateCarSpeedLabel(((CarUpdateNotification) n).getNewCarSpeed()); //for the CarSpeed label.
		}
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void register() {
		mySession.register(this,  CarUpdateNotification.class);
		// TODO Auto-generated method stub
		
	}


}
