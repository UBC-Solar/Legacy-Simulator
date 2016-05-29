/**
 * The Car subpanel on the main UI
 */
package com.ubcsolar.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.util.*;

import com.ubcsolar.Main.GlobalController;
import com.ubcsolar.common.Listener;
import com.ubcsolar.common.LogType;
import com.ubcsolar.common.SolarLog;
import com.ubcsolar.common.TelemDataPacket;
import com.ubcsolar.notification.CarUpdateNotification;
import com.ubcsolar.notification.ExceptionNotification;
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
import java.text.DecimalFormat;
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
	private JLabel lblTempitem_6;
	private JLabel lblPackv1; //first item in voltage pack list
	private JLabel lblPackv2; //second item in voltage pack list
	private JLabel lblPackv_3; //the third
	private JLabel lblPackv_4; //and fourth. Can add more as needed 
	private JLabel lblSpeed; //the label showing speed (update this one!)
	private JLabel lblLastDataReceived; //Time of last data received
	private JLabel lblCarLoadedName; // the name of the currently loaded car. 
	private ArrayList<Integer> voltageLabelKeys; //the keys for the voltage labels
	private ArrayList<String> temperatureLabelKeys; //the keys for the temperature labels
	private JLabel label;
	private JLabel label_1;
	private JLabel lblSoC; //title for state of charge
	private JLabel SoC; //state of charge value
	
	
	/**
	 * Most of the code in the constructor was done automatically by the WSYIWG I installed to Eclipse. 
	 * Mostly it's building things. 
	 * @param session the GUIMain for the program, in case I need to send a notification or something. 
	 * @param parent the parent UI object, mostly to pass things for jDialog and advanced window stuff
	 */
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
		
		lblTempuratures = new JLabel("Temps (\u00B0C)");
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
		
		lblTempitem_6 = new JLabel("TempItem6");
		lblTempitem_6.setHorizontalAlignment(SwingConstants.CENTER);
		lblTempitem_6.setAlignmentX(Component.CENTER_ALIGNMENT);
		temperatureDispPanel.add(lblTempitem_6);
		
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
		
		label_1 = new JLabel("_________");
		label_1.setAlignmentX(0.5f);
		speedDisplayPanel.add(label_1);
		
		lblSoC = new JLabel("SoC");
		lblSoC.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblSoC.setAlignmentX(Component.CENTER_ALIGNMENT);
		speedDisplayPanel.add(lblSoC);
		
		SoC = new JLabel("");
		SoC.setFont(new Font("Tahoma", Font.BOLD, 18));
		SoC.setAlignmentX(Component.CENTER_ALIGNMENT);
		speedDisplayPanel.add(SoC);
		
		label = new JLabel("");
		speedDisplayPanel.add(label);
		
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
		//TODO make the labels turn red in case of danger values (label.setForeground(Color.RED))
		
	/*
	 * The best way to do these labels would be to pull the keys
	 * out of the sets, and then sort alphabetically
	 * and add them to dynamically-generated and added
	 * labels. However, I don't expect the labels to change that
	 * much, and I don't want to add that much processing load
	 * to do on every received packet. So now the keys are 
	 * generated here, and the algo requests the exact keys that 
	 * are here, so if we ever update which keys we are using 
	 * in the Telem Data Packet, we'll need to update the list of keys
	 * here. This is a trade-off we could re-examine in the future. 
	 * 
	 * However, they're still displaying the exactly value returned by the keys
	 * so we don't have  to worry about showing the wrong number next to an 
	 * incorrect label
	 */
	if(voltageLabelKeys == null){
		voltageLabelKeys = generateVoltageLabelKeys(); //only generate this list once per program
	}
	if(temperatureLabelKeys == null){
		temperatureLabelKeys = generateTemperatureLabelKeys(); //also only generate this list once per program. 
	}
	HashMap<String, Integer> temps = recentPacket.getTemperatures(); //pull it out of the telemDataPack
	HashMap<Integer,ArrayList<Float>> voltages = recentPacket.getCellVoltages(); 
	
	//This ArrayList is the exact text to set the labels to, generated from the extractTempLabels method.
	//Will need to modify this to make any values appear red; currently the strings have the label and value
	//and no easy way to set a rule like "if x>100, set.red"
	ArrayList<String> temperatureLabels = extractTempLabels(temps); 
	
	lblTempitem1.setText(temperatureLabels.get(0)); //first temp item
	lblTempitem_2_1.setText(temperatureLabels.get(1)); //2nd temp item
	lblTempitem_3_1.setText(temperatureLabels.get(2)); //3rd item in temperature list
	lblTempitem_4.setText(temperatureLabels.get(3)); //4th...
	lblTempitem_5.setText(temperatureLabels.get(4));//5th
	lblTempitem_6.setText(temperatureLabels.get(4));
	
	//Calculate and display the average value of each pack voltage. 
	ArrayList<String> voltageLabels = new ArrayList();
	
	for(int i = 0; i<voltageLabelKeys.size(); i++){	
	if(voltages.get(voltageLabelKeys.get(i)) != null){
		ArrayList<Float> tempVoltages = voltages.get(voltageLabelKeys.get(i));
		float averageVolt = calculateAverage(tempVoltages);
		DecimalFormat decVal = new DecimalFormat("##.##"); //May need to change to ###.## if we're talking hundreds of volts
		voltageLabels.add("Pack" + voltageLabelKeys.get(i) + ": " + decVal.format(averageVolt)); 	
	}
	}
	
	//TODO should probably add these to a list or something. 
	lblPackv1.setText(voltageLabels.get(0)); //first item in volt-age pack list
	lblPackv2.setText(voltageLabels.get(1)); //second item in voltage pack list
	lblPackv_3.setText(voltageLabels.get(2)); //the third
	lblPackv_4.setText(voltageLabels.get(3)); //and fourth. Can add more as needed 

	//easy to update the speed.
	lblSpeed.setText(""+new DecimalFormat("###.##").format(recentPacket.getSpeed())); //the label showing speed to two decimal places (update this one!)

	SoC.setText(""+recentPacket.getStateOfCharge()+"%"); //the label showing the state of charge
		
	if (recentPacket.getStateOfCharge()<=10){
		SoC.setForeground(Color.red);
	}
	else{
		SoC.setForeground(Color.black);
	}
	SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");
	Date now = new Date((long) (recentPacket.getTimeCreated()));
    String strDate = sdfDate.format(now);
	lblLastDataReceived.setText("Last Received: " + strDate); //Time of last data received
	}
	
	/**
	 * This method is used to average the cell voltages in a pack. No limit on total number of items. 
	 * @param tempVoltages - the list of floats to average 
	 * @return - the average value. 
	 */
	private float calculateAverage(List<Float> tempVoltages) {
		
		float runningTotal = 0;
		for(float i : tempVoltages){
			runningTotal += i;
		}
		runningTotal /= tempVoltages.size();
		return runningTotal;
	}


	private ArrayList<String> generateTemperatureLabelKeys() {
		// TODO Generate the list of keys here (duct-tape work around until
		//I program them in dynmacially (what happens if we change a key?)
		return null;
	}

	/*
	 * This method made a lot more sense until I realized that the keys for the cell voltage arrays
	 * in the TelemDataPack are integers rather than Strings. If we change the number of 
	 * packs to display, will need to edit this. 
	 */
	private ArrayList<Integer> generateVoltageLabelKeys() {
		// duct tape workaround. I don't really want to hard-code the values in,
		//(what happens if Jason changes the name of a key in the TelemDataPacket?), 
		//but it works for now. If we anticipate changes, we can update this
		//to generate it dynamically. 
		ArrayList<Integer> keys = new ArrayList();
		keys.add(0);
		keys.add(1);
		keys.add(2);
		keys.add(3);
		return keys;
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
		
		
		//Current sensor labels as of Oct 17, 2015.
		ArrayList<String> labels = new ArrayList();
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

	/**
	 * Sets all labels to default values (first label to 'NONE', remainder to "" so they disappear. 
	 */
	private void setLabelsTODefaultValues(){
		lblTempitem1.setText("NONE"); //first temp item
		lblTempitem_2_1.setText(""); //2nd temp item
		lblTempitem_3_1.setText(""); //3rd item in temperature list
		lblTempitem_4.setText(""); //4th...
		lblTempitem_5.setText(""); //5th item in temperature list.
		lblTempitem_6.setText("");
		lblPackv1.setText("NONE"); //first item in voltage pack list
		lblPackv2.setText(""); //second item in voltage pack list
		lblPackv_3.setText(""); //the thirdf
		lblPackv_4.setText(""); //and fourth. Can add more as needed 
		lblSpeed.setText("NONE");; //the label showing speed (update this one!)
		lblLastDataReceived.setText("Last Received: NONE"); //Time of last data received
		lblCarLoadedName.setText("No Car Connected");; // the name of the currently loaded car. 
		SoC.setText("N/A"); //the default text for state of charge
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
				mySession.sendNotification(new ExceptionNotification(e, "Error updating the labels on the Car Panel"));
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
