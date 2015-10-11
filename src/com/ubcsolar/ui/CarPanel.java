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
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0, 51, 0, 0, 0};
		gbl_panel.rowHeights = new int[]{14, 0};
		gbl_panel.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
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
		panel.add(btnSettings, gbc_btnSettings);
		
		JLabel lblCarTitle = new JLabel("CAR");
		lblCarTitle.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblCarTitle.setHorizontalAlignment(SwingConstants.CENTER);
		GridBagConstraints gbc_lblCarTitle = new GridBagConstraints();
		gbc_lblCarTitle.insets = new Insets(0, 0, 0, 5);
		gbc_lblCarTitle.gridx = 2;
		gbc_lblCarTitle.gridy = 0;
		panel.add(lblCarTitle, gbc_lblCarTitle);
		
		JButton btnDash = new JButton("Dash");
		GridBagConstraints gbc_btnDash = new GridBagConstraints();
		gbc_btnDash.insets = new Insets(0, 0, 0, 5);
		gbc_btnDash.gridx = 3;
		gbc_btnDash.gridy = 0;
		panel.add(btnDash, gbc_btnDash);
		
		JPanel panel_5 = new JPanel();
		GridBagConstraints gbc_panel_5 = new GridBagConstraints();
		gbc_panel_5.insets = new Insets(0, 0, 5, 0);
		gbc_panel_5.fill = GridBagConstraints.BOTH;
		gbc_panel_5.gridx = 0;
		gbc_panel_5.gridy = 1;
		add(panel_5, gbc_panel_5);
		GridBagLayout gbl_panel_5 = new GridBagLayout();
		gbl_panel_5.columnWidths = new int[]{0, 0, 0};
		gbl_panel_5.rowHeights = new int[]{0, 0};
		gbl_panel_5.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_panel_5.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel_5.setLayout(gbl_panel_5);
		
		lblLastDataReceived = new JLabel("Last Data Received: NONE");
		GridBagConstraints gbc_lblLastDataReceived = new GridBagConstraints();
		gbc_lblLastDataReceived.insets = new Insets(0, 0, 0, 5);
		gbc_lblLastDataReceived.gridx = 0;
		gbc_lblLastDataReceived.gridy = 0;
		panel_5.add(lblLastDataReceived, gbc_lblLastDataReceived);
		
		lblCarLoadedName = new JLabel("Car Loaded: NONE");
		GridBagConstraints gbc_lblCarLoadedName = new GridBagConstraints();
		gbc_lblCarLoadedName.gridx = 1;
		gbc_lblCarLoadedName.gridy = 0;
		panel_5.add(lblCarLoadedName, gbc_lblCarLoadedName);
		
		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 2;
		add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0, 76, 88, 68, 0, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0};
		gbl_panel_1.columnWeights = new double[]{1.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		JPanel panel_4 = new JPanel();
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.insets = new Insets(0, 0, 0, 5);
		gbc_panel_4.fill = GridBagConstraints.BOTH;
		gbc_panel_4.gridx = 1;
		gbc_panel_4.gridy = 0;
		panel_1.add(panel_4, gbc_panel_4);
		panel_4.setLayout(new BoxLayout(panel_4, BoxLayout.Y_AXIS));
		
		lblTempuratures = new JLabel("Temps");
		lblTempuratures.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblTempuratures.setHorizontalAlignment(SwingConstants.CENTER);
		lblTempuratures.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_4.add(lblTempuratures);
		
		lblTempitem1 = new JLabel("None received");
		lblTempitem1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblTempitem1.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_4.add(lblTempitem1);
		
		lblTempitem_2_1 = new JLabel("tempItem1");
		lblTempitem_2_1.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblTempitem_2_1.setHorizontalAlignment(SwingConstants.CENTER);
		panel_4.add(lblTempitem_2_1);
		
		lblTempitem_3_1 = new JLabel("TempItem 3");
		lblTempitem_3_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblTempitem_3_1.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_4.add(lblTempitem_3_1);
		
		lblTempitem_4 = new JLabel("TempItem5");
		lblTempitem_4.setHorizontalAlignment(SwingConstants.CENTER);
		lblTempitem_4.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_4.add(lblTempitem_4);
		
		JPanel panel_3 = new JPanel();
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.insets = new Insets(0, 0, 0, 5);
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.gridx = 2;
		gbc_panel_3.gridy = 0;
		panel_1.add(panel_3, gbc_panel_3);
		panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.Y_AXIS));
		
		JLabel lblCellVoltages = new JLabel("Avg Cell V");
		lblCellVoltages.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblCellVoltages.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_3.add(lblCellVoltages);
		
		lblPackv1 = new JLabel("Pack1: 23v");
		lblPackv1.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_3.add(lblPackv1);
		
		lblPackv2 = new JLabel("Pack 2: 322");
		lblPackv2.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_3.add(lblPackv2);
		
		lblPackv_3 = new JLabel("Pack 3: 10v");
		lblPackv_3.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_3.add(lblPackv_3);
		
		lblPackv_4 = new JLabel("Pack 4: 13v");
		lblPackv_4.setHorizontalAlignment(SwingConstants.CENTER);
		lblPackv_4.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_3.add(lblPackv_4);
		
		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 0, 5);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 3;
		gbc_panel_2.gridy = 0;
		panel_1.add(panel_2, gbc_panel_2);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.Y_AXIS));
		
		JLabel lblSpeedTitle = new JLabel("Speed");
		lblSpeedTitle.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblSpeedTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_2.add(lblSpeedTitle);
		
		lblSpeed = new JLabel("20");
		lblSpeed.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblSpeed.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblSpeed.setBackground(Color.WHITE);
		panel_2.add(lblSpeed);
		
		JLabel lblKmh = new JLabel("KM/H");
		lblKmh.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_2.add(lblKmh);
		
		if(mySession != null) { //NullPointerException protection. And now they'll show up in a 
								//WYSIWYG editor. 
		
		register();
		}
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
        }
		return labels;
	}


	private void setLabelsTODefaultValues(){
		lblTempitem1.setText("NONE"); //first temp item
		lblTempitem_2_1.setText(""); //2nd temp item
		lblTempitem_3_1.setText(""); //3rd item in temperature list
		lblTempitem_4.setText(""); //4th...
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
			updateLabels(((CarUpdateNotification) n).getDataPacket()); //for the CarSpeed label.
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
