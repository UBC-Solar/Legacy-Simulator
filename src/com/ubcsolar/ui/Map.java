/**
 * this Frame is the advanced window for the map. 
 * Will show advanced settings as well as advanced information that doesn't belong on the main window. 
 * Allows a user to load a new map
 */
package com.ubcsolar.ui;

import com.ubcsolar.common.DistanceUnit;
import com.ubcsolar.common.Listener;
import com.ubcsolar.map.*;
import com.ubcsolar.notification.NewMapLoadedNotification;
import com.ubcsolar.notification.Notification;
import com.ubcsolar.notification.RouteDataAsRequestedNotification;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JEditorPane;
import javax.swing.JButton;
import javax.xml.parsers.ParserConfigurationException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.xml.sax.SAXException;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
 



public class Map extends JFrame implements Listener {

	private JPanel contentPane;
	private GlobalController mySession;
	private JLabel lblMapName;
	private JButton btnRefreshMapName;
	private XYDataset ds;
	private JFreeChart elevationChart;
	private ChartPanel cp;

	/**
	 * update the map name label
	 * @param labelupdate - what to make the label display
	 */
	public void labelUpdate(String labelupdate) {
		lblMapName.setText(labelupdate);
	}
	
	
	
	/**
	 * All notifications that this class has registered for will come here
	 */
	@Override
	public void notify(Notification n){
		//TODO add any notifications here
		
		//A new map has been loaded into the program
		if(n.getClass() == NewMapLoadedNotification.class){ //when a new map is loaded, propogate the new name. 
			labelUpdate(((NewMapLoadedNotification) n).getMapLoadedName()); 
			JOptionPane.showMessageDialog(this, "New map: " + (((NewMapLoadedNotification) n).getMapLoadedName()));
		}
		//the data that this class likely requested has been processed and loaded
		//possibly somewhere else requested data, but we can update this graph anyway
		else if (n.getClass() == RouteDataAsRequestedNotification.class){
			RouteDataAsRequestedNotification n2 = (RouteDataAsRequestedNotification) n;
			updateMap(n2.getListOfPoints(), n2.getNumOfDistanceRequested(), n2.getUnitMeasuredBy());
		}
	}

	/**
	 * register for any notifications that this class needs to
	 */
	@Override
	public void register(){
		mySession.register(this, NewMapLoadedNotification.class); //need this for the map label and tool bar.
		mySession.register(this, RouteDataAsRequestedNotification.class); //for when route data is processed and sent out. Likely requested
																		//by this class
		//TODO add any notifications you need to listen for here. 
	}
	
	/**
	 * constructor
	 * @param toAdd - the session to refer to for the controllers, and to register with
	 */
	public Map(GlobalController toAdd) {
		mySession = toAdd;
		register();
		buildDefaultChart();
		setTitleAndLogo();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setBounds(100, 100, 538, 395);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("View Map");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmCoordinates = new JMenuItem("Coordinates");
		mnNewMenu.add(mntmCoordinates);
		
		JMenu mnLoadMap = new JMenu("Load Map");
		menuBar.add(mnLoadMap);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("ASC2014 Route Map");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
					try {
						//TODO hardcoded, will need to update
						mySession.getMapController().load("res/HopeToMerrittElevated.kml");
						
					} catch (IOException e) {
						JDialog dialog = new ErrorMessage("IO Exception: File could not be loaded (bad filename?)");
						dialog.setVisible(true);
						e.printStackTrace();
						
					} catch (SAXException e) {
						JDialog dialog = new ErrorMessage("SAX parser Exception: The file was formatted badly. Bad character?");
						dialog.setVisible(true);
						e.printStackTrace();
						
					} catch (ParserConfigurationException e) {
						JDialog dialog = new ErrorMessage("Something with the parser configuration: Check stack trace.");
						dialog.setVisible(true);
						e.printStackTrace();
						
					}
					mySession.getMapController().getAllPoints();
				 
				
			
			}
		});
		mnLoadMap.add(mntmNewMenuItem);
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Other Map");
		mnLoadMap.add(mntmNewMenuItem_1);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel statusPanel= new JPanel();
		contentPane.add(statusPanel, BorderLayout.NORTH);
		
		lblMapName = new JLabel("Map Loaded: None");
		statusPanel.add(lblMapName);
		
		btnRefreshMapName = new JButton("Refresh Map Name");
		statusPanel.add(btnRefreshMapName);
		//TODO add in handler to refresh name
		
		
		
		//add in the chart
		contentPane.add(cp, BorderLayout.CENTER);
		
		}
	
	
	
	/**
	 * set the title and icon for this window. 
	 */
	private void setTitleAndLogo(){
			this.setIconImage(mySession.iconImage.getImage()); //centrally stored image for easy update (SPOC!)
			this.setTitle("Advanced Map"); //possible "advanced map"?
	}
	
	/**
	 * Creates a canned chart. Usually to initialize the window because no data has been loaded. 
	 */
	private void buildDefaultChart(){
		ds = createBlankDataset();
		this.elevationChart = 
				ChartFactory.createXYLineChart(
						"Height Chart",
						"Distance",
						"Height", 
						ds,
						PlotOrientation.VERTICAL, true, true, false);
		
		cp = new ChartPanel(elevationChart);
	}
	
	/** this method is for testing, builds canned dataset. Code developed from 
	 * http://www.caveofprogramming.com/frontpage/articles/java/charts-in-java-swing-with-jfreechart/
	 * 
	 * @return
	 */
	private XYDataset createDataset(){
	DefaultXYDataset dds = new DefaultXYDataset();
		double[][] data = { {0.1, 0.2, 0.3}, {1, 2, 3} };
		
		dds.addSeries("series1", data);
		return dds;
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
	
	/**
	 * update map with received data
	 * @param listOfPoints - the points
	 * @param numOfDistanceRequested - the distance they cover (or were requested to)
	 * @param unitMeasuredBy - units. 
	 */
	private void updateMap(ArrayList<Point> listOfPoints,
			int numOfDistanceRequested, DistanceUnit unitMeasuredBy) {
		contentPane.remove(this.cp);
		//if there are no points, make an empty graph?
		/*if(listOfPoints.size() <1){ //if there are no points, abort. 
			return;
		}*/
		
		//TODO make the unit dynamic.
		//ds = makeDataSet(listOfPoints, DistanceUnit.KILOMETERS);
		//elevationChart.
		
		double minHeight = 0.0; //blank chart
		double maxHeight = 200.0; //a blank chart
		
		//TODO ignores last 2 cuz I haven't figured out how to sort them yet. 
				double tripDistance = 0.0; //whatever units I select below. 
				double[][] data = new double[2][listOfPoints.size()];
				if(listOfPoints.size() > 1){
					data[0][0] = tripDistance; //first distance is zero.
					data[1][0] = listOfPoints.get(0).getElevationInMeters();
					minHeight = listOfPoints.get(0).getElevationInMeters();
					maxHeight = listOfPoints.get(0).getElevationInMeters();
					
					
					for(int i = 1; i<listOfPoints.size(); i++){
						tripDistance +=  listOfPoints.get(i-1).calculateDistance(listOfPoints.get(i), unitMeasuredBy);
						System.out.println(listOfPoints.get(i-1).calculateDistance(listOfPoints.get(i), unitMeasuredBy));
						data[0][i] = tripDistance;
						
						//TODO make this change from feet to anything
						double tempHeight = listOfPoints.get(i).getElevationInMeters();
						data[1][i] = tempHeight;
						if(tempHeight - minHeight < 0){
							minHeight = tempHeight; //new low
						}
						if(tempHeight - maxHeight > 0){
							maxHeight = tempHeight; //new height
						}
						
						
					}
					
					
				}
				
				
				DefaultXYDataset dds = new DefaultXYDataset();
				
				dds.addSeries("Route", data);
				ds = dds;
				
		
		this.elevationChart = 
				ChartFactory.createXYLineChart(
						"Height Map",
						"Travel Distance (" + unitMeasuredBy + ")",
						"elevation (feet)", //TODO make this dynamic 
						ds,
						PlotOrientation.VERTICAL, true, true, false);
		
		XYPlot plot = (XYPlot) elevationChart.getPlot();
		ValueAxis axis = plot.getRangeAxis();
		axis.setLowerBound(minHeight - ((maxHeight - minHeight) * 0.1)); //pad by 10% of the difference
		axis.setUpperBound(maxHeight + ((maxHeight - minHeight) *  0.1)); //pad by 10% for prettiness
		
		
		
		cp = new ChartPanel(elevationChart);
	
		
		
		
		contentPane.add(cp);
		cp.repaint();
		cp.revalidate();
		contentPane.revalidate();
		
		contentPane.repaint();
		
		//initialize ds, elevationChart, and cp
	
		
	}



	
}
