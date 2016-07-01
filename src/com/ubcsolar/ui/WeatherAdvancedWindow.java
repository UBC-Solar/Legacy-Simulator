package com.ubcsolar.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import com.github.dvdme.ForecastIOLib.FIODataBlock;
import com.github.dvdme.ForecastIOLib.FIODataPoint;
import com.github.dvdme.ForecastIOLib.ForecastIO;
import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.Main.GlobalValues;
import com.ubcsolar.common.ForecastReport;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.Listener;
import com.ubcsolar.common.LocationReport;
import com.ubcsolar.common.LogType;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.exception.InconsistentForecastMapStateException;
import com.ubcsolar.exception.NoLoadedRouteException;
import com.ubcsolar.notification.NewForecastReport;
import com.ubcsolar.notification.NewLocationReportNotification;
import com.ubcsolar.notification.NewMapLoadedNotification;
import com.ubcsolar.notification.Notification;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.SwingConstants;

public class WeatherAdvancedWindow extends JFrame implements Listener{
	
	private GUImain parent;
	private JPanel contentPane;
	private GlobalController mySession;
	private ChartPanel temperatureChart;
	private JFreeChart temperatureChartJFree;
	private ForecastReport currentForecastReport;
	private final String X_AXIS_LABEL = "Travel Distance (km)";
	private final int NUM_LINES = 4;
	private ChartPanel cloudCoverChart;
	private JFreeChart cloudCoverChartJFree;
	private ChartPanel precipitationChart;
	private JFreeChart precipitationChartJFree;
	private ChartPanel windSpeedChart;
	private JFreeChart windSpeedChartJFree;
	private JPanel windDirectionPanel;
	private JPanel panel;
	private JPanel fogPanel;
	private JLabel fogLabel;
	private JPanel stormPanel;
	private JLabel stormLabel;
	private JLabel windDirectionLabel;
	private GeoCoord currentLocation;
	private final int DEW_POINT_DIFF = 7;
	private double travelDistance;
	private double[] distances;
	private double kilometerMark;
	private boolean showWelcomeMessageAgain = true;
	
	private static final String WelcomeInfoMessage = "use \"Load Forecasts for Route(48 hours)\" under the \"Forecasts\" menu to get the weather information."
				+"\n\n"+ "Note: You should Load the route before this.";


	protected final WeatherAdvancedWindow parentInstance = this;
	private List<GeoCoord> forecastPoints;
	private JMenuItem mntmLoadLastForecast;

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

	
	private void setLabelDefaultValues(){
		this.stormLabel.setText("No storm warning");
		this.fogLabel.setText("No fog warning");
		this.windDirectionLabel.setText("Wind is blowing from: None");
	}
	/**
	 * Create the frame.
	 * @param mySession 
	 */
	public WeatherAdvancedWindow(final GlobalController mySession, GUImain main) {
		
		
		parent = main;
		setTitle("Advanced Weather");
		this.mySession = mySession;
		travelDistance = 0.0;
		register();
		setTitle("Weather");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1043, 655);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnForecasts = new JMenu("Forecasts");
		menuBar.add(mnForecasts);
		
		JMenuItem mntmLoadForecastsFor = new JMenuItem("Load Forecasts for Route (48 hours)");
		mntmLoadForecastsFor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));// changing the cursor type
				JFrame frame = new LoadingWindow(mySession);
				frame.setVisible(true);
				
				try{
					mySession.getMyWeatherController().downloadNewForecastsForRoute(100); //main Process
					
					frame.setVisible(false);
					contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));// changing the cursor type
					Toolkit.getDefaultToolkit().beep(); // simple alert for end of process
					
					mapChartNavigationTutorialDialog();
				}catch(IOException e){
					frame.setVisible(false); //no need to show the loading screen now.
					contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));// changing the cursor type
					handleError("IOException, check internet connection");
					Toolkit.getDefaultToolkit().beep(); // simple alert for end of process
					e.printStackTrace();
				}

			}
		});
		
		mnForecasts.add(mntmLoadForecastsFor);
		
		mntmLoadLastForecast = new JMenuItem("Load Last Forecast From File");
		mntmLoadLastForecast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File defaultDirectory = new File(mySession.getMyDataBaseController().getCurrentOutputFolderName());
				JFileChooser fc = new JFileChooser();
				if(defaultDirectory.exists() && defaultDirectory.isDirectory()){
					fc.setCurrentDirectory(defaultDirectory);
				}
				fc.addChoosableFileFilter(new FileNameExtensionFilter("Forecast Cache Files", "FIO", "fio"));
				fc.setAcceptAllFileFilterUsed(false); //makes the 'kml' one default. 
				fc.setAcceptAllFileFilterUsed(true);
				 int returnVal = fc.showOpenDialog(parentInstance);
				 
				 if (returnVal == JFileChooser.APPROVE_OPTION) {
					
				//	 loadFrame and change the cursor type to waiting cursor
					 
					 contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					 JFrame frame = new LoadingWindow(mySession);
					 frame.setVisible(true);
					 
					 try {
						mySession.getMyWeatherController().loadLastForecastFromFile(fc.getSelectedFile());
					} catch (FileNotFoundException e) {
						handleError("Error: File not found");
						SolarLog.write(LogType.ERROR, System.currentTimeMillis(), "File not found - tried to load FC from: " + fc.getSelectedFile());
						e.printStackTrace();
					} catch (IOException e) {
						handleError("Error: IO Exception");
						SolarLog.write(LogType.ERROR, System.currentTimeMillis(), "IOException - tried to load FC from: " + fc.getSelectedFile());
						e.printStackTrace();
					} //main process
					 catch (InconsistentForecastMapStateException e) {
						 handleError("ERROR: Forecast not for current map");
						 SolarLog.write(LogType.ERROR, System.currentTimeMillis(), "Tried to load FC for route: " + e.getForecastRouteName() + " but loaded route was: " + e.getLoadedRouteName());
						e.printStackTrace();
					}
					 
					 frame.setVisible(false);
					 contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			        }
				 
				 else {
			            //cancelled by user, do nothing
			        	return;
			        }
			}
		});
		
		mnForecasts.add(mntmLoadLastForecast);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
				
		buildTemperatureChart(createBlankDataset());
		temperatureChart = new ChartPanel(temperatureChartJFree);
		temperatureChart.setMouseWheelEnabled(true);
		temperatureChart.setMouseZoomable(true);
		GridBagConstraints gbc_temperatureChart = new GridBagConstraints();
		gbc_temperatureChart.weighty = 10.0;
		gbc_temperatureChart.insets = new Insets(0, 0, 5, 5);
		gbc_temperatureChart.fill = GridBagConstraints.BOTH;
		gbc_temperatureChart.gridx = 0;
		gbc_temperatureChart.gridy = 0;
		contentPane.add(temperatureChart, gbc_temperatureChart);
		
		buildCloudCoverChart(createBlankDataset());
		cloudCoverChart = new ChartPanel(cloudCoverChartJFree);
		cloudCoverChart.setMouseZoomable(true);
		cloudCoverChart.setMouseWheelEnabled(true);
		GridBagConstraints gbc_cloudCoverChart = new GridBagConstraints();
		gbc_cloudCoverChart.insets = new Insets(0, 0, 5, 0);
		gbc_cloudCoverChart.fill = GridBagConstraints.BOTH;
		gbc_cloudCoverChart.gridx = 1;
		gbc_cloudCoverChart.gridy = 0;
		contentPane.add(cloudCoverChart, gbc_cloudCoverChart);
		
		buildPrecipitationChart(createBlankDataset());
		precipitationChart = new ChartPanel(precipitationChartJFree);
		precipitationChart.setMouseZoomable(true);
		precipitationChart.setMouseWheelEnabled(true);
		GridBagConstraints gbc_precipitationChart = new GridBagConstraints();
		gbc_precipitationChart.weighty = 10.0;
		gbc_precipitationChart.insets = new Insets(0, 0, 5, 5);
		gbc_precipitationChart.fill = GridBagConstraints.BOTH;
		gbc_precipitationChart.gridx = 0;
		gbc_precipitationChart.gridy = 1;
		contentPane.add(precipitationChart, gbc_precipitationChart);
		
		buildWindSpeedChart(createBlankDataset());
		windSpeedChart = new ChartPanel(windSpeedChartJFree);
		windSpeedChart.setMouseZoomable(true);
		windSpeedChart.setMouseWheelEnabled(true);
		GridBagConstraints gbc_windSpeedChart = new GridBagConstraints();
		gbc_windSpeedChart.insets = new Insets(0, 0, 5, 0);
		gbc_windSpeedChart.fill = GridBagConstraints.BOTH;
		gbc_windSpeedChart.gridx = 1;
		gbc_windSpeedChart.gridy = 1;
		contentPane.add(windSpeedChart, gbc_windSpeedChart);
		
		fogPanel = new JPanel();
		GridBagConstraints gbc_fogPanel = new GridBagConstraints();
		gbc_fogPanel.insets = new Insets(0, 0, 5, 5);
		gbc_fogPanel.gridx = 0;
		gbc_fogPanel.gridy = 2;
		contentPane.add(fogPanel, gbc_fogPanel);
		
		windDirectionPanel = new JPanel();
		GridBagConstraints gbc_windDirectionPanel = new GridBagConstraints();
		gbc_windDirectionPanel.insets = new Insets(0, 0, 5, 0);
		gbc_windDirectionPanel.gridx = 1;
		gbc_windDirectionPanel.gridy = 2;
		contentPane.add(windDirectionPanel, gbc_windDirectionPanel);
		
		stormPanel = new JPanel();
		GridBagConstraints gbc_stormPanel = new GridBagConstraints();
		gbc_stormPanel.insets = new Insets(0, 0, 0, 5);
		gbc_stormPanel.gridx = 0;
		gbc_stormPanel.gridy = 3;
		contentPane.add(stormPanel, gbc_stormPanel);
		
		windDirectionLabel = new JLabel("DEFAULT");
		windDirectionPanel.add(windDirectionLabel);
		
		fogLabel = new JLabel("DEFAULT");
		fogLabel.setHorizontalAlignment(SwingConstants.LEFT);
		fogPanel.add(fogLabel);
		
		stormLabel = new JLabel("DEFAULT");
		stormPanel.add(stormLabel);
		
		this.setLabelDefaultValues();
		panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 3;
		contentPane.add(panel, gbc_panel);
		
		
		setTitleAndLogo();
		}
		
		protected void handleError(String string) {
			JOptionPane.showMessageDialog(this, string);
		}
		private void setTitleAndLogo(){
			
			this.setIconImage(GlobalValues.iconImage.getImage());
			this.setTitle("Weather");
		}
		
		/**
		 * pops up a tutorial dialog 
		 */
		private void mapChartNavigationTutorialDialog() {
			Object[] options= { "Ok, Thanks" ,  "Don't show this message again" };
			
			if (GlobalValues.showChartNavigationTutorialAgain == true)
			{
				int chosenOption= JOptionPane.showOptionDialog(this, GlobalValues.CHART_TUT_MESSAGE , "Tutorial", JOptionPane.YES_NO_OPTION,
					JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			
				if (chosenOption == 1){
					GlobalValues.showChartNavigationTutorialAgain = false;
				}
				else{
					GlobalValues.showChartNavigationTutorialAgain = true;
				}
			}
		}
		
		
		/**
		 * pops up a tutorial dialog 
		 */
		public void welcomeInfoDialog() {
			Object[] options= { "Ok, Thanks" ,  "Don't show this message again" };
			
			if (showWelcomeMessageAgain == true)
			{
				int chosenOption= JOptionPane.showOptionDialog(this, WelcomeInfoMessage , "Tutorial", JOptionPane.YES_NO_OPTION,
					JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
			
				if (chosenOption == 1){
					showWelcomeMessageAgain = false;
				}
				else{
					showWelcomeMessageAgain = true;
				}
			}
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
				//updateMap(n2.getRoute().getTrailMarkers(), -1);
			//	JOptionPane.showMessageDialog(this, "New map: " + (((NewMapLoadedNotification) n).getMapLoadedName()));	
			}
			if(n.getClass() == NewLocationReportNotification.class){
				NewLocationReportNotification n2 = (NewLocationReportNotification) n;
				currentLocation = n2.getCarLocation().getLocation();
				this.updateCarPositionBar(currentLocation);

				
			}
			updateCharts();
			updateLabels();

		}
		

		/**
		 * register for any notifications that this class needs to
		 */
		@Override
		public void register(){
			mySession.register(this, NewForecastReport.class);
			mySession.register(this, NewLocationReportNotification.class);
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
			contentPane.repaint();
			contentPane.revalidate();
			
			if (currentLocation != null){ //a duct tape way to avoid having vertical bar on 0KM. 
				this.updateCarPositionBar(kilometerMark);
			}
			
		}
		
		public void updateLabels(){
			
			if(currentLocation != null &&
					currentForecastReport != null &&
					currentForecastReport.getForecasts().size()>0){
				//next block figures out which forecast is closest to the current car location
				//and which one will be the next one in the sequence
				List<ForecastIO> forecasts = currentForecastReport.getForecasts();
				List<FIODataBlock> hourlyForecasts = new ArrayList<FIODataBlock>();
				ForecastIO closestForecast = forecasts.get(0);
				GeoCoord closestForecastLocation = new GeoCoord(closestForecast.getLatitude(),
						closestForecast.getLongitude(), 0.0);
				ForecastIO nextForecast = null;
				boolean nextForecastExists = false;
				if(forecasts.size() > 1){
					nextForecast = forecasts.get(1);
					nextForecastExists = true;
				}
				for(int i = 1; i < forecasts.size(); i++){
					GeoCoord curr = new GeoCoord(forecasts.get(i).getLatitude(),
							forecasts.get(i).getLongitude(), 0.0);
					if(currentLocation.calculateDistance(curr) <
							currentLocation.calculateDistance(closestForecastLocation)){
						closestForecast = forecasts.get(i);
						closestForecastLocation = curr;
						if(i == forecasts.size()-1){
							nextForecastExists = false;
						}else{
							nextForecast = forecasts.get(i+1);
						}
					}					
				}
				FIODataBlock closestHourly = new FIODataBlock(closestForecast.getHourly());
				FIODataPoint closestForecastNow = closestHourly.datapoint(0); //TODO need to do something about the STORM because storm seems to be only in CURRENTLY forecast
				FIODataBlock nextHourly;
				if(nextForecastExists){
					nextHourly = new FIODataBlock(nextForecast.getHourly());
				}
				
				double windBearing = closestForecastNow.windBearing();
				String windDirection = findDirection(windBearing);
				
				boolean fogWarning = false;
				String dewDifference = "";
				if(closestForecastNow.dewPoint()+DEW_POINT_DIFF >= closestForecastNow.temperature()){
					fogWarning = true;
					 double dewDifferenceNum = closestForecastNow.temperature()-closestForecastNow.dewPoint();
					 dewDifference = new DecimalFormat("#.##").format(dewDifferenceNum);
				}
				
				boolean stormWarning = false;
				FIODataPoint closestCurrently = new FIODataPoint(closestForecast.getCurrently());
				if(closestForecast.hasCurrently()){
					
					if(closestCurrently.nearestStormDistance() < 100 && 
							closestCurrently.nearestStormDistance() >= 0){
						stormWarning = true;
					}
				}
				
				windDirectionLabel.setText("Wind is blowing from: " + windDirection + " (" 
						+ windBearing + "°)");
				if(fogWarning){
					fogLabel.setText("Fog warning. (Temp is " + 
							dewDifference + "° above dew point.)");
				}else{
					fogLabel.setText("No fog warning");
				}
				if(stormWarning){
					double stormBearing = closestCurrently.nearestStormBearing();
					String stormDirection = findDirection(stormBearing);
					stormLabel.setText("Warning: There's a storm " + closestCurrently.nearestStormDistance()
							+ " km to the " + stormDirection + " (" + stormBearing + "°)");
				}
				
				
			}
			else{
				this.setLabelDefaultValues();
			}
			windDirectionLabel.repaint();
			windDirectionPanel.repaint();
			fogLabel.repaint();
			fogPanel.repaint();
			stormLabel.repaint();
			stormPanel.repaint();
		}

		private String findDirection(double windBearing){
			String windDirection;
			if(windBearing == -1){
				windDirection = "none";
			}else if((windBearing >= 0 && windBearing < 22.5) || (windBearing >= 337.5 )){
				windDirection = "N";
			}else if(windBearing >= 22.5 && windBearing < 67.5){
				windDirection = "NE";
			}else if(windBearing >= 67.5 && windBearing < 112.5){
				windDirection = "E";
			}else if(windBearing >= 112.5 && windBearing < 157.5){
				windDirection = "SE";
			}else if(windBearing >= 157.5 && windBearing < 202.5){
				windDirection = "S";
			}else if(windBearing >= 202.5 && windBearing < 247.5){
				windDirection = "SW";
			}else if(windBearing >= 247.5 && windBearing < 292.5){
				windDirection = "W";
			}else{
				windDirection = "NW";
			}
			return windDirection;
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
			
			XYPlot plot = (XYPlot) temperatureChartJFree.getPlot();		
			plot.setRangePannable(true);
			plot.setDomainPannable(true);
			
			
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
			
			XYPlot plot = (XYPlot) temperatureChartJFree.getPlot();
			for(int i = 0; i<ds.getSeriesCount(); i++){
				final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer(0);
				//renderer.setBaseLegendTextFont(new Font("Helvetica", Font.BOLD, 11));
				renderer.setSeriesStroke(i, new BasicStroke(2));
			}
			plot.setRangePannable(true);
			plot.setDomainPannable(true);
			
			
		}
		
		private void buildCloudCoverChart(XYDataset ds){
			JFreeChart cloudCoverChart = 
					ChartFactory.createXYLineChart(
							"Cloud Cover",
							X_AXIS_LABEL,
							"Cloud Cover (%)", 
							ds,
							PlotOrientation.VERTICAL, true, true, false);
			cloudCoverChartJFree = cloudCoverChart;
			
			XYPlot plot = (XYPlot) cloudCoverChartJFree.getPlot();	
			for(int i = 0; i<ds.getSeriesCount(); i++){
				final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer(0);
				//renderer.setBaseLegendTextFont(new Font("Helvetica", Font.BOLD, 11));
				renderer.setSeriesStroke(i, new BasicStroke(2));
			}
			plot.setRangePannable(true);
			plot.setDomainPannable(true);
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
			
			XYPlot plot = (XYPlot) precipitationChartJFree.getPlot();
			for(int i = 0; i<ds.getSeriesCount(); i++){
				final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer(0);
				//renderer.setBaseLegendTextFont(new Font("Helvetica", Font.BOLD, 11));
				renderer.setSeriesStroke(i, new BasicStroke(2));
			}
			plot.setRangePannable(true);
			plot.setDomainPannable(true);
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
			
			XYPlot plot = (XYPlot) windSpeedChartJFree.getPlot();		
			for(int i = 0; i<ds.getSeriesCount(); i++){
				final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer(0);
				//renderer.setBaseLegendTextFont(new Font("Helvetica", Font.BOLD, 11));
				renderer.setSeriesStroke(i, new BasicStroke(2));
			}
			plot.setRangePannable(true);
			plot.setDomainPannable(true);
		}
		
		private XYDataset createChartDataset(WeatherChartType chartType){
			if(currentForecastReport == null || currentForecastReport.getForecasts().size() == 0){
				return createBlankDataset();
			}else{
				DefaultXYDataset dds = new DefaultXYDataset();
				List<ForecastIO> forecastsForChart = currentForecastReport.getForecasts();
				forecastPoints = new ArrayList<GeoCoord>();
				
				List<FIODataBlock> hourlyForecasts = new ArrayList<FIODataBlock>();
				for(int i = 0; i < forecastsForChart.size()+1; i++){
					//if statement adds duplicate point at the end
					if (i == forecastsForChart.size()){
						forecastPoints.add(new GeoCoord(forecastsForChart.get(i-1).getLatitude(), 
								forecastsForChart.get(i-1).getLongitude(), 0.0));//uses 0 for elevation cause it doesn't matter for our uses
						hourlyForecasts.add(new FIODataBlock(forecastsForChart.get(i-1).getHourly()));
					}else{
						forecastPoints.add(new GeoCoord(forecastsForChart.get(i).getLatitude(), 
								forecastsForChart.get(i).getLongitude(), 0.0));//uses 0 for elevation cause it doesn't matter for our uses
						hourlyForecasts.add(new FIODataBlock(forecastsForChart.get(i).getHourly()));
					}
				}
				
				distances = new double[forecastsForChart.size()+1];
				List<GeoCoord> trailMarkers = mySession.getMapController().getAllPoints().getTrailMarkers();
				int distanceIndex = 1;
				int trailMarkerIndex = 1;
				travelDistance = 0.0;
				distances[0] = travelDistance;
				while(/*distanceIndex < distances.length &&*/ trailMarkerIndex < trailMarkers.size()){
					travelDistance += trailMarkers.get(trailMarkerIndex-1).calculateDistance(
							trailMarkers.get(trailMarkerIndex));
					GeoCoord currentMarker = new GeoCoord(trailMarkers.get(trailMarkerIndex).getLat(),
							trailMarkers.get(trailMarkerIndex).getLon(), 0.0);
					if(currentMarker.equals(forecastPoints.get(distanceIndex))){
						//forecastPoints.set(distanceIndex, trailMarkers.get(trailMarkerIndex));
						//updates GeoCoord of forecasts to include elevation, will make comparing
						//GeoCoords easier when calculating headwind
						distances[distanceIndex] = travelDistance;
						distanceIndex++;
					}if(trailMarkerIndex == trailMarkers.size()-1){
						distances[distanceIndex] = travelDistance;
						distanceIndex++;
					}
					trailMarkerIndex++;
				}

				
				int numHours = 0;
				while((numHours < NUM_LINES) && ( numHours < hourlyForecasts.get(0).datablockSize()) ){
					double[][] data = new double[2][distances.length];
					for(int i = 0; i < hourlyForecasts.size(); i++){
						data[0][i] = distances[i];
						FIODataPoint currentHourForecast = hourlyForecasts.get(i).datapoint(numHours);
						if(chartType.equals(WeatherChartType.TEMPERATURE)){
							data[1][i] = currentHourForecast.temperature();
						}else if(chartType.equals(WeatherChartType.CLOUD_COVER)){
							data[1][i] = currentHourForecast.cloudCover()*100;
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
		
		public double getTravelDistance(){
			return travelDistance;
		}
		
		public void clearWindow(){
			this.currentForecastReport = null;
			this.updateCharts();
		}
		
		/**
		 * Needed so that the window always pops up when requested. 
		 * See http://stackoverflow.com/questions/309023/how-to-bring-a-window-to-the-front 
		 * for reasoning
		 */
		@Override
		public void toFront() {
		   // int sta = super.getExtendedState() & ~JFrame.ICONIFIED & JFrame.NORMAL;

		   // super.setExtendedState(sta);
		    super.setAlwaysOnTop(true);
		    super.toFront();
		    super.requestFocus();
		    super.setAlwaysOnTop(false);
		}
		
		private void updateCarPositionBar(GeoCoord currentLocation) {
			//System.out.println("TRYING TO UPDATE CAR BAR IN WEATHER CHARTS");
			//System.out.println(mySession.getMapController().hasMapLoaded());
			if(this.mySession.getMapController().hasMapLoaded()){
				try {
					kilometerMark=(this.mySession.getMapController().findDistanceAlongLoadedRoute(currentLocation));
				} catch (NoLoadedRouteException e) {
					SolarLog.write(LogType.ERROR, System.currentTimeMillis(), "Tried to get Car location on Map Controller, but no route loaded");
				}
			}
			
		}
		private void updateCarPositionBar(double kilometerMark){
			//System.out.println("DRAWING LINE FOR KM: " + kilometerMark);
			ValueMarker marker = new ValueMarker(kilometerMark);  // position is the value on the axis
			marker.setPaint(Color.black);
			//marker.setLabel("here"); // see JavaDoc for labels, colors, strokes

			XYPlot plotTemperature = (XYPlot) temperatureChartJFree.getPlot();
		//	plotTemperature.clearDomainMarkers();
			plotTemperature.addDomainMarker(marker);
			
			XYPlot plotCloudCover = (XYPlot) cloudCoverChartJFree.getPlot();
		//	plotCloudCover.clearDomainMarkers();
			plotCloudCover.addDomainMarker(marker);

			XYPlot plotPrecipitation = (XYPlot) precipitationChartJFree.getPlot();
		//	plotPrecipitation.clearDomainMarkers();
			plotPrecipitation.addDomainMarker(marker);
			
			XYPlot plotWindSpeed = (XYPlot) windSpeedChartJFree.getPlot();
		//	plotWindSpeed.clearDomainMarkers();
			plotWindSpeed.addDomainMarker(marker);
			
			temperatureChart.revalidate();
			temperatureChart.repaint();
			
			cloudCoverChart.revalidate();
			cloudCoverChart.repaint();
			
			precipitationChart.revalidate();
			precipitationChart.repaint();
			
			windSpeedChart.revalidate();
			windSpeedChart.repaint();
			
			contentPane.revalidate();
			contentPane.repaint();
		}
}
