/**
 */

package com.ubcsolar.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.Main.GlobalValues;
import com.ubcsolar.common.Listener;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.common.LogType;
import com.ubcsolar.notification.ExceptionNotification;
import com.ubcsolar.notification.NewForecastReport;
import com.ubcsolar.notification.NewLocationReportNotification;
import com.ubcsolar.notification.NewMapLoadedNotification;
import com.ubcsolar.notification.Notification;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class GUImain implements Listener{

	private JFrame mainFrame; //The main/root program window
	private GlobalController mySession; //Global Controller for the program (interface between code and UI)
	private JPanel carPanel; //Car status within the main window
	private CustomDisplayMap mainPanel; //Biggest panel in the main window; Shows amalgamated information
	private JPanel simPanel; //Sim status within the main window
	private JPanel mapPanel; //map status within the main window
	private JPanel weatherPanel; //weather status within the main window
	private JFrame mapFrame; //The map module's 'advanced' options menu
	private JFrame carFrame; //The car module's 'advanced' options menu
	private JFrame weatherFrame; //The weather module's 'advanced' options menu
	private JFrame simFrame; //The sim module's 'advanced' options menu
	private JPanel loadStatusPanel; //Shows the loaded status of modules at a quick glance

	/**
	 * Constructor; Creates the application.
	 */
	public GUImain(GlobalController parent) {
		initialize(parent);
	}
	
	/**
	 * creates all windows that can be launched from this main panel.
	 * Does not overwrite any that currently exist! Should close those before calling this. 
	 */
	private void buildAllWindows(){
		if(this.simFrame == null){
		this.simFrame = new SimulationAdvancedWindow(this.mySession, this); //Sim advanced window
		}
		if(this.carFrame == null){
		this.carFrame = new CarAdvancedWindow(this.mySession); //Car advanced window
		}
		if(this.mapFrame == null){
		this.mapFrame = new MapAdvancedWindow(this.mySession); //Map advanced window
		}
		if(this.weatherFrame == null){
		this.weatherFrame = new WeatherAdvancedWindow(this.mySession, this); //Weather advanced window
		}

	}
	
	/**
	 * registers for all notification classes that this window needs to listen for. 
	 */
	@Override
	public void register() {
			/*mySession.register(this, NewMapLoadedNotification.class);
			mySession.register(this, CarUpdateNotification.class);*/	
			mySession.register(this, ExceptionNotification.class);
			mySession.register(this, NewMapLoadedNotification.class);
			mySession.register(this, NewLocationReportNotification.class);
			mySession.register(this, NewForecastReport.class);
			//mySession.register(this, ); TODO for loading frame
	}
	
	/**
	 * will be notified of any notifications this class has registered for. 
	 */
	@Override
	public void notify(Notification n){
	
		//Do something here when notified. 
		
		if(n.getClass()== ExceptionNotification.class){
			handleException((ExceptionNotification) n);
		}
		if(n.getClass() == NewMapLoadedNotification.class){
			drawNewMap((NewMapLoadedNotification) n);
		}
		
		if(n.getClass() == NewLocationReportNotification.class){
			NewLocationReportNotification temp = (NewLocationReportNotification) n;
			mainPanel.addNewCarLocationToMap(temp.getCarLocation());
		}
		if(n.getClass() == NewForecastReport.class){
			NewForecastReport temp = (NewForecastReport) n;
			mainPanel.addForecastsToMap(temp.getTheReport());
		}
		
		//if(n.getClass() == ) //TODO for loadingFrame
		
	}
	
	private void drawNewMap(NewMapLoadedNotification notification) {
		/*Coordinate vancouver = new Coordinate(49.282,-123.12);
		Coordinate merritt = new Coordinate(50.1119,-120.78);
		Coordinate thirdPoint = new Coordinate(49.282, -120.78);
		mainPanel.addMapMarker(new MapMarkerDot(vancouver)); //Vancouver
		mainPanel.addMapMarker(new MapMarkerDot(merritt)); //merritt
		mainPanel.addMapMarker(new MapMarkerDot(thirdPoint)); //third point to make a triangle
		List<Coordinate> toAdd = new ArrayList<Coordinate>(3);
		toAdd.add(vancouver);
		toAdd.add(merritt);
		toAdd.add(thirdPoint);
		mainPanel.addMapPolygon(new MapPolygonImpl(toAdd));
		System.out.println(n.getRoute().getTrailMarkers().get(0));
		*/
		/*Route temp = notification.getRoute();
		List<Coordinate> listForPolygon = new ArrayList<Coordinate>(temp.getTrailMarkers().size());
		for(GeoCoord geo : temp.getTrailMarkers()){
			listForPolygon.add(new Coordinate(geo.getLat(), geo.getLon()));
		}
		
		//adding this in to make it a single line, otherwise it draws a line from end to start. 
		for(int i = temp.getTrailMarkers().size()-1; i>=0; i--){
			GeoCoord toAdd = temp.getTrailMarkers().get(i);
			listForPolygon.add(new Coordinate(toAdd.getLat(), toAdd.getLon()));
		}
		
		mainPanel.addMapPolygon(new MapPolygonImpl(listForPolygon));
		*/
		mainPanel.changeDrawnRoute(notification.getRoute());
	}

	public void handleException(ExceptionNotification e){
		JOptionPane.showMessageDialog(new JFrame(), e.getMessage());
	}
	// To display urgent messages to the user, but not neccesarily an exception
	//i.e battery pack at dangerous temperatures
	public void showPupupToUser(String e){
		JOptionPane.showMessageDialog(mainFrame, e);
	}
	

	/**
	 * This method basically creates the entire UI. 
	 * Creates a new mainframe, then generates and adds all sub components. 
	 * @param parent 
	 */
	private void initialize(GlobalController parent) {

		mySession = parent; //adds parent 
		mainFrame = new JFrame(); //
		setTitleAndLogo();
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double screenWidth = screenSize.getWidth();
		double screenHeight = screenSize.getHeight();
		double programWindowWidth = screenWidth/1.4;
		double programWindowHeight = screenHeight/1.05; //leave a little gap at top
		double programWindowStartx = (screenWidth - programWindowWidth)/2; //center it
		double programWindowStarty = 0+(screenHeight/40); //give a little buffer.
		
		//public void setBounds(int x, int y, int width, int height)
		mainFrame.setBounds((int) programWindowStartx, (int) programWindowStarty, (int) programWindowWidth, (int) programWindowHeight); //main window size on opening
		mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainFrame.setVisible(true);
		//NOTE: Could consider not building all the windows at once in case of performance issues.
		this.buildAllWindows(); //builds all the windows in one shot
		JMenuBar menuBar = new JMenuBar();
		mainFrame.setJMenuBar(menuBar);
		
		//adding this to gracefully close the program when the 'x' is hit on the main frame. 
		mainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        tryCloseProgram();
		    }
		});
		
		//THIS SECTION CREATES AND ADDS IN THE 'FILE' MENU
		JMenu mnFile = new JMenu("File"); //Make a 'file' drop down list
		menuBar.add(mnFile); //add that list to the main menu bar 
		
		
		JMenuItem mntmPrintLog = new JMenuItem("Print Log"); //Print option
		mntmPrintLog.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				SolarLog.printOut();
			}
		});
		mnFile.add(mntmPrintLog); //add it to the File menu
		
		JMenuItem mntmExit = new JMenuItem("Exit"); //Exit option in the menu
		mntmExit.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				tryCloseProgram();
			}
		});
		mnFile.add(mntmExit); //add it to the File menu
			
		//THIS SECTION CREATES ADDS IN THE MODULES DROP DOWN MENU
		//Make and add the 'Modules' drop down menu to the main menu bar
		JMenu mnModules = new JMenu("Modules");
		menuBar.add(mnModules);
		
		//Make entry to open Sim advanced options
		JMenuItem mntmSimulator = new JMenuItem("Simulation");
		mntmSimulator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				launchSim();
			}
		});
		
		//Make entry to open Weather advanced options
		JMenuItem mntmWeather = new JMenuItem("Weather");
		mntmWeather.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				launchWeather();
			}
		});
		mnModules.add(mntmWeather);
		
		//Make entry to open Car (aka 'performance') advanced options
		JMenuItem mntmPerformance = new JMenuItem("Car Advanced");
		mntmPerformance.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				launchCarAdvancedWindow();
			}
		});
		mnModules.add(mntmPerformance);
		mnModules.add(mntmSimulator);
		
		//Make entry to open Map advanced options
		JMenuItem mntmMap = new JMenuItem("Map");
		mntmMap.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				//Should launch the map advanced window when clicked on
				launchMap();
			}
		});
		mnModules.add(mntmMap);
		
		JMenu mnDebug = new JMenu("Debug");
		menuBar.add(mnDebug);
		
		JMenuItem mntmAddCustomForecast = new JMenuItem("Add Custom Forecast");
		mntmAddCustomForecast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if(mySession.getMapController().getAllPoints() == null){
					mySession.sendNotification(new ExceptionNotification(new NullPointerException(), 
							"Must load route before adding forecasts."));
				}else{
					JFrame frame = new FakeForecastAddWindow(mySession);
					frame.setVisible(true);
				}
			}
		});
		mnDebug.add(mntmAddCustomForecast);
		
		JMenuItem mntmClearCustomForecasts = new JMenuItem("Clear Custom Forecasts");
		mntmClearCustomForecasts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				mySession.getMyWeatherController().clearCustomForecasts();
			}
		});
		mnDebug.add(mntmClearCustomForecasts);
		
		JMenuItem mntmAddLocationReport = new JMenuItem("Add Location Report");
		mntmAddLocationReport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new CustomLocationAddWindow(mySession);
				frame.setVisible(true);
			}
		});
		
		JMenuItem mntmAddCarTelempacket = new JMenuItem("Add Car TelemPacket");
		mntmAddCarTelempacket.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new CarTelemPacketWindow(mySession);
				frame.setVisible(true);
			}
		});
		mnDebug.add(mntmAddCarTelempacket);
		mnDebug.add(mntmAddLocationReport);
		
		//This sets up the layout for the main window
		mainFrame.getContentPane().setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("300px"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				RowSpec.decode("0px"), //this is the row for the status panel 
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		
		//THIS SECTION ADDS IN THE PANELS
		
		weatherPanel = new WeatherPanel(this.mySession, this);
		weatherPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		mainFrame.getContentPane().add(weatherPanel, "1, 3, fill, fill");
		
		/*JLabel lblWeather = new JLabel("Weather");
		weatherWindow.add(lblWeather);*/
		
		carPanel = new CarPanel(this.mySession, this);
		carPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		mainFrame.getContentPane().add(carPanel, "1, 5, fill, fill");

		mainPanel = new CustomDisplayMap();
		mainPanel.setCenter(new Point(350,700));
		mainPanel.setZoom(5);
		mainPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		mainFrame.getContentPane().add(mainPanel, "3, 3, 1, 7, fill, fill");
		
		JLabel lblMain = new JLabel("Main");
		mainPanel.add(lblMain);
		
		simPanel = new SimPanel(this, mySession);
		mainFrame.getContentPane().add(simPanel, "1, 7, fill, fill");
		simPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		
		
		mapPanel = new MapPanel(this, mySession);
		mainFrame.getContentPane().add(mapPanel, "1, 9, fill, fill");
		mapPanel.validate();
		mapPanel.repaint();
		mainFrame.getContentPane().validate();
		mainFrame.getContentPane().repaint();
		register();
		

		mainFrame.repaint(); //sometimes the map window button doesn't pop up, hoping this fixes the glitch
	}
	
	/**
	 * Sets the window title and logo. Currently just what I came up with 
	 */
	private void setTitleAndLogo(){
		mainFrame.setIconImage(GlobalValues.iconImage.getImage());
		mainFrame.setTitle("TITUS-Main");
	}
	
	
	/**
	 * launches the Sim window (should be created already)
	 */
	public void launchSim() {
		if(simFrame == null){ //Shouldn't happen
			SolarLog.write(LogType.ERROR, System.currentTimeMillis(),
					"Tried to open the Sim advanced window, but was null");
			this.buildAllWindows();
		}
		simFrame.setVisible(true);
		
	}

	/**
	 * launches the Weather window
	 */
	public void launchWeather() {
		if(weatherFrame == null){ //Shouldn't happen
			SolarLog.write(LogType.ERROR, System.currentTimeMillis(),
					"Tried to open the Weather advanced window, but was null");
			this.buildAllWindows();
		}
		weatherFrame.setVisible(true);
		//if(weatherFrame.getState()!=Frame.NORMAL) { weatherFrame.setState(Frame.NORMAL); }
		weatherFrame.toFront();
		weatherFrame.repaint();
		
	}
	/**
	 * launches the Car window
	 */
	public void launchCarAdvancedWindow() {
		if(carFrame == null){ //Shouldn't happen
			SolarLog.write(LogType.ERROR, System.currentTimeMillis(),
					"Tried to open the Car advanced window, but was null");
			this.buildAllWindows();
		}
		carFrame.setVisible(true);
		
	}
	/**
	 * launches the Map window
	 */
	public void launchMap(){
		if(mapFrame == null){ //Shouldn't happen
			SolarLog.write(LogType.ERROR, System.currentTimeMillis(),
					"Tried to open the Map advanced window, but was null");
			this.buildAllWindows();
		}
		
		mapFrame.setVisible(true);
	}
	
	public void tryCloseProgram() {
		if (JOptionPane.showConfirmDialog(mainFrame, 
            "Are you sure you want to quit?", "Quit Program?", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
            mySession.exit(); //do the graceful exit code. 
        }
	}

	public void clearWeather(){
		WeatherAdvancedWindow weatherWindow = (WeatherAdvancedWindow) weatherFrame;
		weatherWindow.clearWindow();
	}

}
