package com.ubcsolar.ui;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JLabel;

import com.ubcsolar.Main.GlobalController;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextField;
import javax.swing.JButton;

public class FakeForecastAddWindow extends JFrame{
	
	private GlobalController mySession;
	private JTextField txtLatitude;
	private JTextField txtLongitude;
	private JTextField txtTemp;
	private JTextField txtCloudCover;
	private JTextField txtDewPoint;
	private JTextField txtHumidity;
	private JTextField txtNearestStormBearing;
	private JTextField txtNearestStormDistance;
	private JTextField txtWindBearing;
	private JTextField txtWindSpeed;
	private JTextField txtPrecipProb;
	private JTextField txtPrecipType;
	
	public FakeForecastAddWindow(GlobalController mySession) {
		
		this.mySession = mySession;
		this.setBounds(500, 250, 400, 400);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {30, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JLabel lblLatitude = new JLabel("Latitude:");
		GridBagConstraints gbc_lblLatitude = new GridBagConstraints();
		gbc_lblLatitude.anchor = GridBagConstraints.EAST;
		gbc_lblLatitude.insets = new Insets(0, 0, 5, 5);
		gbc_lblLatitude.gridx = 0;
		gbc_lblLatitude.gridy = 0;
		getContentPane().add(lblLatitude, gbc_lblLatitude);
		
		txtLatitude = new JTextField();
		txtLatitude.setText("49.2496600");
		GridBagConstraints gbc_txtLatitude = new GridBagConstraints();
		gbc_txtLatitude.insets = new Insets(0, 0, 5, 5);
		gbc_txtLatitude.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtLatitude.gridx = 1;
		gbc_txtLatitude.gridy = 0;
		getContentPane().add(txtLatitude, gbc_txtLatitude);
		txtLatitude.setColumns(10);
		
		JLabel lblLongitude = new JLabel("Longitude:");
		GridBagConstraints gbc_lblLongitude = new GridBagConstraints();
		gbc_lblLongitude.anchor = GridBagConstraints.EAST;
		gbc_lblLongitude.insets = new Insets(0, 0, 5, 5);
		gbc_lblLongitude.gridx = 0;
		gbc_lblLongitude.gridy = 1;
		getContentPane().add(lblLongitude, gbc_lblLongitude);
		
		txtLongitude = new JTextField();
		txtLongitude.setText("-123.1193400");
		GridBagConstraints gbc_txtLongitude = new GridBagConstraints();
		gbc_txtLongitude.anchor = GridBagConstraints.NORTH;
		gbc_txtLongitude.insets = new Insets(0, 0, 5, 5);
		gbc_txtLongitude.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtLongitude.gridx = 1;
		gbc_txtLongitude.gridy = 1;
		getContentPane().add(txtLongitude, gbc_txtLongitude);
		txtLongitude.setColumns(10);
		
		JLabel lblTemp = new JLabel("Temp. (\u00B0C):");
		GridBagConstraints gbc_lblTemp = new GridBagConstraints();
		gbc_lblTemp.anchor = GridBagConstraints.EAST;
		gbc_lblTemp.insets = new Insets(0, 0, 5, 5);
		gbc_lblTemp.gridx = 0;
		gbc_lblTemp.gridy = 2;
		getContentPane().add(lblTemp, gbc_lblTemp);
		
		txtTemp = new JTextField();
		txtTemp.setText("20");
		GridBagConstraints gbc_txtTemp = new GridBagConstraints();
		gbc_txtTemp.anchor = GridBagConstraints.NORTH;
		gbc_txtTemp.insets = new Insets(0, 0, 5, 5);
		gbc_txtTemp.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtTemp.gridx = 1;
		gbc_txtTemp.gridy = 2;
		getContentPane().add(txtTemp, gbc_txtTemp);
		txtTemp.setColumns(10);
		
		JLabel lblCloudCover = new JLabel("Cloud Cover %:");
		GridBagConstraints gbc_lblCloudCover = new GridBagConstraints();
		gbc_lblCloudCover.anchor = GridBagConstraints.EAST;
		gbc_lblCloudCover.insets = new Insets(0, 0, 5, 5);
		gbc_lblCloudCover.gridx = 0;
		gbc_lblCloudCover.gridy = 3;
		getContentPane().add(lblCloudCover, gbc_lblCloudCover);
		
		txtCloudCover = new JTextField();
		txtCloudCover.setText("50");
		GridBagConstraints gbc_txtCloudCover = new GridBagConstraints();
		gbc_txtCloudCover.insets = new Insets(0, 0, 5, 5);
		gbc_txtCloudCover.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtCloudCover.gridx = 1;
		gbc_txtCloudCover.gridy = 3;
		getContentPane().add(txtCloudCover, gbc_txtCloudCover);
		txtCloudCover.setColumns(10);
		
		JLabel lblDewPoint = new JLabel("Dew Point (\u00B0C):");
		GridBagConstraints gbc_lblDewPoint = new GridBagConstraints();
		gbc_lblDewPoint.anchor = GridBagConstraints.EAST;
		gbc_lblDewPoint.insets = new Insets(0, 0, 5, 5);
		gbc_lblDewPoint.gridx = 0;
		gbc_lblDewPoint.gridy = 4;
		getContentPane().add(lblDewPoint, gbc_lblDewPoint);
		
		txtDewPoint = new JTextField();
		txtDewPoint.setText("22");
		GridBagConstraints gbc_txtDewPoint = new GridBagConstraints();
		gbc_txtDewPoint.insets = new Insets(0, 0, 5, 5);
		gbc_txtDewPoint.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtDewPoint.gridx = 1;
		gbc_txtDewPoint.gridy = 4;
		getContentPane().add(txtDewPoint, gbc_txtDewPoint);
		txtDewPoint.setColumns(10);
		
		JLabel lblHumidity = new JLabel("Humidity %:");
		GridBagConstraints gbc_lblHumidity = new GridBagConstraints();
		gbc_lblHumidity.anchor = GridBagConstraints.EAST;
		gbc_lblHumidity.insets = new Insets(0, 0, 5, 5);
		gbc_lblHumidity.gridx = 0;
		gbc_lblHumidity.gridy = 5;
		getContentPane().add(lblHumidity, gbc_lblHumidity);
		
		txtHumidity = new JTextField();
		txtHumidity.setText("20");
		GridBagConstraints gbc_txtHumidity = new GridBagConstraints();
		gbc_txtHumidity.insets = new Insets(0, 0, 5, 5);
		gbc_txtHumidity.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtHumidity.gridx = 1;
		gbc_txtHumidity.gridy = 5;
		getContentPane().add(txtHumidity, gbc_txtHumidity);
		txtHumidity.setColumns(10);
		
		JLabel lblNearestStormBearing = new JLabel("Nearest Storm Bearing (\u00B0 from N):");
		GridBagConstraints gbc_lblNearestStormBearing = new GridBagConstraints();
		gbc_lblNearestStormBearing.anchor = GridBagConstraints.EAST;
		gbc_lblNearestStormBearing.insets = new Insets(0, 0, 5, 5);
		gbc_lblNearestStormBearing.gridx = 0;
		gbc_lblNearestStormBearing.gridy = 6;
		getContentPane().add(lblNearestStormBearing, gbc_lblNearestStormBearing);
		
		txtNearestStormBearing = new JTextField();
		txtNearestStormBearing.setText("30");
		GridBagConstraints gbc_txtNearestStormBearing = new GridBagConstraints();
		gbc_txtNearestStormBearing.insets = new Insets(0, 0, 5, 5);
		gbc_txtNearestStormBearing.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtNearestStormBearing.gridx = 1;
		gbc_txtNearestStormBearing.gridy = 6;
		getContentPane().add(txtNearestStormBearing, gbc_txtNearestStormBearing);
		txtNearestStormBearing.setColumns(10);
		
		JLabel lblNearestStormDistance = new JLabel("Nearest Storm Distance (km):");
		GridBagConstraints gbc_lblNearestStormDistance = new GridBagConstraints();
		gbc_lblNearestStormDistance.anchor = GridBagConstraints.EAST;
		gbc_lblNearestStormDistance.insets = new Insets(0, 0, 5, 5);
		gbc_lblNearestStormDistance.gridx = 0;
		gbc_lblNearestStormDistance.gridy = 7;
		getContentPane().add(lblNearestStormDistance, gbc_lblNearestStormDistance);
		
		txtNearestStormDistance = new JTextField();
		txtNearestStormDistance.setText("25");
		GridBagConstraints gbc_txtNearestStormDistance = new GridBagConstraints();
		gbc_txtNearestStormDistance.anchor = GridBagConstraints.NORTH;
		gbc_txtNearestStormDistance.insets = new Insets(0, 0, 5, 5);
		gbc_txtNearestStormDistance.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtNearestStormDistance.gridx = 1;
		gbc_txtNearestStormDistance.gridy = 7;
		getContentPane().add(txtNearestStormDistance, gbc_txtNearestStormDistance);
		txtNearestStormDistance.setColumns(10);
		
		JLabel lblWindBearing = new JLabel("Wind Bearing (\u00B0 from N):");
		GridBagConstraints gbc_lblWindBearing = new GridBagConstraints();
		gbc_lblWindBearing.anchor = GridBagConstraints.EAST;
		gbc_lblWindBearing.insets = new Insets(0, 0, 5, 5);
		gbc_lblWindBearing.gridx = 0;
		gbc_lblWindBearing.gridy = 8;
		getContentPane().add(lblWindBearing, gbc_lblWindBearing);
		
		txtWindBearing = new JTextField();
		txtWindBearing.setText("275");
		GridBagConstraints gbc_txtWindBearing = new GridBagConstraints();
		gbc_txtWindBearing.insets = new Insets(0, 0, 5, 5);
		gbc_txtWindBearing.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtWindBearing.gridx = 1;
		gbc_txtWindBearing.gridy = 8;
		getContentPane().add(txtWindBearing, gbc_txtWindBearing);
		txtWindBearing.setColumns(10);
		
		JLabel lblWindSpeed = new JLabel("Wind Speed (km/h):");
		GridBagConstraints gbc_lblWindSpeed = new GridBagConstraints();
		gbc_lblWindSpeed.anchor = GridBagConstraints.EAST;
		gbc_lblWindSpeed.insets = new Insets(0, 0, 5, 5);
		gbc_lblWindSpeed.gridx = 0;
		gbc_lblWindSpeed.gridy = 9;
		getContentPane().add(lblWindSpeed, gbc_lblWindSpeed);
		
		txtWindSpeed = new JTextField();
		txtWindSpeed.setText("10");
		GridBagConstraints gbc_txtWindSpeed = new GridBagConstraints();
		gbc_txtWindSpeed.insets = new Insets(0, 0, 5, 5);
		gbc_txtWindSpeed.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtWindSpeed.gridx = 1;
		gbc_txtWindSpeed.gridy = 9;
		getContentPane().add(txtWindSpeed, gbc_txtWindSpeed);
		txtWindSpeed.setColumns(10);
		
		JLabel lblPrecipitationProbability = new JLabel("Precipitation Probability:");
		GridBagConstraints gbc_lblPrecipitationProbability = new GridBagConstraints();
		gbc_lblPrecipitationProbability.anchor = GridBagConstraints.EAST;
		gbc_lblPrecipitationProbability.insets = new Insets(0, 0, 5, 5);
		gbc_lblPrecipitationProbability.gridx = 0;
		gbc_lblPrecipitationProbability.gridy = 10;
		getContentPane().add(lblPrecipitationProbability, gbc_lblPrecipitationProbability);
		
		txtPrecipProb = new JTextField();
		txtPrecipProb.setText("36");
		GridBagConstraints gbc_txtPrecipProb = new GridBagConstraints();
		gbc_txtPrecipProb.insets = new Insets(0, 0, 5, 5);
		gbc_txtPrecipProb.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPrecipProb.gridx = 1;
		gbc_txtPrecipProb.gridy = 10;
		getContentPane().add(txtPrecipProb, gbc_txtPrecipProb);
		txtPrecipProb.setColumns(10);
		
		JLabel lblPrecipitationType = new JLabel("Precipitation Type:");
		GridBagConstraints gbc_lblPrecipitationType = new GridBagConstraints();
		gbc_lblPrecipitationType.anchor = GridBagConstraints.EAST;
		gbc_lblPrecipitationType.insets = new Insets(0, 0, 5, 5);
		gbc_lblPrecipitationType.gridx = 0;
		gbc_lblPrecipitationType.gridy = 11;
		getContentPane().add(lblPrecipitationType, gbc_lblPrecipitationType);
		
		txtPrecipType = new JTextField();
		txtPrecipType.setText("Rain");
		GridBagConstraints gbc_txtPrecipType = new GridBagConstraints();
		gbc_txtPrecipType.insets = new Insets(0, 0, 5, 5);
		gbc_txtPrecipType.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPrecipType.gridx = 1;
		gbc_txtPrecipType.gridy = 11;
		getContentPane().add(txtPrecipType, gbc_txtPrecipType);
		txtPrecipType.setColumns(10);
		
		JButton btnOk = new JButton("OK");
		GridBagConstraints gbc_btnOk = new GridBagConstraints();
		gbc_btnOk.insets = new Insets(0, 0, 5, 5);
		gbc_btnOk.gridx = 1;
		gbc_btnOk.gridy = 12;
		getContentPane().add(btnOk, gbc_btnOk);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				closeWindow();
			}
		});
		GridBagConstraints gbc_btnCancel = new GridBagConstraints();
		gbc_btnCancel.insets = new Insets(0, 0, 5, 0);
		gbc_btnCancel.gridx = 2;
		gbc_btnCancel.gridy = 12;
		getContentPane().add(btnCancel, gbc_btnCancel);
	}
	
	private void closeWindow(){
		this.dispose();
	}

}
