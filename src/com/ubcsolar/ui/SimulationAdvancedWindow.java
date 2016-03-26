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
	private ChartPanel chartFrame;
	
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
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JButton btnNewSimulation = new JButton("New Simulation");
		btnNewSimulation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					mySession.getMySimController().runSimulation(new HashMap<GeoCoord,Double>());
				} catch (NoForecastReportException e1) {
					handleError("No Forcecast Loaded yet");
					SolarLog.write(LogType.ERROR, System.currentTimeMillis(), "Tried to No Forecast loaded");
				} catch (NoLoadedRouteException e1) {
					handleError("No Route Loaded yet");
					SolarLog.write(LogType.ERROR, System.currentTimeMillis(), "Tried to run a sim with no route loaded");
				} catch (NoLocationReportedException e1) {
					handleError("No Location Reported yet");
					SolarLog.write(LogType.ERROR, System.currentTimeMillis(), "Tried to run a sim with no location reported yet");
				} catch (NoCarStatusException e1) {
					handleError("No Car Status Reported yet");
					SolarLog.write(LogType.ERROR, System.currentTimeMillis(), "Tried to run a sim with no car status reported yet");
				}
			}
		});
		
		
		GridBagConstraints gbc_btnNewSimulation = new GridBagConstraints();
		gbc_btnNewSimulation.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewSimulation.gridx = 0;
		gbc_btnNewSimulation.gridy = 0;
		contentPane.add(btnNewSimulation, gbc_btnNewSimulation);
		
		setDefaultChart();
		this.chartFrame = new ChartPanel(simResults);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		contentPane.add(chartFrame, gbc_panel);
		
		setTitleAndLogo();
		this.register();
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
			double[][] data = new double[2][simReport.getSimFrames().size()];
			//[0] is distance, [1] is speed
			data[0][0] = 0;
			data[1][0] = simReport.getSimFrames().get(0).getCarStatus().getSpeed();
			double runningTotalDistance = 0;
			for(int i = 1; i<simReport.getSimFrames().size(); i++){
				SimFrame temp = simReport.getSimFrames().get(i);
				GeoCoord lastPosition = simReport.getSimFrames().get(i-1).getGPSReport().getLocation();
				GeoCoord thisPosition = temp.getGPSReport().getLocation();
				runningTotalDistance += lastPosition.calculateDistance(thisPosition, DistanceUnit.KILOMETERS);
				data[0][i] = runningTotalDistance;
				data[1][i] = temp.getCarStatus().getSpeed();
			}
			
			dds.addSeries("Sim Results", data);
			
			this.simResults = 
					ChartFactory.createXYLineChart(
							CHART_TITLE,
							X_AXIS_LABEL,
							Y_AXIS_LABEL, 
							dds,
							PlotOrientation.VERTICAL, true, true, false);
			
			this.chartFrame.setChart(this.simResults);
			contentPane.repaint();
			contentPane.validate();
			chartFrame.repaint();
			chartFrame.validate();
			this.repaint();
			this.validate();
			
		}
		@Override
		public void register() {
			mySession.register(this, NewSimulationReportNotification.class);
		}

}
