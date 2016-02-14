package com.ubcsolar.ui;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.ubcsolar.Main.GlobalController;

import java.awt.GridLayout;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Component;
import javax.swing.Box;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomLocationAddWindow extends JFrame {
	private JTextField txtRaven;
	private JTextField txtPhonegps;
	private JTextField txtLatitude;
	private JTextField txtLongitude;
	private JTextField textField;
	private GlobalController mySession;

	public CustomLocationAddWindow(GlobalController mySession) throws HeadlessException {
		setResizable(false);
		this.mySession = mySession;
		setTitleAndLogo();
		this.setBounds(500, 250, 400, 225);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 83, 0, 70, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JLabel lblCarName = new JLabel("Car Name:");
		GridBagConstraints gbc_lblCarName = new GridBagConstraints();
		gbc_lblCarName.anchor = GridBagConstraints.EAST;
		gbc_lblCarName.insets = new Insets(0, 0, 5, 5);
		gbc_lblCarName.gridx = 1;
		gbc_lblCarName.gridy = 0;
		getContentPane().add(lblCarName, gbc_lblCarName);
		
		txtRaven = new JTextField();
		txtRaven.setHorizontalAlignment(SwingConstants.CENTER);
		txtRaven.setText("Raven");
		GridBagConstraints gbc_txtRaven = new GridBagConstraints();
		gbc_txtRaven.insets = new Insets(0, 0, 5, 5);
		gbc_txtRaven.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtRaven.gridx = 2;
		gbc_txtRaven.gridy = 0;
		getContentPane().add(txtRaven, gbc_txtRaven);
		txtRaven.setColumns(10);
		
		JLabel lblSource = new JLabel("Source:");
		GridBagConstraints gbc_lblSource = new GridBagConstraints();
		gbc_lblSource.anchor = GridBagConstraints.EAST;
		gbc_lblSource.insets = new Insets(0, 0, 5, 5);
		gbc_lblSource.gridx = 1;
		gbc_lblSource.gridy = 1;
		getContentPane().add(lblSource, gbc_lblSource);
		
		txtPhonegps = new JTextField();
		txtPhonegps.setText("PhoneGPS");
		txtPhonegps.setHorizontalAlignment(SwingConstants.CENTER);
		txtPhonegps.setColumns(10);
		GridBagConstraints gbc_txtPhonegps = new GridBagConstraints();
		gbc_txtPhonegps.insets = new Insets(0, 0, 5, 5);
		gbc_txtPhonegps.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPhonegps.gridx = 2;
		gbc_txtPhonegps.gridy = 1;
		getContentPane().add(txtPhonegps, gbc_txtPhonegps);
		
		JLabel lblLatitude = new JLabel("Latitude:");
		GridBagConstraints gbc_lblLatitude = new GridBagConstraints();
		gbc_lblLatitude.anchor = GridBagConstraints.EAST;
		gbc_lblLatitude.insets = new Insets(0, 0, 5, 5);
		gbc_lblLatitude.gridx = 1;
		gbc_lblLatitude.gridy = 2;
		getContentPane().add(lblLatitude, gbc_lblLatitude);
		
		txtLatitude = new JTextField();
		txtLatitude.setText("Latitude");
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
		txtLongitude.setText("Longitude");
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
		
		JSpinner spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(new Integer(100), null, null, new Integer(1)));
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.anchor = GridBagConstraints.WEST;
		gbc_spinner.insets = new Insets(0, 0, 5, 5);
		gbc_spinner.gridx = 2;
		gbc_spinner.gridy = 4;
		getContentPane().add(spinner, gbc_spinner);
		
		JLabel lblTime = new JLabel("Time:");
		GridBagConstraints gbc_lblTime = new GridBagConstraints();
		gbc_lblTime.anchor = GridBagConstraints.EAST;
		gbc_lblTime.insets = new Insets(0, 0, 5, 5);
		gbc_lblTime.gridx = 1;
		gbc_lblTime.gridy = 5;
		getContentPane().add(lblTime, gbc_lblTime);
		
		textField = new JTextField();
		textField.setText("14:22:10");
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		textField.setColumns(10);
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 2;
		gbc_textField.gridy = 5;
		getContentPane().add(textField, gbc_textField);
		
		JButton btnOk = new JButton("OK");
		btnOk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				System.out.println("AHAHAHAHAH OK clicked");
			}
		});
		GridBagConstraints gbc_btnOk = new GridBagConstraints();
		gbc_btnOk.anchor = GridBagConstraints.EAST;
		gbc_btnOk.insets = new Insets(0, 0, 0, 5);
		gbc_btnOk.gridx = 2;
		gbc_btnOk.gridy = 6;
		getContentPane().add(btnOk, gbc_btnOk);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("cancel clicked");
			}
		});
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.anchor = GridBagConstraints.WEST;
		gbc_btnCancel.gridx = 3;
		gbc_btnCancel.gridy = 6;
		getContentPane().add(btnCancel, gbc_btnCancel);
	}

	private void setTitleAndLogo() {
		this.setIconImage(mySession.iconImage.getImage()); //centrally stored image for easy update (SPOC!)
		this.setTitle("Custom Location Report");
	}

}
