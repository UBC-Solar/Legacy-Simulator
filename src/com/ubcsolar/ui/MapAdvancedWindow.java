/**
 * this Frame is the advanced window for the map. 
 * Will show advanced settings as well as advanced information that doesn't belong on the main window. 
 * Allows a user to load a new map
 */
package com.ubcsolar.ui;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.Main.GlobalValues;
import com.ubcsolar.common.GeoCoord;
import com.ubcsolar.common.Listener;
import com.ubcsolar.common.LogType;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.exception.NoLoadedRouteException;
import com.ubcsolar.notification.NewLocationReportNotification;
import com.ubcsolar.notification.NewMapLoadedNotification;
import com.ubcsolar.notification.Notification;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
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
import javax.swing.JFileChooser;
import javax.swing.JButton;
import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.JDOMException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.TextAnchor;
import org.xml.sax.SAXException;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.SwingConstants;
import java.awt.FlowLayout;
 



public class MapAdvancedWindow extends JFrame implements Listener {


	private JPanel contentPane;
	private GlobalController mySession;
	private JLabel lblMapName;
	private JButton btnRefreshMapName;
	private XYDataset ds;
	private JFreeChart elevationChart;
	private ChartPanel cp;
	private final String DEFAULT_FILE_LOCATION = "Res\\";
	private final MapAdvancedWindow parentInstance = this;
	private final String X_AXIS_LABEL = "Travel Distance (km)";
	private final String Y_AXIS_LABEL = "Elevation (m)";
	/**
	 * update the map name label
	 * @param labelupdate - what to make the label display
	 */
	public void labelUpdate(String labelupdate) {
		lblMapName.setText("Map Loaded: " + labelupdate);
	}
	
	private void handleError(String message){
		JOptionPane.showMessageDialog(this, message);
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
	 * All notifications that this class has registered for will come here
	 */
	@Override
	public void notify(Notification n){
		//add any notifications here
		
		//A new map has been loaded into the program
		if(n.getClass() == NewMapLoadedNotification.class){ //when a new map is loaded, propogate the new name. 
			NewMapLoadedNotification n2 = (NewMapLoadedNotification) n; 
			labelUpdate(n2.getMapLoadedName());
			updateMap(n2.getRoute().getTrailMarkers(), -1);
			if(mySession.getMapController().getLastReportedLocation() != null){
				this.updateCarPositionBar(mySession.getMapController().getLastReportedLocation().getLocation());
			}
		//	JOptionPane.showMessageDialog(this, "New map: " + (((NewMapLoadedNotification) n).getMapLoadedName()));
				
			
		}
		
		if(n.getClass() == NewLocationReportNotification.class){
			NewLocationReportNotification n2 = (NewLocationReportNotification) n;
			GeoCoord currentLocation = n2.getCarLocation().getLocation();
			this.updateCarPositionBar(currentLocation);
		}

	}



	/**
	 * register for any notifications that this class needs to
	 */
	@Override
	public void register(){
		mySession.register(this, NewMapLoadedNotification.class); //need this for the map label and tool bar.
		//add any notifications you need to listen for here. 
		mySession.register(this, NewLocationReportNotification.class);
	}
	
	/**
	 * constructor
	 * @param toAdd - the session to refer to for the controllers, and to register with
	 */
	public MapAdvancedWindow(GlobalController toAdd) {
		
		
		mySession = toAdd;
		register();
		buildDefaultChart();
		setTitleAndLogo();
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setBounds(100, 100, 880, 590);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnLoadMap = new JMenu("Load Map");
		menuBar.add(mnLoadMap);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Select File");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File defaultDirectory = new File(DEFAULT_FILE_LOCATION);
				JFileChooser fc = new JFileChooser();
				if(defaultDirectory.exists() && defaultDirectory.isDirectory()){
					fc.setCurrentDirectory(defaultDirectory);
				}
				
				fc.addChoosableFileFilter(new FileNameExtensionFilter("Google Map files", "KML", "kml"));
				fc.setAcceptAllFileFilterUsed(false); //makes the 'kml' one default. 
				fc.setAcceptAllFileFilterUsed(true);
				
				 int returnVal = fc.showOpenDialog(parentInstance);
				 
				 if (returnVal == JFileChooser.APPROVE_OPTION) {
					
				//	 loadFrame and change the cursor type to waiting cursor
					 
					 contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					 JFrame frame = new LoadingWindow(mySession);
					 frame.setVisible(true);
					 
					 parentInstance.loadMap(fc.getSelectedFile()); //main process
					 
					 frame.setVisible(false);
					 contentPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					 Toolkit.getDefaultToolkit().beep(); // simple alert for end of process
					
					 mapChartNavigationTutorialDialog();
			            
			        }
				 
				 else {
			            //cancelled by user, do nothing
			        	return;
			        }
			}
		});
		mnLoadMap.add(mntmNewMenuItem);
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Connect Cell Phone");
		mntmNewMenuItem_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new BTPhoneSelectDialog(mySession).setVisible(true);
			}
		});
		
		mnLoadMap.add(mntmNewMenuItem_1);
		
		JMenuItem mntmDisconnectCellPhone = new JMenuItem("Disconnect Cell Phone");
		mntmDisconnectCellPhone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
				mySession.getMapController().disconnectCellPhone();
				handleError("Cellphone is disconnected.");
				}
				catch(NullPointerException r){

						handleError("No cellphone was connected.");
					
				}
				
			}
		});
		
		JMenuItem mntmConnectFakeCar = new JMenuItem("Connect Fake Car");
		mntmConnectFakeCar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mySession.getMapController().connectToFakeCar();
			}
		});
		mnLoadMap.add(mntmConnectFakeCar);
		mntmDisconnectCellPhone.setHorizontalAlignment(SwingConstants.CENTER);
		mnLoadMap.add(mntmDisconnectCellPhone);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel statusPanel= new JPanel();
		contentPane.add(statusPanel, BorderLayout.NORTH);
		
		lblMapName = new JLabel("Map Loaded: None");
		statusPanel.add(lblMapName);
		
		
		
		//add in the chart
		contentPane.add(cp, BorderLayout.CENTER);
		
		}
	


	private void loadMap(File fileToLoad){

		try {
			mySession.getMapController().load(fileToLoad);
			
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
			
		} catch (JDOMException e) {
			JDialog dialog = new ErrorMessage("JDOM parser Exception: The file was formatted badly. Bad character?");
			dialog.setVisible(true);
			e.printStackTrace();
		}
		mySession.getMapController().getAllPoints();
	 
	
	}
	
	/**
	 * set the title and icon for this window. 
	 */
	private void setTitleAndLogo(){
			this.setIconImage(GlobalValues.iconImage.getImage()); //centrally stored image for easy update (SPOC!)
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
						X_AXIS_LABEL,
						Y_AXIS_LABEL, 
						ds,
						PlotOrientation.VERTICAL, true, true, false);
		
		cp = new ChartPanel(elevationChart,true,true,true,true,true);
		//cp.setAutoscrolls(true);
		//cp.setRefreshBuffer(true);
		cp.setMouseZoomable(true);
		cp.setMouseWheelEnabled(true);
		
		XYPlot plot = (XYPlot) elevationChart.getPlot();
		plot.setRangePannable(true);
		plot.setDomainPannable(true);

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
	private void updateMap(ArrayList<GeoCoord> listOfPoints,
			int numOfDistanceRequested) {

		double minHeight = -20.0; //blank chart (centers '0')
		double maxHeight = 20.0; //a blank chart
		
				double tripDistance = 0.0; //whatever units I select below. 
				double[][] data = new double[2][listOfPoints.size()];
				if(listOfPoints.size() > 1){
					data[0][0] = tripDistance; //first distance is zero.
					data[1][0] = listOfPoints.get(0).getElevation();
					double firstPointHeight = listOfPoints.get(0).getElevation();
					if(firstPointHeight - minHeight < 0){
						minHeight = firstPointHeight; //new low, IFF it's less than the empty one. 
														//don't want window resizing if it's zero. 										
					}
					if(firstPointHeight - maxHeight > 0){
						maxHeight = firstPointHeight; //same for max. No resizing if it's within the min range. 
					}
					
					for(int i = 1; i<listOfPoints.size(); i++){
						tripDistance +=  listOfPoints.get(i-1).calculateDistance(listOfPoints.get(i));
						data[0][i] = tripDistance;
						
						//TODO make this change from feet to anything
						double tempHeight = listOfPoints.get(i).getElevation();
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
						X_AXIS_LABEL,
						Y_AXIS_LABEL, //TODO make this dynamic 
						ds,
						PlotOrientation.VERTICAL, true, true, false);
		
		XYPlot plot = (XYPlot) elevationChart.getPlot();
		ValueAxis axis = plot.getRangeAxis();
		axis.setLowerBound(minHeight - ((maxHeight - minHeight) * 0.1)); //pad by 10% of the difference
		axis.setUpperBound(maxHeight + ((maxHeight - minHeight) *  0.1)); //pad by 10% for prettiness
		final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer(0);
		//renderer.setBaseLegendTextFont(new Font("Helvetica", Font.BOLD, 11));
		renderer.setSeriesStroke(0, new BasicStroke(3));
		
		
		cp.setChart(elevationChart);
		cp.repaint();
		cp.revalidate();
		contentPane.revalidate();
		contentPane.repaint();
		
		//initialize ds, elevationChart, and cp
	}



	private void updateCarPositionBar(double kilometerMark){
		ValueMarker marker = new ValueMarker(kilometerMark);  // position is the value on the axis
		marker.setPaint(Color.black);
		//marker.setLabel("here"); // see JavaDoc for labels, colors, strokes TODO
		
		XYPlot plot = (XYPlot) elevationChart.getPlot();
		plot.clearDomainMarkers();
		plot.addDomainMarker(marker);
		
		cp.repaint();
		cp.revalidate();
		contentPane.revalidate();
		contentPane.repaint();
	}
	
	private void updateCarPositionBar(GeoCoord currentLocation) {

		if(this.mySession.getMapController().hasMapLoaded()){
			try {
				this.updateCarPositionBar(this.mySession.getMapController().findDistanceAlongLoadedRoute(currentLocation));
			} catch (NoLoadedRouteException e) {
				SolarLog.write(LogType.ERROR, System.currentTimeMillis(), "Tried to get Car location on Map Controller, but no route loaded");
			}
		}
		
	}
}
