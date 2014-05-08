/**
 * This class forms the main window, and launches all additional needed windows. 
 * This is the main "dashboard" for a user on the road. 
 */

package com.ubcsolar.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SpringLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.ubcsolar.common.Listener;
import com.ubcsolar.common.Log;
import com.ubcsolar.common.LogType;
import com.ubcsolar.map.NewMapLoadedNotification;
import com.ubcsolar.notification.CarUpdateNotification;
import com.ubcsolar.notification.Notification;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import javax.swing.JPanel;

public class GUImain implements Listener{

	private JFrame frame;
	private GlobalController mySession; 
	private JLabel loadedMapName;
	private JPanel carWindow;
	private JPanel mainPanel;
	private JPanel simPanel;
	private JPanel mapWindow;
	private JPanel weatherWindow;
	private JFrame myMap;
	private JFrame myCar;
	private JFrame myWeather;
	private JFrame mySim;
	private JPanel LoadStatusPanel;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		Log.write(LogType.SYSTEM_REPORT, System.currentTimeMillis(), "Application started");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUImain window = new GUImain();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUImain() {
		initialize();
	}
	
	/**
	 * creates all windows that can be launched from this main panel
	 */
	private void buildAllWindows(){
		this.mySim = new Simulation(this.mySession); //Sim advanced window
		this.myCar = new Performance(this.mySession); //Car advanced window
		this.myMap = new Map(this.mySession); //Map advanced window
		this.myWeather = new Weather(this.mySession); //Weather advanced window
	}
	
	/**
	 * registers for all classes that this window needs to listen for
	 */
	@Override
	public void register() {
			/*mySession.register(this, NewMapLoadedNotification.class);
			mySession.register(this, CarUpdateNotification.class);*/
		
		
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * will be notified of any notifications this class has registered for. 
	 */
	@Override
	public void notify(Notification n){
	
		//TODO: Do something when notified. 
		
	}
	

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		mySession = new GlobalController(this);
		
		frame = new JFrame();
		frame.setBounds(200, 200, 800, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.buildAllWindows();
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		
		JMenuItem mntmPrintLog = new JMenuItem("Print Log");
		mntmPrintLog.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				Log.printOut();
			}
		});
		mnFile.add(mntmPrintLog);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				mySession.exit();
			}
		});
		mnFile.add(mntmExit);
		
		/*JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				mySession.exit();
			}
		});*/
		
		
		JMenu mnModules = new JMenu("Modules");
		menuBar.add(mnModules);
		
		JMenuItem mntmMap = new JMenuItem("Map");
		mntmMap.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				//System.out.println("test");
			/*	try{
				mySession.getMapController().load("res\\ASC2014ClassicMapFull.kml");
				}
				catch(IOException ex){
					JOptionPane.showMessageDialog(frame, ex.getMessage() + " Could not load map");
				}*/
				launchMap();
			}
		});
		mnModules.add(mntmMap);
		
		JMenuItem mntmSimulator = new JMenuItem("Simulation");
		mntmSimulator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				launchSim();
				
			}
		});
		
		JMenuItem mntmWeather = new JMenuItem("Weather");
		mntmWeather.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				launchWeather();
			}
		});
		mnModules.add(mntmWeather);
		mnModules.add(mntmSimulator);
		
		JMenuItem mntmPerformance = new JMenuItem("Performance");
		mntmPerformance.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				launchPerformance();
				
			}
		});
		mnModules.add(mntmPerformance);
		
		JMenuItem mntmStrategy = new JMenuItem("Strategy");
		mntmStrategy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new Strategy();
				frame.setVisible(true);
			}
		});
		mnModules.add(mntmStrategy);
		this.loadedMapName = new JLabel("None");
		frame.getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("19px:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				RowSpec.decode("14px:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		
		//THIS SECTION ADDS IN THE PANELS
		LoadStatusPanel = new LoadStatusPanel(this.mySession);
		LoadStatusPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		//frame.getContentPane().add(LoadStatusPanel);
		frame.getContentPane().add(LoadStatusPanel, "1, 1, 3, 1, fill, fill");
		//TODO: remove the fills. We don't want it to grow.
		
		weatherWindow = new WeatherPanel(this.mySession, this);
		weatherWindow.setBorder(BorderFactory.createLineBorder(Color.black));
		frame.getContentPane().add(weatherWindow, "1, 3, fill, fill");
		
		/*JLabel lblWeather = new JLabel("Weather");
		weatherWindow.add(lblWeather);*/
		
		carWindow = new CarPanel(this.mySession, this);
		carWindow.setBorder(BorderFactory.createLineBorder(Color.black));
		frame.getContentPane().add(carWindow, "1, 5, fill, fill");

		
		
		mainPanel = new JPanel();
		mainPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		frame.getContentPane().add(mainPanel, "3, 3, 1, 7, fill, fill");
		
		JLabel lblMain = new JLabel("Main");
		mainPanel.add(lblMain);
		//TODO: turn these panels into their own classes, and set them up.
		simPanel = new JPanel();
		frame.getContentPane().add(simPanel, "1, 7, fill, fill");
		simPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		
		JLabel lblSim = new JLabel("Sim");
		simPanel.add(lblSim);
		
		mapWindow = new JPanel();
		frame.getContentPane().add(mapWindow, "1, 9, fill, fill");
		mapWindow.setBorder(BorderFactory.createLineBorder(Color.black));
		
		JLabel lblMap = new JLabel("Map");
		mapWindow.add(lblMap);
		register(); //do last, in case a notification is sent before we're done building.
		
		
		setTitleAndLogo();
	}
	
	private void setTitleAndLogo(){
		frame.setIconImage(mySession.iconImage.getImage());
		frame.setTitle("TITUS-Main");
	}
	
	
	/**
	 * launches the Sim window
	 */
	public void launchSim() {
		mySim.setVisible(true);
		
	}

	/**
	 * launches the Weather window
	 */
	public void launchWeather() {
		myWeather.setVisible(true);
		
	}
	/**
	 * launches the Car window
	 */
	public void launchPerformance() {
		myCar.setVisible(true);
		
	}
	/**
	 * launches the Map window
	 */
	public void launchMap(){
		myMap.setVisible(true);
	}
	
	

}
