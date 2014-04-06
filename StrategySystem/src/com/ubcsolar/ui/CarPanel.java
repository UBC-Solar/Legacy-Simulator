/**
 * The Car subpanel on the main UI
 */
package com.ubcsolar.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ubcsolar.common.Listener;
import com.ubcsolar.notification.CarUpdateNotification;
import com.ubcsolar.notification.Notification;

import javax.swing.SwingConstants;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import javax.swing.JButton;

public class CarPanel extends JPanel implements Listener{
	private GlobalController mySession;
	private JLabel lblCarspeed;
	private JLabel lblCar;
	private GUImain parent;
	public CarPanel(GlobalController session, GUImain parent){
		mySession = session;
		this.parent = parent;
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		lblCar = new JLabel("Car");
		add(lblCar, "18, 2");
		
		lblCarspeed = new JLabel("No speed yet");
		
		add(lblCarspeed, "2, 6");
		
		JButton btnSettings = new JButton("Settings");
		add(btnSettings, "2, 8");
		btnSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchPerformance();
			}
		});
		

		if(mySession != null){ //NullPointerException protection. And now they'll show up in a 
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
	public void launchPerformance(){
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
		if(n.getClass() == CarUpdateNotification.class){
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
