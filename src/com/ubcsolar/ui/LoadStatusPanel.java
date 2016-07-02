/**
 *  this class forms a basic tool/status bar showing the name of the loaded modules. 
 */
package com.ubcsolar.ui;

import javax.swing.JPanel;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.Main.GlobalValues;
import com.ubcsolar.common.Listener;
import com.ubcsolar.database.DatabaseController;
import com.ubcsolar.notification.CarUpdateNotification;
import com.ubcsolar.notification.DatabaseCreatedOrConnectedNotification;
import com.ubcsolar.notification.DatabaseDisconnectedOrClosed;
import com.ubcsolar.notification.NewCarLoadedNotification;
import com.ubcsolar.notification.NewForecastReport;
import com.ubcsolar.notification.NewLocationReportNotification;
import com.ubcsolar.notification.NewMetarReportLoadedNotification;
import com.ubcsolar.notification.NewTafReportLoadedNotification;
import com.ubcsolar.notification.Notification;

import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.SwingConstants;

public class LoadStatusPanel extends JPanel implements Listener {
	private GlobalController mySession;
	//private GUImain parent;
	private JLabel lblCar; //displays the name of the loaded car (simulated or real?)
	private JLabel lblTaf; //displays the name of the loaded Taf report
	private JLabel lblDatabase; //displays the status of the Database. 
	private JLabel lblLocationReport;
	private JLabel lblTelemdata;
	private Component horizontalGlue_4;
	private DateFormat labelTimeFormat = new SimpleDateFormat("HH:mm:ss"); //the format for the times on the labels.
	//private final String TAFTITLE = "Taf: ";
	private final String CARLOADED = " Car: ";
	private final String LOCATIONREPORT = "|  LocationReprt: ";
	private final String TELEMDATA = "|  TelemData: ";	
	/**
	 * constructor
	 * @param session - the session to get the needed controllers
	 */
	public LoadStatusPanel(GlobalController session){
		super();
		//this.parent = parent;
		mySession = session;
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		lblCar = new JLabel("Car: ");
		lblCar.setHorizontalAlignment(SwingConstants.LEFT);
		add(lblCar);
		
		horizontalGlue_4 = Box.createHorizontalGlue();
		add(horizontalGlue_4);
		
		lblDatabase = new JLabel("|    Database: unknown   ");
		add(lblDatabase);
		
		lblTelemdata = new JLabel("|        TelemData: ");
		add(lblTelemdata);
		
		lblLocationReport = new JLabel("|        LocationReprt: ");
		lblLocationReport.setHorizontalAlignment(SwingConstants.RIGHT);
		add(lblLocationReport);

		if(mySession != null){
			initializeValues();
			register();
		}
	}
	
	private void initializeValues(){
		//updateTafLabel("none");
		this.updateLocationLabel("none");
		this.updateTelemLabel("none");
		updateCarLabel(mySession.getMyCarController().getLoadedCarName());
		if(mySession.getMyDataBaseController() != null){
		if((mySession.getMyDataBaseController()).getDatabaseName() != null){
		this.updateDatabaseLabel(mySession.getMyDataBaseController().getDatabaseName(), false, System.currentTimeMillis());
		}
		else{
			this.updateDatabaseLabel(null, true, System.currentTimeMillis());
		}
		}
	}
	
	/**
	 * updates the map label with the right message
	 * @param mapName - the name of map to display
	 */
	
	/**
	 * updates the car label with the right message to display
	 * @param carName - the name of the car to display
	 */
	private void updateCarLabel(String carName){
		lblCar.setText(CARLOADED + carName);
	}
	
	private void updateDatabaseLabel(String databaseName, boolean isClosed, double time){
		//Could consider breaking this into two different methods; don't need the name for a 
		//disconnection. 
		if(isClosed){
		this.lblDatabase.setText("|  Database: DISCONECTED@" + labelTimeFormat.format(time)); 
		}
		else{
			this.lblDatabase.setText("| Database: " + databaseName + "@" + labelTimeFormat.format(time));
		}
	}
	
	@Override
	public void notify(Notification n) {
		//Add support for additional notifications as they come. 
		if(n.getClass() == NewCarLoadedNotification.class){
			updateCarLabel(((NewCarLoadedNotification) n).getNameOfCar()); //to update the car label
		}
		/*else if(n.getClass() == NewMetarReportLoadedNotification.class){
			updateTafLabel("" + GlobalValues.hourMinSec.format(n.getTimeCreated()));
		}*/
		else if(n.getClass() == DatabaseCreatedOrConnectedNotification.class){
			this.updateDatabaseLabel(((DatabaseCreatedOrConnectedNotification) n).getName(), false, n.getTimeCreated());
		}
		else if(n.getClass() == DatabaseDisconnectedOrClosed.class){
			this.updateDatabaseLabel(((DatabaseDisconnectedOrClosed) n).getName(), true, n.getTimeCreated());
		}
		else if(n.getClass() == CarUpdateNotification.class){
			this.updateTelemLabel("rcv'd @ " + GlobalValues.hourMinSec.format(n.getTimeCreated()));
		}
		else if(n.getClass() == NewLocationReportNotification.class){
			this.updateLocationLabel("rcv'd @ " + GlobalValues.hourMinSec.format(n.getTimeCreated()));
		}
		
		/*//TODO implement these
		else if(n.getClass() == NewWeatherLoadedNotification.class){ //to update the weather label
			updateWeatherLabel(n.getNameOfWeather());
		}
		else if(n.getClass() == SimDoneRunningNotification.class){ //to update the sim label
			
			updateSimLabel(n.getNameOfSim());
		}*/
		
	}
	
	

/*	private void updateTafLabel(String string) {
		this.lblTaf.setText(TAFTITLE + string);
		
	}
	*/
	private void updateLocationLabel(String string){
		lblLocationReport.setText(LOCATIONREPORT + string);
	}
	
	private void updateTelemLabel(String string){
		lblTelemdata.setText(TELEMDATA + string);
	}
	
	@Override
	public void register() {
		if(mySession != null){
		mySession.register(this, NewCarLoadedNotification.class);
		mySession.register(this, NewMetarReportLoadedNotification.class);
		mySession.register(this, NewForecastReport.class);
		mySession.register(this, NewTafReportLoadedNotification.class);
		mySession.register(this, DatabaseCreatedOrConnectedNotification.class);
		mySession.register(this, DatabaseDisconnectedOrClosed.class);
		mySession.register(this,  CarUpdateNotification.class);
		mySession.register(this, NewLocationReportNotification.class);
		}
		//mySession.register(this, SimDonwRunningNotificaiton.class); //TODO implement when I get there. 
		//mySession.register(this,  NewWeatherLoadedNotification.class); //TODO implement when this gets created
		
	}
	
	
}
