/**
 * this Frame is the advanced window for the map. 
 * Will show advanced settings as well as advanced information that doesn't belong on the main window. 
 * Allows a user to load a new map
 */
package com.ubcsolar.ui;

import com.ubcsolar.common.Listener;
import com.ubcsolar.map.*;
import com.ubcsolar.notification.Notification;

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
import org.jfree.chart.plot.PlotOrientation;
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
	 * Launch the application.
	 *//*//commented out, don't need a MAIN in Map. 
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Map frame = new Map();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}*/

	/**
	 * Create the frame.
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
		if(n.getClass() == NewMapLoadedNotification.class){ //when a new map is loaded, propogate the new name. 
			labelUpdate(((NewMapLoadedNotification) n).getMapLoadedName()); 
			JOptionPane.showMessageDialog(this, "New map: " + (((NewMapLoadedNotification) n).getMapLoadedName()));
		}
	}
	
	/**
	 * register for any notifications that this class needs to
	 */
	@Override
	public void register(){
		mySession.register(this, NewMapLoadedNotification.class); //need this for the map label and tool bar.
		//TODO add any notifications you need to listen for here. 
	}
	
	/**
	 * constructor
	 * @param toAdd - the session to refer to for the controllers, and to register with
	 */
	public Map(GlobalController toAdd) {
		mySession = toAdd;
		register();
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
						mySession.getMapController().load("res/test.txt");
					} catch (IOException e) {
						JDialog dialog = new ErrorMessage("IO Exception: File could not be loaded (bad filename?)");
						dialog.setVisible(true);
						e.printStackTrace();
						
					} catch (SAXException e) {
						JDialog dialog = new ErrorMessage("SAX parser Exception: The file was formatted badly");
						dialog.setVisible(true);
						e.printStackTrace();
						
					} catch (ParserConfigurationException e) {
						JDialog dialog = new ErrorMessage("Something with the parser configuration: Check stack trace.");
						dialog.setVisible(true);
						e.printStackTrace();
						
					}
				 
				
				//TODO hardcoded, will need to update
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
		
		
		
		buildDefaultChart();
		contentPane.add(cp, BorderLayout.CENTER);
		
		
		setTitleAndLogo();
		}
		
		private void setTitleAndLogo(){
			this.setIconImage(mySession.iconImage.getImage());
			this.setTitle("Map");
		}
	private void buildDefaultChart(){
		ds = createDataset();
		this.elevationChart = 
				ChartFactory.createXYLineChart(
						"Test Chart",
						"x axis",
						"y axis", 
						ds,
						PlotOrientation.VERTICAL, true, true, false);
		
		cp = new ChartPanel(elevationChart);
		//initialize ds, elevationChart, and cp
	}
	
	/** this method is for testing. Code developed from 
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
	
}
