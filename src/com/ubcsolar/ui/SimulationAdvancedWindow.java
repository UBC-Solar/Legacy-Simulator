package com.ubcsolar.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.common.DistanceUnit;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.Listener;
import com.ubcsolar.common.LogType;
import com.ubcsolar.common.SimFrame;
import com.ubcsolar.common.SimulationReport;
import com.ubcsolar.common.SolarLog;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.event.ActionEvent;
import java.awt.Insets;

public class SimulationAdvancedWindow extends JFrame implements Listener{

	private static final String CHART_TITLE = "Sim Results";
	private JPanel contentPane;
	private GlobalController mySession;
	private JFreeChart simResults;
	private final String X_AXIS_LABEL = "Distance (km)";
	private final String Y_AXIS_LABEL = "speed (km/h)";
	private JPanel buttonPanel;
	private ChartPanel mainDisplay;
	
	/**
	 * Launch the application.
	 *//*//don't need a Main() here.
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Simulation frame = new Simulation();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

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
		setBounds(100, 100, 450, 400);
		
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
		buttonPanel.add(btnNewSimulation);
		
		JPanel chartHoldingPanel = new JPanel();
		contentPane.add(chartHoldingPanel, BorderLayout.CENTER);
		GridBagLayout gbl_chartHoldingPanel = new GridBagLayout();
		gbl_chartHoldingPanel.columnWidths = new int[]{0, 0, 0};
		gbl_chartHoldingPanel.rowHeights = new int[]{0, 0, 0};
		gbl_chartHoldingPanel.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_chartHoldingPanel.rowWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		chartHoldingPanel.setLayout(gbl_chartHoldingPanel);
		
		setDefaultChart();
		mainDisplay = new ChartPanel(simResults);
		GridBagConstraints gbc_mainDisplay = new GridBagConstraints();
		gbc_mainDisplay.weighty = 2.0;
		gbc_mainDisplay.weightx = 2.0;
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
		
		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 0, 5);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 1;
		chartHoldingPanel.add(panel_2, gbc_panel_2);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 1;
		chartHoldingPanel.add(panel, gbc_panel);
		
		setDefaultChart();
		
		setTitleAndLogo();
		this.register();
		}
		
		protected void runSimultion() {
		
		try {
			mySession.getMySimController().runSimulation(new HashMap<GeoCoord,Double>());
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
			}
			
		}
		
		
		private void updateChart(SimulationReport simReport) {
			DefaultXYDataset dds = new DefaultXYDataset();
			double[][] speedSeries = generateSpeedSeries(simReport.getSimFrames());
			dds.addSeries("Speed", speedSeries);
			double[][] stateOfChargeSeries = generateStateOfChargeSeries(simReport.getSimFrames());
			dds.addSeries("StateOfCharge", stateOfChargeSeries);
			
			this.simResults = 
					ChartFactory.createXYLineChart(
							CHART_TITLE,
							X_AXIS_LABEL,
							Y_AXIS_LABEL, 
							dds,
							PlotOrientation.VERTICAL, true, true, false);
			
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
			toReturn[0][0] = 0;
			toReturn[1][0] = simFrames.get(0).getCarStatus().getStateOfCharge();
			double runningTotalDistance = 0;
			for(int i = 1; i<simFrames.size(); i++){
				SimFrame temp = simFrames.get(i);
				GeoCoord lastPosition = simFrames.get(i-1).getGPSReport().getLocation();
				GeoCoord thisPosition = temp.getGPSReport().getLocation();
				runningTotalDistance += lastPosition.calculateDistance(thisPosition, DistanceUnit.KILOMETERS);
				toReturn[0][i] = runningTotalDistance;
				toReturn[1][i] = temp.getCarStatus().getStateOfCharge();
				System.out.println(temp.getCarStatus().getStateOfCharge());
			}
			
			return toReturn;
		}
		private double[][] generateSpeedSeries(List<SimFrame> simFrames) {
			double[][] toReturn= new double[2][simFrames.size()];
			//[0] is distance, [1] is speed
			toReturn[0][0] = 0;
			toReturn[1][0] = simFrames.get(0).getCarStatus().getSpeed();
			double runningTotalDistance = 0;
			for(int i = 1; i<simFrames.size(); i++){
				SimFrame temp = simFrames.get(i);
				GeoCoord lastPosition = simFrames.get(i-1).getGPSReport().getLocation();
				GeoCoord thisPosition = temp.getGPSReport().getLocation();
				runningTotalDistance += lastPosition.calculateDistance(thisPosition, DistanceUnit.KILOMETERS);
				toReturn[0][i] = runningTotalDistance;
				toReturn[1][i] = temp.getCarStatus().getSpeed();
			}
			
			return toReturn;
		}
		
		
		@Override
		public void register() {
			mySession.register(this, NewSimulationReportNotification.class);
		}

}
