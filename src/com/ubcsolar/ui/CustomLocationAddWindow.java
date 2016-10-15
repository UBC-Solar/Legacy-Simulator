package com.ubcsolar.ui;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.Main.GlobalValues;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.LocationReport;

import java.awt.GridLayout;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridBagConstraints;
import java.awt.Component;
import javax.swing.Box;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import javax.swing.JDialog;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CustomLocationAddWindow extends JFrame {
	private JTextField txtCarName;
	private JTextField txtSource;
	private JTextField txtLatitude;
	private JTextField txtLongitude;
	private JTextField timeField;
	private GlobalController mySession;
	private DateFormat standardTimeFormat = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
	private JSpinner spinElevation;

	public CustomLocationAddWindow(GlobalController mySession) throws HeadlessException {
		setResizable(true);
		this.mySession = mySession;
		setTitleAndLogo();
		this.setBounds(500, 250, 400, 225);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 83, 0, 70, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JLabel lblCarName = new JLabel("Car Name:");
		GridBagConstraints gbc_lblCarName = new GridBagConstraints();
		gbc_lblCarName.anchor = GridBagConstraints.EAST;
		gbc_lblCarName.insets = new Insets(0, 0, 5, 5);
		gbc_lblCarName.gridx = 1;
		gbc_lblCarName.gridy = 0;
		getContentPane().add(lblCarName, gbc_lblCarName);
		
		txtCarName = new JTextField();
		txtCarName.setHorizontalAlignment(SwingConstants.CENTER);
		txtCarName.setText("Raven");
		GridBagConstraints gbc_txtCarName = new GridBagConstraints();
		gbc_txtCarName.insets = new Insets(0, 0, 5, 5);
		gbc_txtCarName.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtCarName.gridx = 2;
		gbc_txtCarName.gridy = 0;
		getContentPane().add(txtCarName, gbc_txtCarName);
		txtCarName.setColumns(10);
		
		JLabel lblSource = new JLabel("Source:");
		GridBagConstraints gbc_lblSource = new GridBagConstraints();
		gbc_lblSource.anchor = GridBagConstraints.EAST;
		gbc_lblSource.insets = new Insets(0, 0, 5, 5);
		gbc_lblSource.gridx = 1;
		gbc_lblSource.gridy = 1;
		getContentPane().add(lblSource, gbc_lblSource);
		
		txtSource = new JTextField();
		txtSource.setText("Debug");
		txtSource.setHorizontalAlignment(SwingConstants.CENTER);
		txtSource.setColumns(10);
		GridBagConstraints gbc_txtSource = new GridBagConstraints();
		gbc_txtSource.insets = new Insets(0, 0, 5, 5);
		gbc_txtSource.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtSource.gridx = 2;
		gbc_txtSource.gridy = 1;
		getContentPane().add(txtSource, gbc_txtSource);
		
		JLabel lblLatitude = new JLabel("Latitude:");
		GridBagConstraints gbc_lblLatitude = new GridBagConstraints();
		gbc_lblLatitude.anchor = GridBagConstraints.EAST;
		gbc_lblLatitude.insets = new Insets(0, 0, 5, 5);
		gbc_lblLatitude.gridx = 1;
		gbc_lblLatitude.gridy = 2;
		getContentPane().add(lblLatitude, gbc_lblLatitude);
		
		txtLatitude = new JTextField();
		txtLatitude.setText("49.2496600");
		txtLatitude.setHorizontalAlignment(SwingConstants.CENTER);
		txtLatitude.setColumns(10);
		GridBagConstraints gbc_txtLatitude = new GridBagConstraints();
		gbc_txtLatitude.insets = new Insets(0, 0, 5, 5);
		gbc_txtLatitude.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtLatitude.gridx = 2;
		gbc_txtLatitude.gridy = 2;
		getContentPane().add(txtLatitude, gbc_txtLatitude);
		
		JLabel lblLongitude = new JLabel("Longitude:");
		GridBagConstraints gbc_lblLongitude = new GridBagConstraints();
		gbc_lblLongitude.anchor = GridBagConstraints.EAST;
		gbc_lblLongitude.insets = new Insets(0, 0, 5, 5);
		gbc_lblLongitude.gridx = 1;
		gbc_lblLongitude.gridy = 3;
		getContentPane().add(lblLongitude, gbc_lblLongitude);
		
		txtLongitude = new JTextField();
		txtLongitude.setText("-123.1193400");
		txtLongitude.setHorizontalAlignment(SwingConstants.CENTER);
		txtLongitude.setColumns(10);
		GridBagConstraints gbc_txtLongitude = new GridBagConstraints();
		gbc_txtLongitude.insets = new Insets(0, 0, 5, 5);
		gbc_txtLongitude.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtLongitude.gridx = 2;
		gbc_txtLongitude.gridy = 3;
		getContentPane().add(txtLongitude, gbc_txtLongitude);
		
		JLabel lblElevationm = new JLabel("Elevation (m):");
		GridBagConstraints gbc_lblElevationm = new GridBagConstraints();
		gbc_lblElevationm.anchor = GridBagConstraints.EAST;
		gbc_lblElevationm.insets = new Insets(0, 0, 5, 5);
		gbc_lblElevationm.gridx = 1;
		gbc_lblElevationm.gridy = 4;
		getContentPane().add(lblElevationm, gbc_lblElevationm);
		
		spinElevation = new JSpinner();
		spinElevation.setModel(new SpinnerNumberModel(new Integer(100), null, null, new Integer(1)));
		GridBagConstraints gbc_spinElevation = new GridBagConstraints();
		gbc_spinElevation.anchor = GridBagConstraints.WEST;
		gbc_spinElevation.insets = new Insets(0, 0, 5, 5);
		gbc_spinElevation.gridx = 2;
		gbc_spinElevation.gridy = 4;
		getContentPane().add(spinElevation, gbc_spinElevation);
		
		JLabel lblTime = new JLabel("Time:");
		GridBagConstraints gbc_lblTime = new GridBagConstraints();
		gbc_lblTime.anchor = GridBagConstraints.EAST;
		gbc_lblTime.insets = new Insets(0, 0, 5, 5);
		gbc_lblTime.gridx = 1;
		gbc_lblTime.gridy = 5;
		getContentPane().add(lblTime, gbc_lblTime);
		
		timeField = new JTextField();
		timeField.setText(this.standardTimeFormat.format(System.currentTimeMillis()));
		timeField.setHorizontalAlignment(SwingConstants.CENTER);
		timeField.setColumns(10);
		GridBagConstraints gbc_timeField = new GridBagConstraints();
		gbc_timeField.insets = new Insets(0, 0, 5, 5);
		gbc_timeField.fill = GridBagConstraints.HORIZONTAL;
		gbc_timeField.gridx = 2;
		gbc_timeField.gridy = 5;
		getContentPane().add(timeField, gbc_timeField);
		
		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handleOkClick();
			}
		});
		btnOk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				handleOkClick();
			}
		});
		GridBagConstraints gbc_btnOk = new GridBagConstraints();
		gbc_btnOk.anchor = GridBagConstraints.EAST;
		gbc_btnOk.insets = new Insets(0, 0, 0, 5);
		gbc_btnOk.gridx = 2;
		gbc_btnOk.gridy = 6;
		getContentPane().add(btnOk, gbc_btnOk);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeWindow();
			}
		});
		btnCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				closeWindow();
			}
		});
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.anchor = GridBagConstraints.WEST;
		gbc_btnCancel.gridx = 3;
		gbc_btnCancel.gridy = 6;
		getContentPane().add(btnCancel, gbc_btnCancel);
		
		btnOk.requestFocusInWindow();
		getRootPane().setDefaultButton(btnOk);
		btnOk.requestFocus();
	}
	
	private void handleError(String message){
		JOptionPane.showMessageDialog(this, message);
	}
	
	private void handleOkClick(){
		double latitude;
		try{
		latitude = Double.parseDouble(this.txtLatitude.getText());
		}
		catch(java.lang.NumberFormatException e){
			this.handleError("Latitude formatted incorrectly");
			return;
		}
		double longitude;
		try{
			longitude = Double.parseDouble(this.txtLongitude.getText());
			}
		catch(java.lang.NumberFormatException e){
			this.handleError("Longitude formatted incorrectly");
			return;
		}
		double elevation;
		try{
			elevation = Double.parseDouble(this.spinElevation.getValue() + "");
		}catch(java.lang.NumberFormatException e){
			this.handleError("Elevation formatted incorrectly");
			return;
		}
		
		GeoCoord temp;
		try{
			temp = new GeoCoord(latitude, longitude, elevation);
			
			}
		catch(java.lang.IllegalArgumentException a){
			this.handleError(a.getMessage());
			return;
		}
		double time;
		try {
			time = this.standardTimeFormat.parse(this.timeField.getText()).getTime();
		} catch (ParseException e) {
			this.handleError("time formatted incorrectly");
			return;
		}
		String carName = this.txtCarName.getText();
		String carNameNoSpaces = carName.replaceAll("\\s", "");
		if(carNameNoSpaces.equals("")){
			this.handleError("invalid car name");
			return;
		}
		String sourceName = this.txtSource.getText();
		String sourceNameNoSpaces = sourceName.replaceAll("\\s", "");
		if(sourceNameNoSpaces.equals("")){
			this.handleError("invalid source name");
			return;
		}
		LocationReport toSend = new LocationReport(temp, carName, sourceName, time);
		mySession.getMapController().recordNewCarLocation(toSend);
		
		this.closeWindow();
	}
	
	private void closeWindow(){
		this.dispose();
	}
	private void setTitleAndLogo() {
		this.setIconImage(GlobalValues.iconImage.getImage()); //centrally stored image for easy update (SPOC!)
		this.setTitle("Custom Location Report");
	}

}
