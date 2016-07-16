package com.ubcsolar.ui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import com.github.dvdme.ForecastIOLib.FIODataBlock;
import com.github.dvdme.ForecastIOLib.ForecastIO;
import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.Main.GlobalValues;
import com.ubcsolar.common.ForecastReport;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.Listener;
import com.ubcsolar.common.LocationReport;
import com.ubcsolar.common.LogType;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.common.TelemDataPacket;
import com.ubcsolar.exception.NoForecastReportException;
import com.ubcsolar.map.MapController;
import com.ubcsolar.notification.CarUpdateNotification;
import com.ubcsolar.notification.NewForecastReport;
import com.ubcsolar.notification.NewLocationReportNotification;
import com.ubcsolar.notification.NewMetarReportLoadedNotification;
import com.ubcsolar.notification.NewTafReportLoadedNotification;
import com.ubcsolar.notification.Notification;
import com.ubcsolar.weather.WeatherController;

import java.awt.Insets;
import java.awt.BorderLayout;

public class WeatherPanel extends JPanel implements Listener {
	private GlobalController mySession;
	private JLabel lblWeatherHeader;
	private GUImain parent;
	private JPanel panel;
	private JPanel panel_1;
	private JPanel panel_2;
	private JLabel lblCloud;
	private JLabel lblWind;
	private JLabel lblRain;
	private JLabel CloudPercent;
	private JLabel WindSpeed;
	private JLabel Rainfall;
	private JLabel lblForecast;
	private JLabel ForecastLoaded;
	
	
	public WeatherPanel(GlobalController session, GUImain parent){
		if (session == null || parent == null){
			return;
		}
		this.parent = parent;
		setLayout(new BorderLayout(0, 0));
		
		panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		
				lblWeatherHeader = new JLabel("Weather");
				panel.add(lblWeatherHeader);
				
				panel_1 = new JPanel();
				add(panel_1, BorderLayout.SOUTH);
				
				JButton btnAdvanced = new JButton("Advanced");
				panel_1.add(btnAdvanced);
				
				panel_2 = new JPanel();
				add(panel_2, BorderLayout.CENTER);
				GridBagLayout gbl_panel_2 = new GridBagLayout();
				gbl_panel_2.columnWidths = new int[]{0, 0, 0};
				gbl_panel_2.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
				gbl_panel_2.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
				gbl_panel_2.rowWeights = new double[]{0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
				panel_2.setLayout(gbl_panel_2);
				
				lblCloud = new JLabel("Amount of Cloud Cover:");
				GridBagConstraints gbc_lblCloud = new GridBagConstraints();
				gbc_lblCloud.insets = new Insets(0, 0, 5, 5);
				gbc_lblCloud.gridx = 0;
				gbc_lblCloud.gridy = 1;
				panel_2.add(lblCloud, gbc_lblCloud);
				
				CloudPercent = new JLabel("NONE");
				GridBagConstraints gbc_CloudPercent = new GridBagConstraints();
				gbc_CloudPercent.insets = new Insets(0, 0, 5, 0);
				gbc_CloudPercent.gridx = 1;
				gbc_CloudPercent.gridy = 1;
				panel_2.add(CloudPercent, gbc_CloudPercent);
				
				lblWind = new JLabel("Wind Speed:");
				GridBagConstraints gbc_lblWind = new GridBagConstraints();
				gbc_lblWind.insets = new Insets(0, 0, 5, 5);
				gbc_lblWind.gridx = 0;
				gbc_lblWind.gridy = 3;
				panel_2.add(lblWind, gbc_lblWind);
				
				WindSpeed = new JLabel("NONE");
				GridBagConstraints gbc_WindSpeed = new GridBagConstraints();
				gbc_WindSpeed.insets = new Insets(0, 0, 5, 0);
				gbc_WindSpeed.gridx = 1;
				gbc_WindSpeed.gridy = 3;
				panel_2.add(WindSpeed, gbc_WindSpeed);
				
				lblRain = new JLabel("Precipitation Intensity:");
				GridBagConstraints gbc_lblRain = new GridBagConstraints();
				gbc_lblRain.insets = new Insets(0, 0, 5, 5);
				gbc_lblRain.gridx = 0;
				gbc_lblRain.gridy = 5;
				panel_2.add(lblRain, gbc_lblRain);
				
				Rainfall = new JLabel("NONE");
				GridBagConstraints gbc_Rainfall = new GridBagConstraints();
				gbc_Rainfall.insets = new Insets(0, 0, 5, 0);
				gbc_Rainfall.gridx = 1;
				gbc_Rainfall.gridy = 5;
				panel_2.add(Rainfall, gbc_Rainfall);
				
				lblForecast = new JLabel("Forecast:");
				GridBagConstraints gbc_lblForecast = new GridBagConstraints();
				gbc_lblForecast.insets = new Insets(0, 0, 0, 5);
				gbc_lblForecast.gridx = 0;
				gbc_lblForecast.gridy = 7;
				panel_2.add(lblForecast, gbc_lblForecast);
				
				ForecastLoaded = new JLabel("NONE");
				GridBagConstraints gbc_ForecastLoaded = new GridBagConstraints();
				gbc_ForecastLoaded.gridx = 1;
				gbc_ForecastLoaded.gridy = 7;
				panel_2.add(ForecastLoaded, gbc_ForecastLoaded);
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

	private void updateKeyLabels(LocationReport location){
		GeoCoord actual_location = location.getLocation();
		try {
			ForecastIO temp_forecast = mySession.getMyWeatherController().getForecastForSpecificPoint(actual_location, true);
			FIODataBlock temp_forecast_block = new FIODataBlock(temp_forecast.getHourly());
			Double cloud = temp_forecast_block.datapoint(0).cloudCover();
			double cloud_percent = cloud*100;
			String cloud_percent_word = String.valueOf(cloud_percent);
			Double wind = temp_forecast_block.datapoint(0).windSpeed(); //units m/s?
			Double weather = temp_forecast_block.datapoint(0).precipIntensity();
			String weather_string = String.valueOf(weather);
			
			WindSpeed.setText(wind + " km/h");
			CloudPercent.setText(cloud_percent_word + "%");
			Rainfall.setText(weather_string + " mm/h");
			
		} catch (IOException e) {
			WindSpeed.setText("No Internet");
			CloudPercent.setText("No Internet");
			Rainfall.setText("No Internet");
			SolarLog.write(LogType.ERROR, System.currentTimeMillis(), "IO Error updating FC labels for current pos");
		} catch (NoForecastReportException e) {
			WindSpeed.setText("No Forecast");
			CloudPercent.setText("No Forecast");
			Rainfall.setText("No Forecast");
			SolarLog.write(LogType.ERROR, System.currentTimeMillis(), "No Forecast when updating FC labels for current pos");
		}catch(NullPointerException e){
			WindSpeed.setText("ERROR GETTING");
			CloudPercent.setText("ERROR GETTING");
			Rainfall.setText("ERROR GETTING");
			SolarLog.write(LogType.ERROR, System.currentTimeMillis(), "Null Pointer Exception when updating FC labels for current pos");
			e.printStackTrace();
		}

		
	}
	
	
	private void updateForcastlbl(String string) {
		this.ForecastLoaded.setText(string);
		
	}
	
	

	@Override
	public void notify(Notification n) {
		
		if(n.getClass() == NewForecastReport.class){
			if (mySession.getMapController().getLastReportedLocation() != null){
				updateKeyLabels(mySession.getMapController().getLastReportedLocation());
				
			}
			NewForecastReport test = (NewForecastReport) n;
			if(test.getTheReport().getRouteNameForecastsWereCreatedFor()== null){
				updateForcastlbl("Cleared @ " + GlobalValues.hourMinSec.format(n.getTimeCreated()));
			}
			else{
				updateForcastlbl("Dwnlded @ " + GlobalValues.hourMinSec.format(n.getTimeCreated()));
			}
		}
		else if(n.getClass() == NewLocationReportNotification.class){
			updateKeyLabels(((NewLocationReportNotification) n).getCarLocation());
			
		}
		
		if(n.getClass() == NewMetarReportLoadedNotification.class){
			updateMetarLabel("" + n.getTimeCreated());
		}
		else if(n.getClass() == NewTafReportLoadedNotification.class){
			updateTafLabel("" + n.getTimeCreated());
		}
		
		
	}
	private void updateTafLabel(String string) {
		this.lblCloud.setText("Taf: " + string);
		
	}


	private void updateMetarLabel(String status) {
		this.lblRain.setText("METAR Loaded: " + status);
		
	}


	@Override
	public void register() {
		mySession.register(this, NewMetarReportLoadedNotification.class);
		mySession.register(this, NewTafReportLoadedNotification.class);
		mySession.register(this, NewForecastReport.class);
		mySession.register(this, NewLocationReportNotification.class);
	}

}
