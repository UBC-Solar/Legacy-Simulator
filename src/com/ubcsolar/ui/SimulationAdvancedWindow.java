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
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.LogType;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.exception.NoCarStatusException;
import com.ubcsolar.exception.NoForecastReportException;
import com.ubcsolar.exception.NoLoadedRouteException;
import com.ubcsolar.exception.NoLocationReportedException;

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

public class SimulationAdvancedWindow extends JFrame {

	private JPanel contentPane;
	private GlobalController mySession;
	private JFreeChart simResults;
	private final String X_AXIS_LABEL = "Distance (km)";
	private final String Y_AXIS_LABEL = "speed (km/h)";
	
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
		ChartPanel displaySimPane = new ChartPanel(simResults);
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		contentPane.add(displaySimPane, gbc_panel);
		
		setTitleAndLogo();
		}
		
		private void setDefaultChart() {
			XYDataset ds = createBlankDataset();
			this.simResults = 
					ChartFactory.createXYLineChart(
							"Height Chart",
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

}
