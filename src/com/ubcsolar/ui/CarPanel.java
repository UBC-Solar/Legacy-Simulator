/**
 * The Car subpanel on the main UI
 */
package com.ubcsolar.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.util.*;
import java.util.Map;
import com.ubcsolar.car.TelemDataPacket;
import com.ubcsolar.common.Listener;
import com.ubcsolar.common.LogType;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.notification.CarUpdateNotification;
import com.ubcsolar.notification.NewCarLoadedNotification;
import com.ubcsolar.notification.Notification;
import java.awt.Insets;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Color;
import javax.swing.BoxLayout;
import java.awt.Component;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class CarPanel extends JPanel implements Listener {
	private GlobalController mySession;
	private GUImain parent;
	private JLabel lblTempuratures; //Title
	private JLabel lblTempitem1; //first temp item
	private JLabel lblTempitem_2_1; //2nd temp item
	private JLabel lblTempitem_3_1; //3rd item in temperature list
	private JLabel lblTempitem_4; //4th...
	private JLabel lblTempitem_5; //5th item in temperature list.
	private JLabel lblPackv1; //first item in voltage pack list
	private JLabel lblPackv2; //second item in voltage pack list
	private JLabel lblPackv_3; //the third
	private JLabel lblPackv_4; //and fourth. Can add more as needed 
	private JLabel lblSpeed; //the label showing speed (update this one!)
	private JLabel lblLastDataReceived; //Time of last data received
	private JLabel lblCarLoadedName; // the name of the currently loaded car. 
	
	
	
	public CarPanel(GlobalController session, GUImain parent) {
		mySession = session;
		this.parent = parent;
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{364, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JPanel titleAndMenuPanel = new JPanel();
		GridBagConstraints gbc_titleAndMenuPanel = new GridBagConstraints();
		gbc_titleAndMenuPanel.insets = new Insets(0, 0, 5, 0);
		gbc_titleAndMenuPanel.fill = GridBagConstraints.BOTH;
		gbc_titleAndMenuPanel.gridx = 0;
		gbc_titleAndMenuPanel.gridy = 0;
		add(titleAndMenuPanel, gbc_titleAndMenuPanel);
		GridBagLayout gbl_titleAndMenuPanel = new GridBagLayout();
		gbl_titleAndMenuPanel.columnWidths = new int[]{0, 0, 51, 0, 0, 0};
		gbl_titleAndMenuPanel.rowHeights = new int[]{14, 0};
		gbl_titleAndMenuPanel.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_titleAndMenuPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		titleAndMenuPanel.setLayout(gbl_titleAndMenuPanel);
		
		JButton btnSettings = new JButton("Settings");
		btnSettings.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				showAdvanced();
			}
		});
		GridBagConstraints gbc_btnSettings = new GridBagConstraints();
		gbc_btnSettings.insets = new Insets(0, 0, 0, 5);
		gbc_btnSettings.gridx = 1;
		gbc_btnSettings.gridy = 0;
		titleAndMenuPanel.add(btnSettings, gbc_btnSettings);
		
		JLabel lblCarTitle = new JLabel("CAR");
		lblCarTitle.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblCarTitle.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblCarTitle = new GridBagConstraints();
		gbc_lblCarTitle.insets = new Insets(0, 0, 0, 5);
		gbc_lblCarTitle.gridx = 2;
		gbc_lblCarTitle.gridy = 0;
		titleAndMenuPanel.add(lblCarTitle, gbc_lblCarTitle);
		
		JButton btnDash = new JButton("Dash");
		GridBagConstraints gbc_btnDash = new GridBagConstraints();
		gbc_btnDash.insets = new Insets(0, 0, 0, 5);
		gbc_btnDash.gridx = 3;
		gbc_btnDash.gridy = 0;
		titleAndMenuPanel.add(btnDash, gbc_btnDash);
		
		JPanel statusPanel = new JPanel();
		GridBagConstraints gbc_statusPanel = new GridBagConstraints();
		gbc_statusPanel.insets = new Insets(0, 0, 5, 0);
		gbc_statusPanel.fill = GridBagConstraints.BOTH;
		gbc_statusPanel.gridx = 0;
		gbc_statusPanel.gridy = 1;
		add(statusPanel, gbc_statusPanel);
		GridBagLayout gbl_statusPanel = new GridBagLayout();
		gbl_statusPanel.columnWidths = new int[]{0, 0, 0};
		gbl_statusPanel.rowHeights = new int[]{0, 0};
		gbl_statusPanel.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_statusPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		statusPanel.setLayout(gbl_statusPanel);
		
		lblLastDataReceived = new JLabel("Last Data Received: NONE");
		GridBagConstraints gbc_lblLastDataReceived = new GridBagConstraints();
		gbc_lblLastDataReceived.insets = new Insets(0, 0, 0, 5);
		gbc_lblLastDataReceived.gridx = 0;
		gbc_lblLastDataReceived.gridy = 0;
		statusPanel.add(lblLastDataReceived, gbc_lblLastDataReceived);
		
		lblCarLoadedName = new JLabel("Car Loaded: NONE");
		GridBagConstraints gbc_lblCarLoadedName = new GridBagConstraints();
		gbc_lblCarLoadedName.gridx = 1;
		gbc_lblCarLoadedName.gridy = 0;
		statusPanel.add(lblCarLoadedName, gbc_lblCarLoadedName);
		
		JPanel containterPanelForUpdateLabels = new JPanel();
		GridBagConstraints gbc_containterPanelForUpdateLabels = new GridBagConstraints();
		gbc_containterPanelForUpdateLabels.fill = GridBagConstraints.BOTH;
		gbc_containterPanelForUpdateLabels.gridx = 0;
		gbc_containterPanelForUpdateLabels.gridy = 2;
		add(containterPanelForUpdateLabels, gbc_containterPanelForUpdateLabels);
		GridBagLayout gbl_containterPanelForUpdateLabels = new GridBagLayout();
		gbl_containterPanelForUpdateLabels.columnWidths = new int[]{0, 76, 88, 68, 0, 0};
		gbl_containterPanelForUpdateLabels.rowHeights = new int[]{0, 0};
		gbl_containterPanelForUpdateLabels.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_containterPanelForUpdateLabels.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		containterPanelForUpdateLabels.setLayout(gbl_containterPanelForUpdateLabels);
		
		JPanel temperatureDispPanel = new JPanel();
		GridBagConstraints gbc_temperatureDispPanel = new GridBagConstraints();
		gbc_temperatureDispPanel.insets = new Insets(0, 0, 0, 5);
		gbc_temperatureDispPanel.fill = GridBagConstraints.BOTH;
		gbc_temperatureDispPanel.gridx = 1;
		gbc_temperatureDispPanel.gridy = 0;
		containterPanelForUpdateLabels.add(temperatureDispPanel, gbc_temperatureDispPanel);
		temperatureDispPanel.setLayout(new BoxLayout(temperatureDispPanel, BoxLayout.Y_AXIS));
		
		lblTempuratures = new JLabel("Temps");
		lblTempuratures.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblTempuratures.setHorizontalAlignment(SwingConstants.CENTER);
		lblTempuratures.setAlignmentX(Component.CENTER_ALIGNMENT);
		temperatureDispPanel.add(lblTempuratures);
		
		lblTempitem1 = new JLabel("None received");
		lblTempitem1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTempitem1.setAlignmentX(Component.CENTER_ALIGNMENT);
		temperatureDispPanel.add(lblTempitem1);
		
		lblTempitem_2_1 = new JLabel("tempItem1");
		lblTempitem_2_1.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblTempitem_2_1.setHorizontalAlignment(SwingConstants.CENTER);
		temperatureDispPanel.add(lblTempitem_2_1);
		
		lblTempitem_3_1 = new JLabel("TempItem 3");
		lblTempitem_3_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblTempitem_3_1.setAlignmentX(Component.CENTER_ALIGNMENT);
		temperatureDispPanel.add(lblTempitem_3_1);
		
		lblTempitem_4 = new JLabel("TempItem5");
		lblTempitem_4.setHorizontalAlignment(SwingConstants.CENTER);
		lblTempitem_4.setAlignmentX(Component.CENTER_ALIGNMENT);
		temperatureDispPanel.add(lblTempitem_4);
		
		lblTempitem_5 = new JLabel("d");
		lblTempitem_5.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblTempitem_5.setHorizontalAlignment(SwingConstants.CENTER);
		temperatureDispPanel.add(lblTempitem_5);
		
		JPanel cellVoltagePanel = new JPanel();
		GridBagConstraints gbc_cellVoltagePanel = new GridBagConstraints();
		gbc_cellVoltagePanel.insets = new Insets(0, 0, 0, 5);
		gbc_cellVoltagePanel.fill = GridBagConstraints.BOTH;
		gbc_cellVoltagePanel.gridx = 2;
		gbc_cellVoltagePanel.gridy = 0;
		containterPanelForUpdateLabels.add(cellVoltagePanel, gbc_cellVoltagePanel);
		cellVoltagePanel.setLayout(new BoxLayout(cellVoltagePanel, BoxLayout.Y_AXIS));
		
		JLabel lblCellVoltages = new JLabel("Avg Cell V");
		lblCellVoltages.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblCellVoltages.setAlignmentX(Component.CENTER_ALIGNMENT);
		cellVoltagePanel.add(lblCellVoltages);
		
		lblPackv1 = new JLabel("Pack1: 23v");
		lblPackv1.setAlignmentX(Component.CENTER_ALIGNMENT);
		cellVoltagePanel.add(lblPackv1);
		
		lblPackv2 = new JLabel("Pack 2: 322");
		lblPackv2.setAlignmentX(Component.CENTER_ALIGNMENT);
		cellVoltagePanel.add(lblPackv2);
		
		lblPackv_3 = new JLabel("Pack 3: 10v");
		lblPackv_3.setAlignmentX(Component.CENTER_ALIGNMENT);
		cellVoltagePanel.add(lblPackv_3);
		
		lblPackv_4 = new JLabel("Pack 4: 13v");
		lblPackv_4.setHorizontalAlignment(SwingConstants.CENTER);
		lblPackv_4.setAlignmentX(Component.CENTER_ALIGNMENT);
		cellVoltagePanel.add(lblPackv_4);
		
		JPanel speedDisplayPanel = new JPanel();
		GridBagConstraints gbc_speedDisplayPanel = new GridBagConstraints();
		gbc_speedDisplayPanel.insets = new Insets(0, 0, 0, 5);
		gbc_speedDisplayPanel.fill = GridBagConstraints.BOTH;
		gbc_speedDisplayPanel.gridx = 3;
		gbc_speedDisplayPanel.gridy = 0;
		containterPanelForUpdateLabels.add(speedDisplayPanel, gbc_speedDisplayPanel);
		speedDisplayPanel.setLayout(new BoxLayout(speedDisplayPanel, BoxLayout.Y_AXIS));
		
		JLabel lblSpeedTitle = new JLabel("Speed");
		lblSpeedTitle.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblSpeedTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		speedDisplayPanel.add(lblSpeedTitle);
		
		lblSpeed = new JLabel("20");
		lblSpeed.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblSpeed.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblSpeed.setBackground(Color.WHITE);
		speedDisplayPanel.add(lblSpeed);
		
		JLabel lblKmh = new JLabel("KM/H");
		lblKmh.setAlignmentX(Component.CENTER_ALIGNMENT);
		speedDisplayPanel.add(lblKmh);
		
		if(mySession != null) { //NullPointerException protection. And now they'll show up in a 
								//WYSIWYG editor. 
		
		register();
		}
		//To make the labels show up better in the WYSYWIG, uncomment these 
		 //and comment out the defualt label values call. 
		/* 
		lblTempitem1.setText("NONE"); //first temp item
		lblTempitem_2_1.setText("Label2"); //2nd temp item
		lblTempitem_3_1.setText("Label3"); //3rd item in temperature list
		lblTempitem_4.setText("Label4"); //4th...
		lblTempitem_5.setText("label5"); //5th item in temperature list.
		lblPackv1.setText("NONE"); //first item in voltage pack list
		lblPackv2.setText("Label 2"); //second item in voltage pack list
		lblPackv_3.setText("Label 3"); //the third
		lblPackv_4.setText("Label 4"); //and fourth. Can add more as needed 
		lblSpeed.setText("NONE");; //the label showing speed (update this one!)
		lblLastDataReceived.setText("Last Received: NONE"); //Time of last data received
		lblCarLoadedName.setText("No Car Connected");; // the name of the currently loaded car.
		*/
		this.setLabelsTODefaultValues();
	}
	
	
	/*
	public void initialize(){
		JLabel lblCar = new JLabel("Car");
		lblCar.setHorizontalAlignment(SwingConstants.CENTER);
		super.add(lblCar);
	}*/
	
	
	/**
	 * Updates the appropriate labels in the panel. 
	 * NOTE should probably be caching these in field variables in case
	 * we ever want to calculate differences or something. 
	 * @param recentPacket - the newest dataPacket
	 */
	private void updateLabels(TelemDataPacket recentPacket){
	HashMap<String, Integer> temps = recentPacket.getTemperatures();
	ArrayList<String> temperatureLabels = extractTempLabels(temps);
	lblTempitem1.setText(temperatureLabels.get(0)); //first temp item
	lblTempitem_2_1.setText(temperatureLabels.get(1)); //2nd temp item
	lblTempitem_3_1.setText(temperatureLabels.get(2)); //3rd item in temperature list
	lblTempitem_4.setText(temperatureLabels.get(3)); //4th...
	lblTempitem_5.setText(temperatureLabels.get(4));//5th
	lblPackv1.setText("NONE"); //first item in voltage pack list
	lblPackv2.setText(""); //second item in voltage pack list
	lblPackv_3.setText(""); //the third
	lblPackv_4.setText(""); //and fourth. Can add more as needed 
	lblSpeed.setText(""+recentPacket.getSpeed());; //the label showing speed (update this one!)

	SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");
	Date now = new Date((long) (recentPacket.getTimeCreated()));
    String strDate = sdfDate.format(now);
	lblLastDataReceived.setText("Last Received: " + strDate); //Time of last data received
	}
	
	private ArrayList<String> extractTempLabels(HashMap<String, Integer> temps) {
		/*
		 //The following did it programatically, but I couldn't control the order that 
		 //they came out of the Set with (didn't want to bother with sorting alphabetically)
		 //TODO make this programmaticly dynamic when I make the generation of labels dynamic
		ArrayList<String> labels = new ArrayList<String>();
		Set mapSet = (Set) temps.entrySet();
		//Create iterator on Set 
        Iterator mapIterator = mapSet.iterator();
        System.out.println("Display the key/value of HashMap.");
        while (mapIterator.hasNext()) {
               Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                // getKey Method of HashMap access a key of map
                String keyValue = (String) mapEntry.getKey();
                //getValue method returns corresponding key's value
                String value = "" +  mapEntry.getValue();
                labels.add(keyValue + ": " + value);
        }*/
		
		ArrayList<String> labels = new ArrayList();
		System.out.println("BMS: " + temps.get("bms"));
		if(temps.get("bms") != null){
			labels.add("BMS: " + temps.get("bms"));
		}
		if(temps.get("motor") != null){
			labels.add("motor: " + temps.get("motor"));
		}
		if(temps.get("pack0") != null){
			labels.add("pack0: " + temps.get("pack0"));
		}
		if(temps.get("pack1") != null){
			labels.add("pack1: " + temps.get("pack1"));
		}
		if(temps.get("pack2") != null){
			labels.add("pack2: " + temps.get("pack2"));
		}
		if(temps.get("pack3") != null){
			labels.add("pack3: " + temps.get("pack3"));
		}
		return labels;
	}


	private void setLabelsTODefaultValues(){
		lblTempitem1.setText("NONE"); //first temp item
		lblTempitem_2_1.setText(""); //2nd temp item
		lblTempitem_3_1.setText(""); //3rd item in temperature list
		lblTempitem_4.setText(""); //4th...
		lblTempitem_5.setText(""); //5th item in temperature list.
		lblPackv1.setText("NONE"); //first item in voltage pack list
		lblPackv2.setText(""); //second item in voltage pack list
		lblPackv_3.setText(""); //the third
		lblPackv_4.setText(""); //and fourth. Can add more as needed 
		lblSpeed.setText("NONE");; //the label showing speed (update this one!)
		lblLastDataReceived.setText("Last Received: NONE"); //Time of last data received
		lblCarLoadedName.setText("No Car Connected");; // the name of the currently loaded car. 
	}
	
	
	@Override
	public void notify(Notification n) {
		if(n.getClass() == CarUpdateNotification.class) {
			try{
			updateLabels(((CarUpdateNotification) n).getDataPacket()); //for the CarSpeed label.
			}
			catch(Exception e){
				SolarLog.write(LogType.ERROR, System.currentTimeMillis(), e.getClass().getName() + 
						"error while updating the labels on the car panel");
				
			}
		}
		if(n.getClass() == NewCarLoadedNotification.class){
			String nameOfCar = ((NewCarLoadedNotification) n).getNameOfCar();
			this.lblCarLoadedName.setText(nameOfCar);
			//TODO add in support for a disconnected car (should clear the label values)
			/*
			 * if(nameOfCar is disconnected){
			 * 	setLabelsToDefaultValues();
			 * }
			 */
		}
		
	}
	
	private void showAdvanced(){
		this.parent.launchCarAdvancedWindow();
	}
	@Override
	public void register() {
		mySession.register(this,  CarUpdateNotification.class);		
		mySession.register(this,  NewCarLoadedNotification.class);
	}
}
