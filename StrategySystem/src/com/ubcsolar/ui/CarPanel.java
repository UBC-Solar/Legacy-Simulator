package com.ubcsolar.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ubcsolar.car.CarUpdateNotification;
import com.ubcsolar.common.Listener;
import com.ubcsolar.common.Notification;

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
		
		lblCarspeed = new JLabel("CarSpeed");
		add(lblCarspeed, "2, 6");
		
		JButton btnSettings = new JButton("Settings");
		add(btnSettings, "2, 8");
		btnSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchPerformance();
			}
		});
		

		//initialize();
		register();
	}
	
	public void launchPerformance(){
		parent.launchPerformance();
	}
	/*
	public void initialize(){
		JLabel lblCar = new JLabel("Car");
		lblCar.setHorizontalAlignment(SwingConstants.CENTER);
		super.add(lblCar);
	}*/
	
	private void updateCarSpeedLabel(int speed){
		this.lblCarspeed.setText("Car Speed: " + speed + " km/h");
	}
	@Override
	public void notify(Notification n) {
		if(n.getClass() == CarUpdateNotification.class){
			updateCarSpeedLabel(((CarUpdateNotification) n).getNewCarSpeed());
		}
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void register() {
		mySession.register(this,  CarUpdateNotification.class);
		// TODO Auto-generated method stub
		
	}


}
