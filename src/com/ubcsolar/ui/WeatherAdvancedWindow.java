package com.ubcsolar.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import com.github.dvdme.ForecastIOLib.FIODataBlock;
import com.github.dvdme.ForecastIOLib.FIODataPoint;
import com.github.dvdme.ForecastIOLib.ForecastIO;
import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.common.DistanceUnit;
import com.ubcsolar.common.ForecastReport;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.Listener;
import com.ubcsolar.common.LocationReport;
import com.ubcsolar.notification.NewForecastReport;
import com.ubcsolar.notification.NewLocationReportNotification;
import com.ubcsolar.notification.NewMapLoadedNotification;
import com.ubcsolar.notification.Notification;

import javax.swing.ImageIcon;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class WeatherAdvancedWindow extends JFrame implements Listener{

	private JPanel contentPane;
	private GlobalController mySession;
	private ChartPanel temperatureChart;
	private JFreeChart temperatureChartJFree;
	private ForecastReport currentForecastReport;
	private GeoCoord startLocation;
	private final String X_AXIS_LABEL = "Travel Distance (km)";
	private final int NUM_LINES = 4;
	private ChartPanel cloudCoverChart;
	private JFreeChart cloudCoverChartJFree;
	private ChartPanel precipitationChart;
	private JFreeChart precipitationChartJFree;
	private ChartPanel windSpeedChart;
	private JFreeChart windSpeedChartJFree;

	/**
	 * Launch the application.
	 *//*//don't need a main here.
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Weather frame = new Weather();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	/**
	 * Create the frame.
	 * @param mySession 
	 */
	public WeatherAdvancedWindow(final GlobalController mySession) {
		setTitle("Advanced Weather");
		this.mySession = mySession;
		register();
		setTitle("Weather");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 489, 358);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnForecasts = new JMenu("Forecasts");
		menuBar.add(mnForecasts);
		
		JMenuItem mntmLoadForecastsFor = new JMenuItem("Load Forecasts for Route (48 hours)");
		mntmLoadForecastsFor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mySession.getMyWeatherController().downloadNewForecastsForRoute(100);
			}
		});
		
		mnForecasts.add(mntmLoadForecastsFor);
		
		JMenuItem mntmLoadForecastsFor_1 = new JMenuItem("Load Forecasts for current location");
		mntmLoadForecastsFor_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Was asked currentLocation forecast");
			}
		});
		
		mnForecasts.add(mntmLoadForecastsFor_1);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
				
		buildTemperatureChart(createBlankDataset());
		temperatureChart = new ChartPanel(temperatureChartJFree);
		GridBagConstraints gbc_temperatureChart = new GridBagConstraints();
		gbc_temperatureChart.insets = new Insets(0, 0, 5, 5);
		gbc_temperatureChart.fill = GridBagConstraints.BOTH;
		gbc_temperatureChart.gridx = 0;
		gbc_temperatureChart.gridy = 0;
		contentPane.add(temperatureChart, gbc_temperatureChart);
		
		buildCloudCoverChart(createBlankDataset());
		cloudCoverChart = new ChartPanel(cloudCoverChartJFree);
		GridBagConstraints gbc_cloudCoverChart = new GridBagConstraints();
		gbc_cloudCoverChart.insets = new Insets(0, 0, 5, 0);
		gbc_cloudCoverChart.fill = GridBagConstraints.BOTH;
		gbc_cloudCoverChart.gridx = 1;
		gbc_cloudCoverChart.gridy = 0;
		contentPane.add(cloudCoverChart, gbc_cloudCoverChart);
		
		buildPrecipitationChart(createBlankDataset());
		precipitationChart = new ChartPanel(precipitationChartJFree);
		GridBagConstraints gbc_precipitationChart = new GridBagConstraints();
		gbc_precipitationChart.insets = new Insets(0, 0, 0, 5);
		gbc_precipitationChart.fill = GridBagConstraints.BOTH;
		gbc_precipitationChart.gridx = 0;
		gbc_precipitationChart.gridy = 1;
		contentPane.add(precipitationChart, gbc_precipitationChart);
		
		buildWindSpeedChart(createBlankDataset());
		windSpeedChart = new ChartPanel(windSpeedChartJFree);
		GridBagConstraints gbc_windSpeedChart = new GridBagConstraints();
		gbc_windSpeedChart.fill = GridBagConstraints.BOTH;
		gbc_windSpeedChart.gridx = 1;
		gbc_windSpeedChart.gridy = 1;
		contentPane.add(windSpeedChart, gbc_windSpeedChart);
		
		
		setTitleAndLogo();
		}
		
		private void setTitleAndLogo(){
			
			this.setIconImage(mySession.iconImage.getImage());
			this.setTitle("Weather");
		}
		
		/**
		 * All notifications that this class has registered for will come here
		 */
		@Override
		public void notify(Notification n){
			//add any notifications here
			
			//A new map has been loaded into the program
			if(n.getClass() == NewForecastReport.class){ //when a new map is loaded, propogate the new name. 
				NewForecastReport n2 = (NewForecastReport) n; 
				currentForecastReport = n2.getTheReport();
				//labelUpdate(n2.getMapLoadedName());
				//updateMap(n2.getRoute().getTrailMarkers(), -1, DistanceUnit.KILOMETERS);
			//	JOptionPane.showMessageDialog(this, "New map: " + (((NewMapLoadedNotification) n).getMapLoadedName()));	
			}
			if(n.getClass() == NewMapLoadedNotification.class){
				NewMapLoadedNotification n2 = (NewMapLoadedNotification) n;
				startLocation = n2.getRoute().getTrailMarkers().get(0);
			}
				updateCharts();

		}
		
		/**
		 * register for any notifications that this class needs to
		 */
		@Override
		public void register(){
			mySession.register(this, NewForecastReport.class);
			mySession.register(this, NewMapLoadedNotification.class);
			//add any notifications you need to listen for here. 
		}
		
		public void updateCharts(){
			
			buildTemperatureChart(createChartDataset(WeatherChartType.TEMPERATURE));
			temperatureChart.setChart(temperatureChartJFree);
			buildCloudCoverChart(createChartDataset(WeatherChartType.CLOUD_COVER));
			cloudCoverChart.setChart(cloudCoverChartJFree);
			buildPrecipitationChart(createChartDataset(WeatherChartType.PRECIPITATION));
			precipitationChart.setChart(precipitationChartJFree);
			buildWindSpeedChart(createChartDataset(WeatherChartType.WIND_SPEED));
			windSpeedChart.setChart(windSpeedChartJFree);
			
			temperatureChart.repaint();
			temperatureChart.revalidate();
			cloudCoverChart.repaint();
			cloudCoverChart.revalidate();
			precipitationChart.repaint();
			precipitationChart.revalidate();
			windSpeedChart.repaint();
			windSpeedChart.revalidate();
			contentPane.revalidate();
			contentPane.repaint();
		}

		
		
		private void buildDefaultChart(){
			XYDataset ds = createDataset();
			JFreeChart elevationChart = 
					ChartFactory.createXYLineChart(
							"Height Chart",
							"X_AXIS_LABEL",
							"Y_AXIS_LABEL", 
							ds,
							PlotOrientation.VERTICAL, true, true, false);
			temperatureChartJFree = elevationChart;
		}
		
		private void buildTemperatureChart(XYDataset ds){
			JFreeChart temperatureChart = 
					ChartFactory.createXYLineChart(
							"Temperature",
							X_AXIS_LABEL,
							"Temperature (Celsius)", 
							ds,
							PlotOrientation.VERTICAL, true, true, false);
			temperatureChartJFree = temperatureChart;
		}
		
		private void buildCloudCoverChart(XYDataset ds){
			JFreeChart cloudCoverChart = 
					ChartFactory.createXYLineChart(
							"Cloud Cover",
							X_AXIS_LABEL,
							"Cloud Cover Percentage", 
							ds,
							PlotOrientation.VERTICAL, true, true, false);
			cloudCoverChartJFree = cloudCoverChart;
		}
		
		private void buildPrecipitationChart(XYDataset ds){
			JFreeChart precipitationChart = 
					ChartFactory.createXYLineChart(
							"Precipitation",
							X_AXIS_LABEL,
							"Chance of Precipitation (%)", 
							ds,
							PlotOrientation.VERTICAL, true, true, false);
			precipitationChartJFree = precipitationChart;
		}
		
		private void buildWindSpeedChart(XYDataset ds){
			JFreeChart headwindChart = 
					ChartFactory.createXYLineChart(
							"Wind Speed",
							X_AXIS_LABEL,
							"Wind Speed (km/h)", 
							ds,
							PlotOrientation.VERTICAL, true, true, false);
			windSpeedChartJFree = headwindChart;
		}
		
		private XYDataset createChartDataset(WeatherChartType chartType){
			if(currentForecastReport == null || startLocation == null){
				return createBlankDataset();
			}else{
				DefaultXYDataset dds = new DefaultXYDataset();
				List<ForecastIO> forecasts = currentForecastReport.getForecasts();
				List<GeoCoord> forecastPoints = new ArrayList<GeoCoord>();
				List<GeoCoord> trailMarkers = mySession.getMapController().getAllPoints().getTrailMarkers();
				List<FIODataBlock> hourlyForecasts = new ArrayList<FIODataBlock>();
				for(int i = 0; i < forecasts.size(); i++){
					forecastPoints.add(new GeoCoord(forecasts.get(i).getLatitude(), 
							forecasts.get(i).getLongitude(), 0.0));//uses 0 for elevation cause it doesn't matter for our uses
					hourlyForecasts.add(new FIODataBlock(forecasts.get(i).getHourly()));
				}
				double[] distances = new double[forecasts.size()];
				int distanceIndex = 1;
				int trailMarkerIndex = 1;
				double travelDistance = 0.0;
				distances[0] = travelDistance;
				while(distanceIndex < distances.length && trailMarkerIndex < trailMarkers.size()){
					travelDistance += trailMarkers.get(trailMarkerIndex-1).calculateDistance(
							trailMarkers.get(trailMarkerIndex), DistanceUnit.KILOMETERS);
					GeoCoord currentMarker = new GeoCoord(trailMarkers.get(trailMarkerIndex).getLat(),
							trailMarkers.get(trailMarkerIndex).getLon(), 0.0);
					if(currentMarker.equals(forecastPoints.get(distanceIndex))){
						forecastPoints.set(distanceIndex, trailMarkers.get(trailMarkerIndex));
						//updates GeoCoord of forecasts to include elevation, will make comparing
						//GeoCoords easier when calculating headwind
						distances[distanceIndex] = travelDistance;
						distanceIndex++;
					}
					trailMarkerIndex++;
				}
				/*for(int i = 1; i < distances.length; i++){
					travelDistance += forecastPoints.get(i-1).calculateDistance(forecastPoints.get(i), 
							DistanceUnit.KILOMETERS);
					distances[i] = travelDistance;
				}*/
				
				int numHours = 0;
				
				while((numHours < NUM_LINES) && ( numHours < hourlyForecasts.size()) ){
					double[][] data = new double[2][distances.length];
					for(int i = 0; i < hourlyForecasts.size(); i++){
						data[0][i] = distances[i];
						FIODataPoint currentHourForecast = hourlyForecasts.get(i).datapoint(numHours);
						if(chartType.equals(WeatherChartType.TEMPERATURE)){
							data[1][i] = currentHourForecast.temperature();
							System.out.println("Hour num: " + currentHourForecast.time());
						}else if(chartType.equals(WeatherChartType.CLOUD_COVER)){
							data[1][i] = currentHourForecast.cloudCover();
						}else if(chartType.equals(WeatherChartType.PRECIPITATION)){
							data[1][i] = currentHourForecast.precipProbability()*100;
						}else if(chartType.equals(WeatherChartType.WIND_SPEED)){
							data[1][i] = currentHourForecast.windSpeed();
						}
					}
					dds.addSeries("Hour " + numHours, data);
					numHours++;
				}
				
				return dds;
			}
		}
		
		private XYDataset createDataset(){
			DefaultXYDataset dds = new DefaultXYDataset();
				double[][] data = { {0.1, 0.2, 0.3}, {1, 2, 3} };
				
				dds.addSeries("series1", data);
				return dds;
		}
		
		private XYDataset createBlankDataset(){
			DefaultXYDataset dds = new DefaultXYDataset();
			double[][] data = new double[2][0];
			dds.addSeries("", data);
			return dds;
		}

}
