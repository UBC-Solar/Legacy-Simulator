package com.ubcsolar.ui;

import java.awt.GridBagLayout;

import javax.swing.JFrame;

import com.eclipsesource.json.JsonObject;
import com.ubcsolar.Main.GlobalValues;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextField;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JSpinner;

public class ChangeHoursWindow extends JFrame{
	private JsonObject original;
	private DefaultListModel<JsonObject> listModel;
	private JSpinner timeSpinner;
	private double currTime;
	private BetterCustomForecastWindow parent;
	
	public ChangeHoursWindow(JsonObject datapoint, DefaultListModel<JsonObject> listModel, 
			double currTime, BetterCustomForecastWindow parent){
		
		this.currTime = currTime;
		this.parent = parent;
		
		original = datapoint;
		this.listModel = listModel;
		this.setBounds(500, 250, 300, 100);
		setTitleAndLogo();
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {30, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JLabel lblTime = new JLabel("Time (num. hours from now):");
		GridBagConstraints gbc_lblTime = new GridBagConstraints();
		gbc_lblTime.anchor = GridBagConstraints.EAST;
		gbc_lblTime.insets = new Insets(0, 0, 5, 5);
		gbc_lblTime.gridx = 0;
		gbc_lblTime.gridy = 0;
		getContentPane().add(lblTime, gbc_lblTime);
		
		timeSpinner = new JSpinner();
		GridBagConstraints gbc_timeSpinner = new GridBagConstraints();
		gbc_timeSpinner.insets = new Insets(0, 0, 5, 0);
		gbc_timeSpinner.gridx = 1;
		gbc_timeSpinner.gridy = 0;
		getContentPane().add(timeSpinner, gbc_timeSpinner);
		
		JButton btnOk = new JButton("OK");
		GridBagConstraints gbc_btnOk = new GridBagConstraints();
		gbc_btnOk.insets = new Insets(0, 0, 0, 5);
		gbc_btnOk.gridx = 0;
		gbc_btnOk.gridy = 1;
		getContentPane().add(btnOk, gbc_btnOk);
		
		btnOk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0){
				handleOkClick();
			}
		});
		
		JButton btnCancel = new JButton("Cancel");
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.gridx = 1;
		gbc_btnCancel.gridy = 1;
		getContentPane().add(btnCancel, gbc_btnCancel);
		
		btnCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0){
				handleCancelClick();
			}
		});
	}
	
	private void setTitleAndLogo() {
		this.setIconImage(GlobalValues.iconImage.getImage()); //centrally stored image for easy update (SPOC!)
		this.setTitle("Copy Forecast Datapoint");
	}
	
	private void handleOkClick(){
		JsonObject newDatapoint = new JsonObject(original);
		int time;
		try{			
			time = (int) ((int)(timeSpinner.getValue())*3600 + currTime);
			time /= 3600;
			time *= 3600;
		}catch(java.lang.NumberFormatException e){
			this.handleError("Time formatted incorrectly");
			return;
		}
		newDatapoint.set("time", time);
		listModel.addElement(newDatapoint);
		parent.eraseDuplicates();
		this.dispose();
	}
	
	private void handleCancelClick(){
		this.dispose();
	}
	
	private void handleError(String message){
		JOptionPane.showMessageDialog(this, message);
	}
}
