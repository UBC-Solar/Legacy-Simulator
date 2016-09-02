package com.ubcsolar.ui;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;

import com.github.dvdme.ForecastIOLib.FIODataPoint;
import com.github.dvdme.ForecastIOLib.ForecastIO;
import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.Main.GlobalValues;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.LogType;
import com.ubcsolar.common.Route;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.exception.NoLoadedRouteException;
import com.ubcsolar.map.MapController;
import com.ubcsolar.weather.FIODataPointFactory;
import com.ubcsolar.weather.ForecastIOFactory;
import com.ubcsolar.weather.WeatherController;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;

public class FakeForecastAddWindow extends JFrame{
	
	private GlobalController mySession;
	private WeatherController myWeather;
	private MapController myMap;
	private Route theRoute;
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
	private JTextField txtPrecipIntensity;
	private DefaultListModel<JsonObject> listModel;
	private JTextField txtTime;
	private double currTime;
	
	public FakeForecastAddWindow(GlobalController mySession, DefaultListModel<JsonObject> listModel,
			double currTime) {
		
		this.mySession = mySession;
		this.myWeather = mySession.getMyWeatherController();
		this.myMap = mySession.getMapController();
		this.listModel = listModel;
		this.currTime = currTime;
		this.setBounds(500, 250, 415, 400);
		setTitleAndLogo();
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {30, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JLabel lblTime = new JLabel("Time (hours from now):");
		GridBagConstraints gbc_lblTime = new GridBagConstraints();
		gbc_lblTime.insets = new Insets(0, 0, 5, 5);
		gbc_lblTime.anchor = GridBagConstraints.EAST;
		gbc_lblTime.gridx = 0;
		gbc_lblTime.gridy = 1;
		getContentPane().add(lblTime, gbc_lblTime);
		
		txtTime = new JTextField();
		txtTime.setText("0");
		GridBagConstraints gbc_txtTime = new GridBagConstraints();
		gbc_txtTime.insets = new Insets(0, 0, 5, 0);
		gbc_txtTime.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtTime.gridx = 1;
		gbc_txtTime.gridy = 1;
		getContentPane().add(txtTime, gbc_txtTime);
		txtTime.setColumns(10);
		
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
		gbc_txtTemp.insets = new Insets(0, 0, 5, 0);
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
		gbc_txtCloudCover.insets = new Insets(0, 0, 5, 0);
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
		gbc_txtDewPoint.insets = new Insets(0, 0, 5, 0);
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
		gbc_txtHumidity.insets = new Insets(0, 0, 5, 0);
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
		gbc_txtNearestStormBearing.insets = new Insets(0, 0, 5, 0);
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
		gbc_txtNearestStormDistance.insets = new Insets(0, 0, 5, 0);
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
		gbc_txtWindBearing.insets = new Insets(0, 0, 5, 0);
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
		gbc_txtWindSpeed.insets = new Insets(0, 0, 5, 0);
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
		gbc_txtPrecipProb.insets = new Insets(0, 0, 5, 0);
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
		gbc_txtPrecipType.insets = new Insets(0, 0, 5, 0);
		gbc_txtPrecipType.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPrecipType.gridx = 1;
		gbc_txtPrecipType.gridy = 11;
		getContentPane().add(txtPrecipType, gbc_txtPrecipType);
		txtPrecipType.setColumns(10);
		
		JLabel lblPrecipitationIntensity = new JLabel("Precipitation Intensity (mm/h):");
		GridBagConstraints gbc_lblPrecipitationIntensity = new GridBagConstraints();
		gbc_lblPrecipitationIntensity.anchor = GridBagConstraints.EAST;
		gbc_lblPrecipitationIntensity.insets = new Insets(0, 0, 5, 5);
		gbc_lblPrecipitationIntensity.gridx = 0;
		gbc_lblPrecipitationIntensity.gridy = 12;
		getContentPane().add(lblPrecipitationIntensity, gbc_lblPrecipitationIntensity);
		
		txtPrecipIntensity = new JTextField();
		txtPrecipIntensity.setText("0.4");
		GridBagConstraints gbc_txtPrecipIntensity = new GridBagConstraints();
		gbc_txtPrecipIntensity.insets = new Insets(0, 0, 5, 0);
		gbc_txtPrecipIntensity.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtPrecipIntensity.gridx = 1;
		gbc_txtPrecipIntensity.gridy = 12;
		getContentPane().add(txtPrecipIntensity, gbc_txtPrecipIntensity);
		txtPrecipIntensity.setColumns(10);
		
		JButton btnOk = new JButton("OK");
		btnOk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				handleOkClick();
			}
		});
		GridBagConstraints gbc_btnOk = new GridBagConstraints();
		gbc_btnOk.insets = new Insets(0, 0, 5, 5);
		gbc_btnOk.gridx = 0;
		gbc_btnOk.gridy = 13;
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
		gbc_btnCancel.gridx = 1;
		gbc_btnCancel.gridy = 13;
		getContentPane().add(btnCancel, gbc_btnCancel);
	}
	
	/**
	 * creates a ForecastIO with the values entered into the input window. For fields that
	 * aren't included in the window, a garbage value is entered. If one of the fields is formatted
	 * incorrectly, it will spit an error message but won't close the window.
	 */
	private void handleOkClick(){
		boolean addedDatapoint = addDatapoint();
		if(!addedDatapoint){
			return;
		}else{
			//TODO: figure out if this tryCatch is needed?
			/*try {
				myWeather.loadCustomForecast(customForecast);
			} catch (NoLoadedRouteException e) {
				// TODO Auto-generated catch block
				this.handleError("No Route Loaded, unable to add custom forecast");
				SolarLog.write(LogType.ERROR, System.currentTimeMillis(), "Tried to load custom forecast, but no route loaded");
			}*/
			closeWindow();
		}
	}
	
	private void closeWindow(){
		this.dispose();
	}
	
	private void handleError(String message){
		JOptionPane.showMessageDialog(this, message);
	}
	
	private void setTitleAndLogo() {
		this.setIconImage(GlobalValues.iconImage.getImage()); //centrally stored image for easy update (SPOC!)
		this.setTitle("Custom Forecast Report");
	}
	
	/**
	 * Will build a ForecastIO from the information entered into the fields in the window.
	 * Fields that aren't included in the window will be filled with garbage values
	 * If one of the entries is formatted incorrectly, the method will spit an error message
	 * @return true if datapoint was added successfully, false is an error was encountered
	 */
	
	private boolean addDatapoint(){
		int time;
		double hourTime;
		try{
			hourTime = Double.parseDouble(this.txtTime.getText());
			time = (int) (currTime + hourTime*3600);
		}catch(java.lang.NumberFormatException e){
			this.handleError("Time formatted incorrectly");
			return false;
		}
		double temp;
		try{
			temp = Double.parseDouble(this.txtTemp.getText());
			}
		catch(java.lang.NumberFormatException e){
			this.handleError("Temperature formatted incorrectly");
			return false;
		}
		double cldCover;
		try{
			cldCover = Double.parseDouble(this.txtCloudCover.getText()) / 100;
		}
		catch(java.lang.NumberFormatException e){
			this.handleError("Cloud cover % formatted incorrectly");
			return false;
		}
		double dewPoint;
		try{
			dewPoint = Double.parseDouble(this.txtDewPoint.getText());
			}
		catch(java.lang.NumberFormatException e){
			this.handleError("Dew point formatted incorrectly");
			return false;
		}
		double humidity;
		try{
			humidity = Double.parseDouble(this.txtHumidity.getText());
			}
		catch(java.lang.NumberFormatException e){
			this.handleError("Humidity formatted incorrectly");
			return false;
		}
		double strmBearing;
		try{
			strmBearing = Double.parseDouble(this.txtNearestStormBearing.getText());
			}
		catch(java.lang.NumberFormatException e){
			this.handleError("Storm bearing formatted incorrectly");
			return false;
		}
		double strmDistance;
		try{
			strmDistance = Double.parseDouble(this.txtNearestStormDistance.getText());
			}
		catch(java.lang.NumberFormatException e){
			this.handleError("Storm distance formatted incorrectly");
			return false;
		}
		double windBearing;
		try{
			windBearing = Double.parseDouble(this.txtWindBearing.getText());
			}
		catch(java.lang.NumberFormatException e){
			this.handleError("Wind bearing formatted incorrectly");
			return false;
		}
		double windSpeed;
		try{
			windSpeed = Double.parseDouble(this.txtWindSpeed.getText());
		}
		catch(java.lang.NumberFormatException e){
			this.handleError("Wind speed formatted incorrectly");
			return false;
		}
		double precipProb;
		try{
			precipProb = Double.parseDouble(this.txtPrecipProb.getText()) / 100;
			}
		catch(java.lang.NumberFormatException e){
			this.handleError("Precipitation probability formatted incorrectly");
			return false;
		}
		double precipIntensity;
		try{
			precipIntensity = Double.parseDouble(this.txtPrecipIntensity.getText());
		}catch(java.lang.NumberFormatException e){
			this.handleError("Precipitation intensity formatted incorrectly");
			return false;
		}
		String precipType = this.txtPrecipType.getText();
		
		FIODataPointFactory factory = new FIODataPointFactory();
		
		factory.time(time).cloudCover(cldCover).dewPoint(dewPoint).humidity(humidity).
			precipIntensity(precipIntensity).precipProb(precipProb).precipType(precipType).
			temperature(temp).windBearing(windBearing).windSpeed(windSpeed).
			stormBearing(strmBearing).stormDistance(strmDistance).hourTime(hourTime);
		
		listModel.addElement(factory.build());
		
		return true;
	}
	
	
	

}
