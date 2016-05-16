package com.ubcsolar.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.Listener;
import com.ubcsolar.common.SimFrame;
import com.ubcsolar.common.SimulationReport;
import com.ubcsolar.exception.NoCarStatusException;
import com.ubcsolar.exception.NoForecastReportException;
import com.ubcsolar.exception.NoLoadedRouteException;
import com.ubcsolar.exception.NoLocationReportedException;
import com.ubcsolar.notification.NewSimulationReportNotification;
import com.ubcsolar.notification.Notification;

import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JMenu;

import java.awt.GridBagLayout;

import javax.swing.JButton;

import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.awt.event.ActionEvent;
import java.awt.Insets;
import javax.swing.JCheckBox;
import java.awt.FlowLayout;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextField;

public class SimulationAdvancedWindow extends JFrame implements Listener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2684909507474196406L;
	private static final String CHART_TITLE = "Sim Results";
	private JPanel contentPane; //the root content holder
	private GlobalController mySession; 
	private JFreeChart simResults; //the main chart model.
	private final String X_AXIS_LABEL = "Distance (km)";
	private final String Y_AXIS_LABEL = "speed (km/h)";
	private final int xValues = 0; //for the Double[][] dataset
	private final int yValues = 1; //for the Double[][] dataset
	private JPanel buttonPanel; 
	private ChartPanel mainDisplay; //the panel displaying the model
	private SimulationReport lastSimReport; //cache the last simReport
	
	private boolean showSpeed = true;
	private boolean showStateOfCharge = true;
	private boolean showCloud = true;
	private boolean showElevation = true;
	private JScrollPane speedSlidersPanel;
	private JTextField textField_1;
	private JPanel SliderHoldingPanel;
	private List<SliderSpinnerFrame> displayedSpeedSliderSpinners;

	
	private void handleError(String message){
		JOptionPane.showMessageDialog(this, message);
	}
	
	/**
	 * Create the frame.
	 * @param mySession 
	 */
	public SimulationAdvancedWindow(GlobalController mySession) {
		
		this.mySession = mySession;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 543);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		
		JMenu mnNewMenu_1 = new JMenu("Results");
		menuBar.add(mnNewMenu_1);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		buttonPanel = new JPanel();
		contentPane.add(buttonPanel, BorderLayout.NORTH);
		
		JButton btnNewSimulation = new JButton("New Simulation");
		btnNewSimulation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				runSimultion();
			}
		});
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		buttonPanel.add(btnNewSimulation);
		
		JCheckBox chckbxSpeed = new JCheckBox("Speed");
		chckbxSpeed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showSpeed = chckbxSpeed.isSelected();
				refreshChart();
			}
		});
		chckbxSpeed.setSelected(true);
		buttonPanel.add(chckbxSpeed);
		
		JCheckBox chckbxSoc = new JCheckBox("SoC");
		chckbxSoc.setSelected(true);
		chckbxSoc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showStateOfCharge = chckbxSoc.isSelected();
				refreshChart();
			}
		});
		buttonPanel.add(chckbxSoc);
		
		
		
		JCheckBox chckbxCloud = new JCheckBox("Cloud");
		chckbxCloud.setSelected(true);
		chckbxCloud.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCloud = chckbxCloud.isSelected();
				refreshChart();
			}
		});
		buttonPanel.add(chckbxCloud);
		
		JCheckBox chckbxElevation = new JCheckBox("Elevation");
		chckbxElevation.setSelected(true);
		chckbxElevation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showElevation = chckbxElevation.isSelected();
				refreshChart();
			}
		});
		buttonPanel.add(chckbxElevation);
		
		JPanel chartHoldingPanel = new JPanel();
		contentPane.add(chartHoldingPanel, BorderLayout.CENTER);
		GridBagLayout gbl_chartHoldingPanel = new GridBagLayout();
		gbl_chartHoldingPanel.columnWidths = new int[]{0, 0, 0};
		gbl_chartHoldingPanel.rowHeights = new int[]{0, 0, 0};
		gbl_chartHoldingPanel.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_chartHoldingPanel.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		chartHoldingPanel.setLayout(gbl_chartHoldingPanel);
		
		setDefaultChart();
		mainDisplay = new ChartPanel(simResults);
		GridBagConstraints gbc_mainDisplay = new GridBagConstraints();
		gbc_mainDisplay.weighty = 1.0;
		gbc_mainDisplay.weightx = 1.0;
		gbc_mainDisplay.insets = new Insets(0, 0, 5, 5);
		gbc_mainDisplay.fill = GridBagConstraints.BOTH;
		gbc_mainDisplay.gridx = 0;
		gbc_mainDisplay.gridy = 0;
		chartHoldingPanel.add(mainDisplay, gbc_mainDisplay);
		
		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 0;
		chartHoldingPanel.add(panel_1, gbc_panel_1);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 1;
		chartHoldingPanel.add(panel, gbc_panel);
		
		speedSlidersPanel = new JScrollPane();
		speedSlidersPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		contentPane.add(speedSlidersPanel, BorderLayout.SOUTH);
		
		SliderHoldingPanel = new JPanel();
		speedSlidersPanel.setViewportView(SliderHoldingPanel);
		GridBagLayout gbl_SliderHoldingPanel = new GridBagLayout();
		gbl_SliderHoldingPanel.columnWidths = new int[]{0, 0, 0, 0};
		gbl_SliderHoldingPanel.rowHeights = new int[]{0, 0};
		gbl_SliderHoldingPanel.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_SliderHoldingPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		SliderHoldingPanel.setLayout(gbl_SliderHoldingPanel);
		
		
		textField_1 = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 0, 5);
		gbc_textField_1.gridx = 0;
		gbc_textField_1.gridy = 1;
		SliderHoldingPanel.add(textField_1, gbc_textField_1);
		textField_1.setColumns(10);
		setDefaultChart();
		
		setTitleAndLogo();
		this.register();
		}

	private void clearAndLoadSpeedSliders(List<SimFrame> simResultValues, int pointsPerSlider) {
		int KM_PER_SLIDER = 1; //could make this dynamic
	
		SliderHoldingPanel.removeAll();
		
		GridBagLayout gbl_SliderHoldingPanel = new GridBagLayout();
		gbl_SliderHoldingPanel.columnWidths = new int[simResultValues.size()];
		for(int i = 0; i<simResultValues.size(); i++){
			gbl_SliderHoldingPanel.columnWidths[i] = 0;
		}
		gbl_SliderHoldingPanel.rowHeights = new int[]{0, 0};
		
		gbl_SliderHoldingPanel.columnWeights = new double[simResultValues.size()];
		for(int i = 0; i<simResultValues.size(); i++){
			gbl_SliderHoldingPanel.columnWeights[i] = 0; //don't want any slider column growing
		} 
		gbl_SliderHoldingPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		SliderHoldingPanel.setLayout(gbl_SliderHoldingPanel);
		
		
		//add the panels dynamically
		this.displayedSpeedSliderSpinners = new ArrayList<SliderSpinnerFrame>();
		double runningTotalDistance = 0;
		int lastAddedPointIndex = 0;
		double lastAddedPointDistance = 0.0;
		
		//can't set the first speed anyway. 
		for(int i = 1; i<simResultValues.size(); i++){
			GeoCoord start = simResultValues.get(i-1).getGPSReport().getLocation();
			GeoCoord end = simResultValues.get(i).getGPSReport().getLocation();
			runningTotalDistance += start.calculateDistance(end);
			
			if((runningTotalDistance-lastAddedPointDistance)>KM_PER_SLIDER){
				List<GeoCoord> pointsToRepresent = new ArrayList<GeoCoord>();
				double totalSpeed = 0;
				for(int index = lastAddedPointIndex+1; index<=i; index++){
					totalSpeed += simResultValues.get(index).getCarStatus().getSpeed();
					pointsToRepresent.add(simResultValues.get(index).getGPSReport().getLocation());
				}
				
				double averageSpeed = totalSpeed/(i-(lastAddedPointIndex+1)); //avg speed across all points represented
				String formattedKMOne = String.format("%.2f", lastAddedPointDistance); //to avoid having 16 digits
				String formattedKMTwo = String.format("%.2f", runningTotalDistance);
				String label = "KMs: " + formattedKMOne+"-"+formattedKMTwo;
				//String label = lastAddedPointDistance+"km-"+runningTotalDistance+"km";
				SliderSpinnerFrame toAddToPanel = new SliderSpinnerFrame(label ,
										(int) averageSpeed, false,pointsToRepresent);
				displayedSpeedSliderSpinners.add(toAddToPanel);
				
				lastAddedPointIndex = i;
				lastAddedPointDistance = runningTotalDistance;
			}			
		}
		
		
		
		for(int i = 0; i<displayedSpeedSliderSpinners.size(); i++){
			GridBagConstraints temp_gbc_panel = new GridBagConstraints();
			temp_gbc_panel.insets = new Insets(0, 0, 0, 5);
			temp_gbc_panel.fill = GridBagConstraints.BOTH;
			temp_gbc_panel.gridx = i; //add it on the right
			temp_gbc_panel.gridy = 0;
			SliderHoldingPanel.add(displayedSpeedSliderSpinners.get(i), temp_gbc_panel);
		}
		
		//this text box is just to force it to be bigger so the scroll bar doesn't cover anything. 
		textField_1 = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 0, 5);
		gbc_textField_1.gridx = 0;
		gbc_textField_1.gridy = 1;
		SliderHoldingPanel.add(textField_1, gbc_textField_1);
		textField_1.setColumns(5);
	}
		
		/**
		 * Attempts to run a simulation with the loaded data. If there is needed data that
		 * is not loaded, displays an error message to end user. 
		 */
		protected void runSimultion() {
			Map<GeoCoord, Double> requestedSpeeds = generateRequestedSpeedMap();
			try {
				mySession.getMySimController().runSimulation(requestedSpeeds);
			} catch (NoForecastReportException e) {
				this.handleError("No Forcecasts Loaded");
				return;
			} catch (NoLoadedRouteException e) {
				this.handleError("No Route Loaded");
				return;
			} catch (NoLocationReportedException e) {
				this.handleError("No Location Reported Yet");
				return;
			} catch (NoCarStatusException e) {
				this.handleError("No Car Status Reported Yet");
				return;
			}
	}
		
		private Map<GeoCoord, Double> generateRequestedSpeedMap() {
			HashMap<GeoCoord, Double> toReturn = new HashMap<GeoCoord, Double>();
			/*Random rng = new Random();
			if(rng.nextBoolean()){
				for(SimFrame g : this.lastSimReport.getSimFrames()){
					toReturn.put(g.getGPSReport().getLocation(), 25.0);
				}
			}*/ //used this for testing. 
			
			for(SliderSpinnerFrame f : this.displayedSpeedSliderSpinners){
				if(f.isManuallySet()){
					for(GeoCoord g : f.getRepresentedCoordinates()){
						toReturn.put(g, f.getValue()+0.0); //the '+0.0' is to make it a Double. 
					}
					System.out.println("Manually Requesting speed to: " + f.getValue());
				}
			}
			
			return toReturn;
		}

		/**
		 * Builds a default, empty chart. 
		 */
		private void setDefaultChart() {
			XYDataset ds = createBlankDataset();
			this.simResults = 
					ChartFactory.createXYLineChart(
							CHART_TITLE,
							X_AXIS_LABEL,
							Y_AXIS_LABEL, 
							ds,
							PlotOrientation.VERTICAL, true, true, false);
		
		}
		
		
		
		/**
		 * Makes an empty dataset for an empty chart. 
		 * @return - an empty dataset. 
		 */
		private XYDataset createBlankDataset(){
			DefaultXYDataset dds = new DefaultXYDataset();
			double[][] data = new double[2][0];
			dds.addSeries("", data);
			return dds;
		}
		
		private void setTitleAndLogo(){
			this.setIconImage(mySession.iconImage.getImage());
			this.setTitle("Simulation");
		}
		@Override
		public void notify(Notification n) {
			if(n.getClass() == NewSimulationReportNotification.class){
				NewSimulationReportNotification test = (NewSimulationReportNotification) n;
				updateChart(test.getSimReport());
				this.clearAndLoadSpeedSliders(test.getSimReport().getSimFrames(), 1);
			}
			
		}
		
		private void refreshChart(){
			updateChart(this.lastSimReport);
		}
		
		/**
		 * parses a simulation into the graph. 
		 * @param simReport - to display
		 */
		private void updateChart(SimulationReport simReport) {
			this.lastSimReport = simReport;
			this.simResults = 
					ChartFactory.createXYLineChart(
							CHART_TITLE,
							X_AXIS_LABEL,
							Y_AXIS_LABEL, 
							null, //we'll add in all the values below so we can map to custom axis
							PlotOrientation.VERTICAL, true, true, false);
			final XYPlot plot = simResults.getXYPlot();
			
			if(this.showSpeed){
				DefaultXYDataset speedDataset = new DefaultXYDataset();
				speedDataset.addSeries("Speed", generateSpeedSeries(simReport.getSimFrames()));
				final NumberAxis axis2 = new NumberAxis("speed (km/h)");
				axis2.setAutoRangeIncludesZero(false);
				plot.setRangeAxis(1, axis2);
				plot.setDataset(1, speedDataset);
				plot.mapDatasetToRangeAxis(1, 1);
				final StandardXYItemRenderer renderer2 = new StandardXYItemRenderer();
				renderer2.setSeriesPaint(0, Color.black);
				//renderer2.setPlotShapes(true);
				plot.setRenderer(1, renderer2);
			}
			
			if(this.showStateOfCharge){
				DefaultXYDataset stateOfChargeDataSet = new DefaultXYDataset();
				stateOfChargeDataSet.addSeries("State Of Charge", generateStateOfChargeSeries(simReport.getSimFrames()));
				final NumberAxis axis3 = new NumberAxis("SoC (%)");
				axis3.setAutoRangeIncludesZero(false);
				plot.setRangeAxis(2, axis3);
				plot.setDataset(2, stateOfChargeDataSet);
				plot.mapDatasetToRangeAxis(2,2);
				final StandardXYItemRenderer renderer3 = new StandardXYItemRenderer();
				renderer3.setSeriesPaint(0, Color.blue);
				//renderer2.setPlotShapes(true);
				plot.setRenderer(2, renderer3);
			}
			
			if(this.showElevation){
				DefaultXYDataset terrainHeight = new DefaultXYDataset();
				terrainHeight.addSeries("Elevation", generateElevationProfile(simReport.getSimFrames()));
				final NumberAxis axis4 = new NumberAxis("height (m)");
				axis4.setAutoRangeIncludesZero(false);
				plot.setRangeAxis(3, axis4);
				plot.setDataset(3, terrainHeight);
				plot.mapDatasetToRangeAxis(3,3);
				final StandardXYItemRenderer renderer4 = new StandardXYItemRenderer();
				renderer4.setSeriesPaint(0, Color.green);
				//renderer2.setPlotShapes(true);
				plot.setRenderer(3, renderer4);
			}
			
			if(this.showCloud){
				DefaultXYDataset cloudiness = new DefaultXYDataset();
				cloudiness.addSeries("Cloud", generateCloudinessSeries(simReport.getSimFrames()));
				final NumberAxis axis5 = new NumberAxis("cloudiness (%)");
				axis5.setAutoRangeIncludesZero(false);
				plot.setRangeAxis(4, axis5);
				plot.setDataset(4, cloudiness);
				plot.mapDatasetToRangeAxis(4,4);
				final StandardXYItemRenderer renderer5 = new StandardXYItemRenderer();
				renderer5.setSeriesPaint(0, Color.RED);
				//renderer2.setPlotShapes(true);
				plot.setRenderer(4, renderer5);
			}
			
			
			this.mainDisplay.setChart(this.simResults);
			contentPane.repaint();
			contentPane.validate();
			mainDisplay.repaint();
			mainDisplay.validate();
			this.repaint();
			this.validate();	
		}
		
		
		private double[][] generateStateOfChargeSeries(List<SimFrame> simFrames) {
			double[][] toReturn= new double[2][simFrames.size()];
			//[0] is distance, [1] is speed
			toReturn[xValues][0] = 0;
			toReturn[yValues][0] = simFrames.get(0).getCarStatus().getStateOfCharge();
			
			double runningTotalDistance = 0;
			
			for(int i = 1; i<simFrames.size(); i++){
				SimFrame temp = simFrames.get(i);
				GeoCoord lastPosition = simFrames.get(i-1).getGPSReport().getLocation();
				GeoCoord thisPosition = temp.getGPSReport().getLocation();
				runningTotalDistance += lastPosition.calculateDistance(thisPosition);
				toReturn[xValues][i] = runningTotalDistance;
				toReturn[yValues][i] = temp.getCarStatus().getStateOfCharge();
			}
			
			return toReturn;
		}
		private double[][] generateSpeedSeries(List<SimFrame> simFrames) {
			double[][] toReturn= new double[2][simFrames.size()];
			//[0] is distance, [1] is speed
			toReturn[xValues][0] = 0;
			toReturn[yValues][0] = simFrames.get(0).getCarStatus().getSpeed();
			
			double runningTotalDistance = 0;
			
			for(int i = 1; i<simFrames.size(); i++){
				SimFrame temp = simFrames.get(i);
				GeoCoord lastPosition = simFrames.get(i-1).getGPSReport().getLocation();
				GeoCoord thisPosition = temp.getGPSReport().getLocation();
				runningTotalDistance += lastPosition.calculateDistance(thisPosition);
				toReturn[xValues][i] = runningTotalDistance;
				toReturn[yValues][i] = temp.getCarStatus().getSpeed();
			}
			
			return toReturn;
		}
		
		private double[][] generateElevationProfile(List<SimFrame> simFrames) {
			double[][] toReturn= new double[2][simFrames.size()];
			//[0] is distance, [1] is speed
			toReturn[xValues][0] = 0;
			toReturn[yValues][0] = simFrames.get(0).getGPSReport().getLocation().getElevation();
			
			double runningTotalDistance = 0;
			
			for(int i = 1; i<simFrames.size(); i++){
				SimFrame temp = simFrames.get(i);
				GeoCoord lastPosition = simFrames.get(i-1).getGPSReport().getLocation();
				GeoCoord thisPosition = temp.getGPSReport().getLocation();
				runningTotalDistance += lastPosition.calculateDistance(thisPosition);
				toReturn[xValues][i] = runningTotalDistance;
				toReturn[yValues][i] = temp.getGPSReport().getLocation().getElevation();
			}
			
			return toReturn;
		}
		
		private double[][] generateCloudinessSeries(List<SimFrame> simFrames) {
			double[][] toReturn= new double[2][simFrames.size()];
			//[0] is distance, [1] is speed
			toReturn[xValues][0] = 0;
			toReturn[yValues][0] = simFrames.get(0).getForecast().cloudCover();
			
			double runningTotalDistance = 0;
			
			for(int i = 1; i<simFrames.size(); i++){
				SimFrame temp = simFrames.get(i);
				GeoCoord lastPosition = simFrames.get(i-1).getGPSReport().getLocation();
				GeoCoord thisPosition = temp.getGPSReport().getLocation();
				runningTotalDistance += lastPosition.calculateDistance(thisPosition);
				toReturn[xValues][i] = runningTotalDistance;
				Double value = temp.getForecast().cloudCover()*100; //convert to %.
				toReturn[yValues][i] = value.intValue(); //to drop unneeded digits in scale
			}
			
			return toReturn;
		}
		
		@Override
		public void register() {
			mySession.register(this, NewSimulationReportNotification.class);
		}
}
