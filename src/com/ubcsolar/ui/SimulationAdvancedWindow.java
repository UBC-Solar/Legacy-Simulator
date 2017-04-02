package com.ubcsolar.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;

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
import java.util.Calendar;
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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class SimulationAdvancedWindow extends JFrame implements Listener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2684909507474196406L;
	private JPanel contentPane; //the root content holder
	private GlobalController mySession; 
	private GUImain parent; //TODO for loading frame
	private JFreeChart simResults; //the main chart model.
	private final String X_AXIS_LABEL = "Distance (km)";
	private final String Y_AXIS_LABEL = null;//"speed (km/h)"; // everything is in secondary axis
	private final int xValues = 0; //for the Double[][] dataset
	private final int yValues = 1; //for the Double[][] dataset
	private JPanel buttonPanel; 
	private final int MAX_NUM_OF_LAPS=30;//the max number of laps to put in the combo box. 30 picked arbitrarily
	private ChartPanel SpeedChartPanel; //the panel displaying the model
	private JFreeChart SpeedChart;
	private ChartPanel SoCChartPanel;
	private JFreeChart SoCChart;
	private SimulationReport lastSimReport; //cache the last simReport
	
	
	private final int KM_PER_SLIDER = 1; //could make this dynamic to allow for slider 'zooming'
	private final double KM_PER_SLIDER_DOUBLE = 0.3;
	private boolean showSpeed = true;
	private boolean showStateOfCharge = true;
	private boolean showCloud = true;
	private boolean showElevation = true;
	private boolean showTime = true;
	//private List<SliderSpinnerFrame> displayedSpeedSliderSpinners = new ArrayList<SliderSpinnerFrame>();

	
	private boolean showWelcomeMessageAgain = true;

	private static final String WelcomeInfoMessage = "use \"Load Forecasts for Route(48 hours)\" under the \"Forecasts\" menu to get the weather information."
			+"\n\n"+ "Note: You should Load the route before this.";
	private JComboBox<Integer> lapSelectComboBox;
	private JCheckBox checkBoxTime;
	private JLabel lblTotalTimeTaken;
	private JPanel chooseStartTimePanel;
	private JLabel lblDesiredSimStart;
	private JLabel lblHour;
	private JSpinner hourSpinner;

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
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{30, 0, 0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		//contentPane.setLayout(new BorderLayout(0, 0));
		
		buttonPanel = new JPanel();
		GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
		gbc_buttonPanel.gridx = 0;
		gbc_buttonPanel.gridy = 0;
		gbc_buttonPanel.insets = new Insets(0, 0, 5, 5);
		contentPane.add(buttonPanel, gbc_buttonPanel);
		
		JButton btnNewSimulation = new JButton("New Simulation");
		btnNewSimulation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));// changing the cursor type
				JFrame frame = new LoadingWindow(mySession);
				frame.setVisible(true);
				
				runSimulation();
				
				frame.setVisible(false);
				contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				Toolkit.getDefaultToolkit().beep(); // simple alert for end of process
			
				mapChartNavigationTutorialDialog();

			}
		});
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		buttonPanel.add(btnNewSimulation);
		
		/*JCheckBox chckbxSpeed = new JCheckBox("Speed");
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
		
		checkBoxTime = new JCheckBox("Time");
		checkBoxTime.setSelected(showTime);
		checkBoxTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showTime = checkBoxTime.isSelected();

				contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));// changing the cursor type
				JFrame frame = new LoadingWindow(mySession);
				frame.setVisible(true);
				refreshChart();
				frame.setVisible(false);
				contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});
		buttonPanel.add(checkBoxTime);*/
		
		lapSelectComboBox = new JComboBox<Integer>();
		lapSelectComboBox.setPreferredSize(new Dimension(39, 18));
		lapSelectComboBox.setEditable(true);
		for(int i = 1; i<=MAX_NUM_OF_LAPS; i++){
			lapSelectComboBox.addItem(new Integer(i));
		}
		
		lapSelectComboBox.setSelectedIndex(0);
		buttonPanel.add(lapSelectComboBox);
		
		JLabel lblOfLaps = new JLabel("# Of Laps: ");
		buttonPanel.add(lblOfLaps);
		
		lblTotalTimeTaken = new JLabel("Total Time Taken");
		buttonPanel.add(lblTotalTimeTaken);
		
//		JPanel SpeedChartHoldingPanel = new JPanel();
//		contentPane.add(SpeedChartHoldingPanel, BorderLayout.CENTER);
//		GridBagLayout gbl_SpeedChartHoldingPanel = new GridBagLayout();
//		gbl_SpeedChartHoldingPanel.columnWidths = new int[]{0, 0, 0};
//		gbl_SpeedChartHoldingPanel.rowHeights = new int[] {271, 0, 0};
//		gbl_SpeedChartHoldingPanel.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
//		gbl_SpeedChartHoldingPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
//		SpeedChartHoldingPanel.setLayout(gbl_SpeedChartHoldingPanel);
		
		JFreeChart SpeedChart = createChart(createBlankDataset(), "Speed");
		
		chooseStartTimePanel = new JPanel();
		GridBagConstraints gbc_chooseStartTimePanel = new GridBagConstraints();
		gbc_chooseStartTimePanel.insets = new Insets(0, 0, 5, 5);
		gbc_chooseStartTimePanel.fill = GridBagConstraints.BOTH;
		gbc_chooseStartTimePanel.gridx = 0;
		gbc_chooseStartTimePanel.gridy = 1;
		contentPane.add(chooseStartTimePanel, gbc_chooseStartTimePanel);
		
		lblDesiredSimStart = new JLabel("Desired sim start time:");
		chooseStartTimePanel.add(lblDesiredSimStart);
		
		hourSpinner = new JSpinner();
		hourSpinner.setModel(new SpinnerNumberModel(0, 0, 47, 1));
		chooseStartTimePanel.add(hourSpinner);
		
		lblHour = new JLabel("hours from now");
		chooseStartTimePanel.add(lblHour);
		SpeedChartPanel = new ChartPanel(SpeedChart);
		SpeedChartPanel.setMouseZoomable(true);
		SpeedChartPanel.setMouseWheelEnabled(true);
		//SpeedChartPanel.setName("Speed");
		//buildChart(createBlankDataset(), SpeedChartPanel);
		GridBagConstraints gbc_SpeedChartPanel = new GridBagConstraints();
		gbc_SpeedChartPanel.weighty = 10.0;
		gbc_SpeedChartPanel.insets = new Insets(0, 0, 5, 5);
		gbc_SpeedChartPanel.fill = GridBagConstraints.BOTH;
		gbc_SpeedChartPanel.gridx = 0;
		gbc_SpeedChartPanel.gridy = 2;
		contentPane.add(SpeedChartPanel, gbc_SpeedChartPanel);
		
//		JPanel SoCChartHoldingPanel = new JPanel();
//		contentPane.add(SoCChartHoldingPanel, BorderLayout.SOUTH);
//		GridBagLayout gbl_SoCChartHoldingPanel = new GridBagLayout();
//		gbl_SoCChartHoldingPanel.columnWidths = new int[]{0, 0, 0};
//		gbl_SoCChartHoldingPanel.rowHeights = new int[] {271, 0, 0};
//		gbl_SoCChartHoldingPanel.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
//		gbl_SoCChartHoldingPanel.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
//		SoCChartHoldingPanel.setLayout(gbl_SoCChartHoldingPanel);
		
		JFreeChart SoCChart = createChart(createBlankDataset(), "State of Charge");
		SoCChartPanel = new ChartPanel(SoCChart);
		SoCChartPanel.setMouseZoomable(true);
		SoCChartPanel.setMouseWheelEnabled(true);
		//SoCChartPanel.setName("State of Charge");
		//buildChart(createBlankDataset(), SoCChartPanel);
		GridBagConstraints gbc_SoCChartPanel = new GridBagConstraints();
		gbc_SoCChartPanel.weighty = 10.0;
		gbc_SoCChartPanel.insets = new Insets(0, 0, 5, 5);
		gbc_SoCChartPanel.fill = GridBagConstraints.BOTH;
		gbc_SoCChartPanel.gridx = 0;
		gbc_SoCChartPanel.gridy = 3;
		contentPane.add(SoCChartPanel, gbc_SoCChartPanel);
		
		setTitleAndLogo();
		this.register();
		}
		
		/**
		 * Attempts to run a simulation with the loaded data. If there is needed data that
		 * is not loaded, displays an error message to end user. 
		 */
		protected void runSimulation() {
			int numLaps = lapSelectComboBox.getSelectedIndex() +1; //0 based index.
			Map<GeoCoord,Map<Integer, Double>> requestedSpeeds = generateRequestedSpeedMap();
			long startTimeMillis = getStartTimeInMillis();
			try {
				mySession.getMySimController().runSimulation(requestedSpeeds, numLaps, startTimeMillis);
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
		
		private long getStartTimeInMillis(){
			Calendar theCalendar = Calendar.getInstance();
			int selectedMonth;
			switch((String)monthComboBox.getSelectedItem()){
			case "January": 
				selectedMonth = Calendar.JANUARY;
				break; 
			case "February":
				selectedMonth = Calendar.FEBRUARY;
				break;
			case "March":
				selectedMonth = Calendar.MARCH;
				break;
			case "April":
				selectedMonth = Calendar.APRIL;
				break;
			case "May":
				selectedMonth = Calendar.MAY;
				break;
			case "June":
				selectedMonth = Calendar.JUNE;
				break;
			case "July":
				selectedMonth = Calendar.JULY;
				break;
			case "August":
				selectedMonth = Calendar.AUGUST;
				break;
			case "September":
				selectedMonth = Calendar.SEPTEMBER;
				break;
			case "October":
				selectedMonth = Calendar.OCTOBER;
				break;
			case "November":
				selectedMonth = Calendar.NOVEMBER;
				break;
			case "December":
				selectedMonth = Calendar.DECEMBER;
				break;
			default:
				selectedMonth = theCalendar.get(Calendar.MONTH);
				break;
			}
			theCalendar.set(theCalendar.get(Calendar.YEAR), selectedMonth, (Integer)dateSpinner.getValue(), 
					(Integer)hourSpinner.getValue(), (Integer)minuteSpinner.getValue());
			return theCalendar.getTimeInMillis();
		}
		
		private Map<GeoCoord,Map<Integer, Double>> generateRequestedSpeedMap() {
			HashMap<GeoCoord,Map<Integer, Double>> toReturn = new HashMap<GeoCoord, Map<Integer,Double>>();
			/*Random rng = new Random();
			if(rng.nextBoolean()){
				for(SimFrame g : this.lastSimReport.getSimFrames()){
					toReturn.put(g.getGPSReport().getLocation(), 25.0);
				}
			}*/ //used this for testing. 
			
			/*for(SliderSpinnerFrame f : this.displayedSpeedSliderSpinners){
				if(f.isManuallySet()){
					int lapNumber = f.getLapNumber();
					for(GeoCoord g : f.getRepresentedCoordinates()){
						if(toReturn.get(g) == null){
							Map<Integer, Double> lapAndSpeed = new HashMap<Integer,Double>();
							lapAndSpeed.put(lapNumber, f.getValue()+0.0);
							toReturn.put(g,lapAndSpeed);
						}
						else{
							toReturn.get(g).put(lapNumber, f.getValue()+0.0); //the '+0.0' is to make it a Double.
						}
						 
					}
					System.out.println("Manually Requesting speed to: " + f.getValue());
				}
			}*/
			
			return toReturn;
		}

		/**
		 * Builds a default, empty chart. 
		 */
		private void setDefaultChart() {
			XYDataset ds = createBlankDataset();
			this.simResults = 
					ChartFactory.createXYLineChart(
							"Default Chart",
							X_AXIS_LABEL,
							Y_AXIS_LABEL, 
							ds,
							PlotOrientation.VERTICAL, true, true, false);
			
			XYPlot temp = simResults.getXYPlot();
			temp.clearRangeAxes();
		}
		
		private JFreeChart createChart(XYDataset ds, String chartName) {
			JFreeChart chart = 
					ChartFactory.createXYLineChart(
							chartName,
							X_AXIS_LABEL,
							Y_AXIS_LABEL, 
							ds,
							PlotOrientation.VERTICAL, true, true, false);
			
			XYPlot temp = null;
			switch (chartName) {
			case "Speed": 
				if(SpeedChart != null)
					temp = SpeedChart.getXYPlot();
				break;
			case "State of Charge": 
				if(SoCChart != null)
					temp = SoCChart.getXYPlot();
				break;
			default: temp = null;
			}
			
			if (temp != null)
				temp.clearRangeAxes();
			
			return chart;
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
		
				int totalFrames = lastSimReport.getSimFrames().size();
				if(totalFrames>0){
					int maxLap = lastSimReport.getSimFrames().get(totalFrames-1).getLapNumber();
					this.lapSelectComboBox.getModel().setSelectedItem(maxLap); 
				}
						
				
				updateChart(test.getSimReport(), startDistance);
				this.validate();
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
			System.out.println("Time Taken: " + getTotalTimeTaken(simReport.getSimFrames()) + " minutes");
			
			// Rounded off to 2 decimal places
			double roundedTime = Math.round(getTotalTimeTaken(simReport.getSimFrames())*100.0)/100.0;
			lblTotalTimeTaken.setText("Total Time Taken: " + roundedTime + " minutes");
			
			if(simReport.getSimFrames().size() == 0){
				//this.setDefaultChart(); //last sim was deleted.
				SpeedChart = createChart(createBlankDataset(), "Speed");
				SoCChart = createChart(createBlankDataset(), "State of Charge");
				this.SpeedChartPanel.setChart(SpeedChart);
				this.SoCChartPanel.setChart(SoCChart);
				
				contentPane.repaint();
				contentPane.validate();
				SpeedChartPanel.repaint();
				SoCChartPanel.repaint();
				SpeedChartPanel.validate();
				SoCChartPanel.validate();
				buttonPanel.repaint();
				buttonPanel.validate();
				this.repaint();
				this.validate();	
				return;
			}
			this.simResults = 
					ChartFactory.createXYLineChart(
							"Dummy Chart",
							X_AXIS_LABEL,
							Y_AXIS_LABEL,
							null, //we'll add in all the values below so we can map to custom axis
							PlotOrientation.VERTICAL, true, true, false);
			final XYPlot plot = simResults.getXYPlot();
			plot.setRangePannable(true);
			plot.setDomainPannable(true);
			plot.clearRangeAxes();
			
			SpeedChart = createChart(createBlankDataset(), "Speed");
			XYPlot SpeedPlot = SpeedChart.getXYPlot();
			SpeedPlot.setRangePannable(true);
			SpeedPlot.setDomainPannable(true);
			SpeedPlot.clearRangeAxes();
			
			SoCChart = createChart(createBlankDataset(), "State of Charge");
			XYPlot SoCPlot = SoCChart.getXYPlot();
			SoCPlot.setRangePannable(true);
			SoCPlot.setDomainPannable(true);
			SoCPlot.clearRangeAxes();
			
			if(this.showSpeed){
				DefaultXYDataset speedDataset = new DefaultXYDataset();
				speedDataset.addSeries("Speed", generateSpeedSeries(simReport.getSimFrames(), startDistance));
				final NumberAxis axis2 = new NumberAxis("speed (km/h)");
				axis2.setAutoRangeIncludesZero(false);
				SpeedPlot.setRangeAxis(0, axis2);
				SpeedPlot.setDataset(0, speedDataset);
				SpeedPlot.mapDatasetToRangeAxis(0, 0);
				final StandardXYItemRenderer renderer2 = new StandardXYItemRenderer();
				renderer2.setSeriesPaint(0, Color.black);
				//renderer.setBaseLegendTextFont(new Font("Helvetica", Font.BOLD, 11));
				renderer2.setSeriesStroke(0, new BasicStroke(2)); // The parameter in new BasicStroke specifies the thickness
				//renderer2.setPlotShapes(true);
				SpeedPlot.setRenderer(0, renderer2);
			}
		
			if(this.showStateOfCharge){
				DefaultXYDataset stateOfChargeDataSet = new DefaultXYDataset();
				stateOfChargeDataSet.addSeries("State Of Charge", generateStateOfChargeSeries(simReport.getSimFrames(),startDistance));
				final NumberAxis axis3 = new NumberAxis("SoC (%)");
				axis3.setAutoRangeIncludesZero(false);
				SoCPlot.setRangeAxis(0, axis3);
				SoCPlot.setDataset(0, stateOfChargeDataSet);
				SoCPlot.mapDatasetToRangeAxis(0,0);
				final StandardXYItemRenderer renderer3 = new StandardXYItemRenderer();
				renderer3.setSeriesPaint(0, Color.blue);
				renderer3.setSeriesStroke(0, new BasicStroke(2));
				//renderer2.setPlotShapes(true);
				SoCPlot.setRenderer(0, renderer3);
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
			
			if(this.showTime){ //should make a 'time series' checkbox
				DefaultXYDataset timeDataset = new DefaultXYDataset();
				timeDataset.addSeries("Time", generateTimeSeries(simReport.getSimFrames(), startDistance));
				final NumberAxis axis5 = new NumberAxis("time (min)");
				axis5.setAutoRangeIncludesZero(false);
				plot.setRangeAxis(5, axis5);
				plot.setDataset(5, timeDataset);
				plot.mapDatasetToRangeAxis(5, 5);
				final StandardXYItemRenderer renderer5 = new StandardXYItemRenderer();
				renderer5.setSeriesPaint(0, Color.WHITE);
				//renderer.setBaseLegendTextFont(new Font("Helvetica", Font.BOLD, 11));
				renderer5.setSeriesStroke(0, new BasicStroke(2));
				//renderer2.setPlotShapes(true);
				plot.setRenderer(5, renderer5);
			}
			
			this.SpeedChartPanel.setChart(SpeedChart);
			this.SoCChartPanel.setChart(SoCChart);
			contentPane.repaint();
			contentPane.validate();
			SpeedChartPanel.repaint();
			SoCChartPanel.repaint();
			SpeedChartPanel.validate();
			SoCChartPanel.validate();
			buttonPanel.repaint();
			buttonPanel.validate();
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
		
		private double[][] generateTimeSeries(List<SimFrame> simFrames, double startDistance) {
			double[][] toReturn= new double[2][simFrames.size()];
			//[0] is distance, [1] is speed
			double startTime = simFrames.get(0).getRepresentedTime();
			toReturn[xValues][0] = startDistance;
			toReturn[yValues][0] = 0;
			
			double runningTotalDistance = startDistance;
			
			for(int i = 1; i<simFrames.size(); i++){
				SimFrame temp = simFrames.get(i);
				GeoCoord lastPosition = simFrames.get(i-1).getGPSReport().getLocation();
				GeoCoord thisPosition = temp.getGPSReport().getLocation();
				runningTotalDistance += lastPosition.calculateDistance(thisPosition);
				toReturn[xValues][i] = runningTotalDistance;
				toReturn[yValues][i] = (((temp.getRepresentedTime() - startTime)/1000)/60);
			}
			
			return toReturn;
		}
		
		private double getTotalTimeTaken(List<SimFrame> simFrames) {
			double timeTaken = 0;
			double startTime = 0;
			double endTime = 0;
			if(simFrames!=null && simFrames.size() > 0)
			{
				startTime = simFrames.get(1).getRepresentedTime();			
				endTime = simFrames.get((simFrames.size()-1)).getRepresentedTime();
			}
			timeTaken = ((endTime - startTime)/1000)/60;
			return timeTaken;
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
