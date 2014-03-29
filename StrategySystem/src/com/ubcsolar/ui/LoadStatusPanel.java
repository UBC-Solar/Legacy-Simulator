/**
 *  this class forms a basic tool/status bar showing the name of the loaded modules. 
 */
package com.ubcsolar.ui;

import javax.swing.JPanel;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.ubcsolar.common.Listener;
import com.ubcsolar.map.NewMapLoadedNotification;
import com.ubcsolar.notification.NewCarLoadedNotification;
import com.ubcsolar.notification.Notification;

import javax.swing.JLabel;

public class LoadStatusPanel extends JPanel implements Listener {
	private GlobalController mySession;
	//private GUImain parent;
	private JLabel lblMap; //Displays the name of the loaded map
	private JLabel lblWeather; //dispalyed the name of the loaded weather
	private JLabel lblSim; //displays the name of the last-run sim
	private JLabel lblCar; //displays the name of the loaded car (simulated or real?)
	
	/**
	 * constructor
	 * @param session - the session to get the needed controllers
	 */
	public LoadStatusPanel(GlobalController session){
		super();
		//this.parent = parent;
		this.mySession = session;
		setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(49dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(49dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(49dlu;default):grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("max(49dlu;default):grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("max(19dlu;default)"),}));
		
		lblMap = new JLabel("Map loaded: " + mySession.getMapController().getLoadedMapName());
		add(lblMap, "2, 2");
		
		//lblWeather = new JLabel(mySession.getMyWeatherController().getLoadedWeatherName());
		//TODO add these in as we develop the model
		lblWeather = new JLabel("Weather loaded: " + "none");
		add(lblWeather, "4, 2");
		
		lblSim = new JLabel("Sim");
		add(lblSim, "6, 2, fill, default");
		
		lblCar = new JLabel("Car: " + mySession.getMyCarController().getLoadedCarName());
		add(lblCar, "8, 2, center, default");
		register();
	}
	
	/**
	 * updates the map label with the right message
	 * @param mapName - the name of map to display
	 */
	private void updateMapLabel(String mapName){
		lblMap.setText("Map: " + mapName);
	}
	
	/**
	 * updates the car label with the right message to display
	 * @param carName - the name of the car to display
	 */
	private void updateCarLabel(String carName){
		lblCar.setText("Car: " + carName);
	}
	
	
	@Override
	public void notify(Notification n) {
		//TODO add support for additional notifcations as they come. 
		if(n.getClass() == NewMapLoadedNotification.class){
			updateMapLabel(((NewMapLoadedNotification) n).getMapLoadedName()); //to update the map label
		}
		else if(n.getClass() == NewCarLoadedNotification.class){
			updateCarLabel(((NewCarLoadedNotification) n).getNameOfCar()); //to update the car label
		}
		/*//TODO implement these
		else if(n.getClass() == NewWeatherLoadedNotification.class){ //to update the weather label
			updateWeatherLabel(n.getNameOfWeather());
		}
		else if(n.getClass() == SimDoneRunningNotification.class){ //to update the sim label
			
			updateSimLabel(n.getNameOfSim());
		}*/
		
	}
	
	
	@Override
	public void register() {
		mySession.register(this,  NewMapLoadedNotification.class);
		mySession.register(this, NewCarLoadedNotification.class);
		//mySession.register(this, SimDonwRunningNotificaiton.class); //TODO implement when I get there. 
		//mySession.register(this,  NewWeatherLoadedNotification.class); //TODO implement when this gets created
		// TODO Auto-generated method stub
		
	}
	
	
}
