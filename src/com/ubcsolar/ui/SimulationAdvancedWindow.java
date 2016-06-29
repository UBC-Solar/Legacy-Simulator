package com.ubcsolar.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;

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
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.Main.GlobalValues;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.Listener;
import com.ubcsolar.common.SimFrame;
import com.ubcsolar.common.SimulationReport;
import com.ubcsolar.exception.NoCarStatusException;
import com.ubcsolar.exception.NoForecastReportException;
import com.ubcsolar.exception.NoLoadedRouteException;
import com.ubcsolar.exception.NoLocationReportedException;
import com.ubcsolar.notification.ExceptionNotification;
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
import java.awt.Toolkit;

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
	private GUImain parent; //TODO for loading frame
	private JFreeChart simResults; //the main chart model.
	private final String X_AXIS_LABEL = "Distance (km)";
	private final String Y_AXIS_LABEL = null;//"speed (km/h)"; // everything is in secondary axis
	private final int xValues = 0; //for the Double[][] dataset
	private final int yValues = 1; //for the Double[][] dataset
	private JPanel buttonPanel; 
	private ChartPanel mainDisplay; //the panel displaying the model
	private SimulationReport lastSimReport; //cache the last simReport
	
	
	private final int KM_PER_SLIDER = 5; //could make this dynamic to allow for slider 'zooming'
	private boolean showSpeed = true;
	private boolean showStateOfCharge = true;
	private boolean showCloud = true;
	private boolean showElevation = true;
	private JScrollPane speedSlidersPanel;
	private JTextField textField_1;
	private JPanel SliderHoldingPanel;
	private List<SliderSpinnerFrame> displayedSpeedSliderSpinners = new ArrayList<SliderSpinnerFrame>();

	
	private boolean showWelcomeMessageAgain = true;

	private static final String WelcomeInfoMessage = "use \"Load Forecasts for Route(48 hours)\" under the \"Forecasts\" menu to get the weather information."
			+"\n\n"+ "Note: You should Load the route before this.";

	private void handleError(String message){
		JOptionPane.showMessageDialog(this, message);
	}
	
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
	 * Create the frame.
	 * @param mySession 
	 * @param main 
	 */
	public SimulationAdvancedWindow(GlobalController mySession, GUImain main) { //TODO for loading frame
		this.parent = main;// TODO for loading Frame
		this.mySession = mySession;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 974, 780);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		buttonPanel = new JPanel();
		contentPane.add(buttonPanel, BorderLayout.NORTH);
		
		JButton btnNewSimulation = new JButton("New Simulation");
		btnNewSimulation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));// changing the cursor type
				JFrame frame = new LoadingWindow(mySession);
				frame.setVisible(true);
				
				runSimultion();
				
				frame.setVisible(false);
				contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				Toolkit.getDefaultToolkit().beep(); // simple alert for end of process
			
				mapChartNavigationTutorialDialog();

			}
		});
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		buttonPanel.add(btnNewSimulation);
		
		JCheckBox chckbxSpeed = new JCheckBox("Speed");
		chckbxSpeed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showSpeed = chckbxSpeed.isSelected();
				
				contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));// changing the cursor type
				JFrame frame = new LoadingWindow(mySession);
				frame.setVisible(true);
				refreshChart();
				frame.setVisible(false);
				contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});
		chckbxSpeed.setSelected(true);
		buttonPanel.add(chckbxSpeed);
		
		JCheckBox chckbxSoc = new JCheckBox("SoC");
		chckbxSoc.setSelected(true);
		chckbxSoc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showStateOfCharge = chckbxSoc.isSelected();
				
				contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));// changing the cursor type
				JFrame frame = new LoadingWindow(mySession);
				frame.setVisible(true);
				refreshChart();
				frame.setVisible(false);
				contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});
		buttonPanel.add(chckbxSoc);
		
		
		
		JCheckBox chckbxCloud = new JCheckBox("Cloud");
		chckbxCloud.setSelected(true);
		chckbxCloud.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCloud = chckbxCloud.isSelected();
				
				contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));// changing the cursor type
				JFrame frame = new LoadingWindow(mySession);
				frame.setVisible(true);
				refreshChart();
				frame.setVisible(false);
				contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});
		buttonPanel.add(chckbxCloud);
		
		JCheckBox chckbxElevation = new JCheckBox("Elevation");
		chckbxElevation.setSelected(true);
		chckbxElevation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showElevation = chckbxElevation.isSelected();

				contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));// changing the cursor type
				JFrame frame = new LoadingWindow(mySession);
				frame.setVisible(true);
				refreshChart();
				frame.setVisible(false);
				contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
		mainDisplay.setMouseZoomable(true);
		mainDisplay.setMouseWheelEnabled(true);
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
		
		JButton btnNewButton = new JButton("Reset Speeds");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clearManualSpeedSettings();
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 1;
		chartHoldingPanel.add(btnNewButton, gbc_btnNewButton);
		
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
		gbl_SliderHoldingPanel.rowHeights = new int[]{0, 0, 0};
		gbl_SliderHoldingPanel.columnWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_SliderHoldingPanel.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		SliderHoldingPanel.setLayout(gbl_SliderHoldingPanel);
		
		
		textField_1 = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 0, 5);
		gbc_textField_1.gridx = 0;
		gbc_textField_1.gridy = 2;
		SliderHoldingPanel.add(textField_1, gbc_textField_1);
		textField_1.setColumns(10);
		setDefaultChart();
		
		setTitleAndLogo();
		this.register();
		}

	
	/**
	 * To clear the manual speed settings. 
	 */
	protected void clearManualSpeedSettings() {
		if(this.lastSimReport.getSimFrames().size() == 0){
			this.clearAndLoadSpeedSliders(this.lastSimReport.getSimFrames(), KM_PER_SLIDER, new HashMap<GeoCoord, Double>(), 0.0);
			return;
		}
		double startDistance;
		try {
			startDistance = mySession.getMapController().findDistanceAlongLoadedRoute(this.lastSimReport.getSimFrames().get(0).getGPSReport().getLocation());
		} catch (NoLoadedRouteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			startDistance = 0.0;
		}
		this.clearAndLoadSpeedSliders(this.lastSimReport.getSimFrames(), KM_PER_SLIDER, new HashMap<GeoCoord, Double>(), startDistance );
	}

	private void clearAndLoadSpeedSliders(List<SimFrame> simResultValues, int KM_PER_SLIDER, Map<GeoCoord, Double> lastManuallyReqSpeeds, double startDistance) {
	
		SliderHoldingPanel.removeAll();
		SliderHoldingPanel.validate();
		SliderHoldingPanel.repaint();
		
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
		double runningTotalDistance= startDistance;
		int lastAddedPointIndex = 0;
		double lastAddedPointDistance = startDistance;
		
		//can't set the first speed anyway. 
		for(int i = 1; i<simResultValues.size(); i++){
			GeoCoord start = simResultValues.get(i-1).getGPSReport().getLocation();
			GeoCoord end = simResultValues.get(i).getGPSReport().getLocation();
			runningTotalDistance += start.calculateDistance(end);
			
			if((runningTotalDistance-lastAddedPointDistance)>KM_PER_SLIDER || i == simResultValues.size() - 1){
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
				boolean isManuallySet = false;
				for(GeoCoord g : pointsToRepresent){
					//not sure to do if any of them, or if majority, or if all, etc. 
					if(lastManuallyReqSpeeds.get(g)!=null){
						isManuallySet = true;
					}
				}
				
				SliderSpinnerFrame toAddToPanel = new SliderSpinnerFrame(label ,
										(int) averageSpeed, isManuallySet,pointsToRepresent);
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
			SliderHoldingPanel.validate();
			SliderHoldingPanel.repaint();
		}
		
		//this text box is just to force it to be bigger so the scroll bar doesn't cover anything. 
		textField_1 = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 0, 5);
		gbc_textField_1.gridx = 0;
		gbc_textField_1.gridy = 1;
		SliderHoldingPanel.add(textField_1, gbc_textField_1);
		textField_1.setColumns(5);
		
		
		this.repaint();
		this.SliderHoldingPanel.repaint();
		this.speedSlidersPanel.repaint();
		
	}
		
		/**
		 * Attempts to run a simulation with the loaded data. If there is needed data that
		 * is not loaded, displays an error message to end user. 
		 */
		protected void runSimultion() {
			Map<GeoCoord, Double> requestedSpeeds = generateRequestedSpeedMap();
			try {
				mySession.getMySimController().runSimulation(requestedSpeeds,1);
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
			
			XYPlot temp = simResults.getXYPlot();
			temp.clearRangeAxes();
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
			this.setIconImage(GlobalValues.iconImage.getImage());
			this.setTitle("Simulation");
		}
		@Override
		public void notify(Notification n) {
			if(n.getClass() == NewSimulationReportNotification.class){
				NewSimulationReportNotification test = (NewSimulationReportNotification) n;
				this.lastSimReport = test.getSimReport();
				
				double startDistance = 0.0;
				if(test.getSimReport().getSimFrames().size() > 0){
					try {
						startDistance = this.mySession.getMapController().findDistanceAlongLoadedRoute(test.getSimReport().getSimFrames().get(0).getGPSReport().getLocation());
					} catch (NoLoadedRouteException e) {
						mySession.sendNotification(new ExceptionNotification(e, "No loaded map when updating simulation"));
						e.printStackTrace();
						return;
					}
				}
				updateChart(test.getSimReport(), startDistance);
				this.clearAndLoadSpeedSliders(lastSimReport.getSimFrames(), KM_PER_SLIDER, lastSimReport.getManuallyRequestedSpeeds(), startDistance);
				this.repaint();
			}
			
		}
		
		private void refreshChart(){
			double startDistance = 0.0;
			this.mySession.getMapController().findClosestPointOnRoute(this.lastSimReport.getSimFrames().get(0).getGPSReport().getLocation());
			updateChart(this.lastSimReport, startDistance);
		}
		
		/**
		 * parses a simulation into the graph. 
		 * @param simReport - to display
		 * @param startDistance 
		 */
		private void updateChart(SimulationReport simReport, double startDistance) {
			this.lastSimReport = simReport;
			if(simReport.getSimFrames().size() == 0){
				this.setDefaultChart(); //last sim was deleted.
				this.mainDisplay.setChart(this.simResults);
				clearAndLoadSpeedSliders(new ArrayList<SimFrame>(),KM_PER_SLIDER,new HashMap<GeoCoord, Double>(), startDistance);
				
				contentPane.repaint();
				contentPane.validate();
				mainDisplay.repaint();
				mainDisplay.validate();
				this.repaint();
				this.validate();	
				this.SliderHoldingPanel.validate();
				this.SliderHoldingPanel.repaint();
				
				return;
			}
			this.simResults = 
					ChartFactory.createXYLineChart(
							CHART_TITLE,
							X_AXIS_LABEL,
							Y_AXIS_LABEL,
							null, //we'll add in all the values below so we can map to custom axis
							PlotOrientation.VERTICAL, true, true, false);
			final XYPlot plot = simResults.getXYPlot();
			plot.setRangePannable(true);
			plot.setDomainPannable(true);
			plot.clearRangeAxes();
			
			if(this.showSpeed){
				DefaultXYDataset speedDataset = new DefaultXYDataset();
				speedDataset.addSeries("Speed", generateSpeedSeries(simReport.getSimFrames(), startDistance));
				final NumberAxis axis2 = new NumberAxis("speed (km/h)");
				axis2.setAutoRangeIncludesZero(false);
				plot.setRangeAxis(1, axis2);
				plot.setDataset(1, speedDataset);
				plot.mapDatasetToRangeAxis(1, 1);
				final StandardXYItemRenderer renderer2 = new StandardXYItemRenderer();
				renderer2.setSeriesPaint(0, Color.black);
				//renderer.setBaseLegendTextFont(new Font("Helvetica", Font.BOLD, 11));
				renderer2.setSeriesStroke(0, new BasicStroke(2));
				//renderer2.setPlotShapes(true);
				plot.setRenderer(1, renderer2);
			}
			
			if(this.showStateOfCharge){
				DefaultXYDataset stateOfChargeDataSet = new DefaultXYDataset();
				stateOfChargeDataSet.addSeries("State Of Charge", generateStateOfChargeSeries(simReport.getSimFrames(),startDistance));
				final NumberAxis axis3 = new NumberAxis("SoC (%)");
				axis3.setAutoRangeIncludesZero(false);
				plot.setRangeAxis(2, axis3);
				plot.setDataset(2, stateOfChargeDataSet);
				plot.mapDatasetToRangeAxis(2,2);
				final StandardXYItemRenderer renderer3 = new StandardXYItemRenderer();
				renderer3.setSeriesPaint(0, Color.blue);
				renderer3.setSeriesStroke(0, new BasicStroke(2));
				//renderer2.setPlotShapes(true);
				plot.setRenderer(2, renderer3);
			}
			
			if(this.showElevation){
				DefaultXYDataset terrainHeight = new DefaultXYDataset();
				terrainHeight.addSeries("Elevation", generateElevationProfile(simReport.getSimFrames(), startDistance));
				final NumberAxis axis4 = new NumberAxis("height (m)");
				axis4.setAutoRangeIncludesZero(false);
				plot.setRangeAxis(3, axis4);
				plot.setDataset(3, terrainHeight);
				plot.mapDatasetToRangeAxis(3,3);
				final StandardXYItemRenderer renderer4 = new StandardXYItemRenderer();
				renderer4.setSeriesPaint(0, Color.green);
				renderer4.setSeriesStroke(0, new BasicStroke(2));
				//renderer2.setPlotShapes(true);
				plot.setRenderer(3, renderer4);
			}
			
			if(this.showCloud){
				DefaultXYDataset cloudiness = new DefaultXYDataset();
				cloudiness.addSeries("Cloud", generateCloudinessSeries(simReport.getSimFrames(), startDistance));
				final NumberAxis axis5 = new NumberAxis("cloudiness (%)");
				axis5.setAutoRangeIncludesZero(false);
				plot.setRangeAxis(4, axis5);
				plot.setDataset(4, cloudiness);
				plot.mapDatasetToRangeAxis(4,4);
				final StandardXYItemRenderer renderer5 = new StandardXYItemRenderer();
				renderer5.setSeriesPaint(0, Color.RED);
				renderer5.setSeriesStroke(0, new BasicStroke(2));
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
		
		
		private double[][] generateStateOfChargeSeries(List<SimFrame> simFrames, double startDistance) {
			double[][] toReturn= new double[2][simFrames.size()];
			//[0] is distance, [1] is speed
			toReturn[xValues][0] = startDistance;
			toReturn[yValues][0] = simFrames.get(0).getCarStatus().getStateOfCharge();
			
			double runningTotalDistance = startDistance;
			
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
		private double[][] generateSpeedSeries(List<SimFrame> simFrames, double startDistance) {
			double[][] toReturn= new double[2][simFrames.size()];
			//[0] is distance, [1] is speed
			toReturn[xValues][0] = startDistance;
			toReturn[yValues][0] = simFrames.get(0).getCarStatus().getSpeed();
			
			double runningTotalDistance = startDistance;
			
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
		
		private double[][] generateElevationProfile(List<SimFrame> simFrames, double startDistance) {
			double[][] toReturn= new double[2][simFrames.size()];
			//[0] is distance, [1] is speed
			toReturn[xValues][0] = startDistance;
			toReturn[yValues][0] = simFrames.get(0).getGPSReport().getLocation().getElevation();
			
			double runningTotalDistance = startDistance;
			
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
		
		private double[][] generateCloudinessSeries(List<SimFrame> simFrames, double startDistance) {
			double[][] toReturn= new double[2][simFrames.size()];
			//[0] is distance, [1] is speed
			toReturn[xValues][0] = startDistance;
			toReturn[yValues][0] = simFrames.get(0).getForecast().cloudCover();
			
			double runningTotalDistance = startDistance;
			
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
